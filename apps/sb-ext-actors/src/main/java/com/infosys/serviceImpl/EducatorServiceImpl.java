/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.serviceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;

import com.infosys.cassandra.CassandraOperation;
import com.infosys.exception.ApplicationLogicError;
import com.infosys.exception.BadRequestException;
import com.infosys.helper.ServiceFactory;
import com.infosys.service.EducatorService;
import com.infosys.util.LexConstants;
import com.infosys.util.LexJsonKey;
import com.infosys.util.Util;

@Service
public class EducatorServiceImpl implements EducatorService {
	private static CassandraOperation cassandraOperation = ServiceFactory.getInstance();
	private Util.DbInfo educatorsDb = Util.dbInfoMap.get(LexJsonKey.Educators_Table);
	private static final Integer FETCH_BATCH_SIZE = 2;
	// private static final Integer INSERT_BATCH_SIZE = 3;

	@Override
	public void addEducators(MultipartFile file) throws BadRequestException, ApplicationLogicError {

		// map of courseId to List of educators
		Map<String, List<String>> inputEducators = parseFile(file);
		Map<String, List<String>> currentBatchRecords = new HashMap<>();
		Integer batchCount = 0;
		for (Map.Entry<String, List<String>> entry : inputEducators.entrySet()) {
			currentBatchRecords.put(entry.getKey(), entry.getValue());
			batchCount++;
			if (batchCount == EducatorServiceImpl.FETCH_BATCH_SIZE) {
				insertInTable(findEducatorsToInsert(currentBatchRecords));
				batchCount = 0;
				currentBatchRecords = new HashMap<>();
			}
		}
		if (currentBatchRecords.size() > 0) {
			insertInTable(findEducatorsToInsert(currentBatchRecords));
		}
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> findEducatorsToInsert(Map<String, List<String>> currentInputMap) {

		Map<String, Object> propertiesMap = new HashMap<>();
		// fetch records for courseIds from input in bulk
		propertiesMap.put(LexConstants.SOURCE_ID, new ArrayList<>(currentInputMap.keySet()));
		Response batchFetchResponse = cassandraOperation.getRecordsByProperties(educatorsDb.getKeySpace(),
				educatorsDb.getTableName(), propertiesMap,
				Arrays.asList(LexConstants.SOURCE_ID,LexConstants.EDUCATOR_EMAIL));

		List<Map<String, String>> batchResult = (List<Map<String, String>>) batchFetchResponse.getResult()
				.get(JsonKey.RESPONSE);
		Map<String, List<String>> existingEducators = createAggregatedMap(batchResult);
		return createInsertMaps(currentInputMap, existingEducators);
	}

	// this is done for emails from db so that both input and current table response
	// follow same structure
	private Map<String, List<String>> createAggregatedMap(List<Map<String, String>> results) {

		Map<String, List<String>> aggregatedMap = new HashMap<>();
		for (Map<String, String> result : results) {
			String sourceId = result.get(LexConstants.SOURCE_ID);
			String educatorEmail = result.get(LexConstants.EDUCATOR_EMAIL);
			if (aggregatedMap.containsKey(sourceId)) {
				aggregatedMap.get(sourceId).add(educatorEmail);
			} else {
				aggregatedMap.put(sourceId, new ArrayList<>(Arrays.asList(educatorEmail)));
			}
		}
		return aggregatedMap;
	}

	private List<Map<String, Object>> createInsertMaps(Map<String, List<String>> mapFromInput,
			Map<String, List<String>> mapFromDb) {
		List<Map<String, Object>> emailsToInsertMaps = new ArrayList<>();
		for (Map.Entry<String, List<String>> inputEntry : mapFromInput.entrySet()) {
			if (mapFromDb.containsKey(inputEntry.getKey())) {
				List<String> emailsToInsert = removeEmailsAlreadyInDb(inputEntry.getValue(),
						mapFromDb.get(inputEntry.getKey()));
				emailsToInsertMaps.addAll(createInsertMapsUtil(inputEntry.getKey(), emailsToInsert));
			} else {
				emailsToInsertMaps.addAll(createInsertMapsUtil(inputEntry.getKey(), inputEntry.getValue()));
			}
		}
		return emailsToInsertMaps;
	}

	// create insert structure for cassandra dac
	private List<Map<String, Object>> createInsertMapsUtil(String sourceId, List<String> emails) {
		List<Map<String, Object>> responseMaps = new ArrayList<>();
		for (String email : emails) {
			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("source_id", sourceId);
			responseMap.put("educator_email", email);
			responseMap.put("date_created", new Date());
			responseMap.put("date_modified", new Date());
			responseMaps.add(responseMap);
		}
		return responseMaps;
	}

	// disregard email's already present in table
	private List<String> removeEmailsAlreadyInDb(List<String> emailsFromInput, List<String> emailsFromDb) {
		emailsFromInput.replaceAll(String::toLowerCase);
		emailsFromDb.replaceAll(String::toLowerCase);
		Set<String> emailsToInsert = new HashSet<>();
		for (String emailFromInput : emailsFromInput) {
			if (!emailsFromDb.contains(emailFromInput)) {
				emailsToInsert.add(emailFromInput);
			} else {
				System.out.println(emailFromInput + "  This email is already present");
			}
		}
		return new ArrayList<>(emailsToInsert);
	}

	private void insertInTable(List<Map<String, Object>> insertMaps) {

		cassandraOperation.batchInsert("bodhi", "educators", insertMaps);
		for (Map<String, Object> insertMap : insertMaps) {
			System.out.println(insertMap.toString());
		}
	}

	// check if email from input contains '@ad.' else adds it.
	private String checkEmailFormat(String email) {
		email = email.replace("\"", "");
		if (!email.contains("@ad.")) {
			email = email.replace("@", "@ad.");
		}
		return email;
	}

	private Map<String, List<String>> parseFile(MultipartFile file) {
		BufferedReader br;
		String line = "";
		String csvSplitBy = ",";
		Map<String, List<String>> completeFile = new HashMap<>();
		try {
			InputStream is = file.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				if (line.trim().equals("")) {
					continue;
				}
				line = line.trim();
				String row[] = line.split(csvSplitBy);
				List<String> rowFile = new ArrayList<>();
				String sourceId = row[0];// will serve as key
				for (int i = 1; i < row.length; ++i) {
					row[i] = checkEmailFormat(row[i]);
					rowFile.add(row[i]);
				}
				if (completeFile.containsKey(sourceId)) {
					throw new BadRequestException("Input file contains  id -> " + sourceId + " multiple times");
				}
				completeFile.put(sourceId, rowFile); //  id as key and educators emails are values as list<String>
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BadRequestException("Cannot parse input File");
		}
		return completeFile;
	}

	// batch insert code

	// public void insertInTable(List<Map<String, Object>> insertMaps) throws
	// Exception {
	// try {
	// if (insertMaps.size() == 0)
	// return;
	// if (insertMaps.size() <= INSERT_BATCH_SIZE) {
	// // cassandraOperation.batchInsert("bodhi", "educators", insertMaps);
	// return;
	// }
	// Integer iterations = insertMaps.size() / INSERT_BATCH_SIZE;
	// Integer remainderBatch = insertMaps.size() % INSERT_BATCH_SIZE;
	// Integer i = 0;
	// Integer startIndex = 0;
	// while (i < iterations) {
	// List<Map<String, Object>> currentBatch = insertMaps.subList(startIndex,
	// startIndex + INSERT_BATCH_SIZE);
	// System.out.println(currentBatch.toString());
	// // cassandraOperation.batchInsert("bodhi", "educators", currentBatch);
	// startIndex += INSERT_BATCH_SIZE;
	// ++i;
	// }
	// List<Map<String, Object>> lastBatch = insertMaps.subList(startIndex,
	// startIndex + remainderBatch);
	// if (lastBatch.size() != 0) {
	// System.out.println(lastBatch.toString());
	// // cassandraOperation.batchInsert("bodhi", "educators", lastBatch);
	// }
	// } catch (Exception exception) {
	// throw new Exception("Batch Insert Failed");
	// }
	// }

}

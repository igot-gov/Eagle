/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.exercise.util;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.infosys.lex.core.exception.ExerciseCodeVerifcationException;
import com.infosys.lex.exercise.dto.JavaVerificationAnalysis;

@Service
public class JavaVerificationUtilServiceImpl implements JavaVerificationUtilService {

	
	@Override
	public JSONObject analyseJavaVerificationResponse(JSONObject verificationToolResponse) throws JSONException {
		JSONObject responseData = new JSONObject();
		JavaVerificationAnalysis resultSummary = new JavaVerificationAnalysis();
		String tcStatus;
		Gson gson = new Gson();

		try {

			String statusCode = verificationToolResponse.getString("responseCode");
			String statusMessage = verificationToolResponse.getString("message");

			JSONObject resp = verificationToolResponse.getJSONObject("data");
			if (resp.isNull("structural") || !(resp.get("structural") instanceof JSONArray)) {
				throw new ExerciseCodeVerifcationException("Jave Eval Analyse Error : Structural field not a list");
			}
			// process structural testcase
			JSONArray structuralTestcases = resp.getJSONArray("structural");
			int totalStructuralTestcaseCount = structuralTestcases.length();
			JSONArray passedStructuralTestcases = new JSONArray();
			JSONArray failedStructuralTestcases = new JSONArray();
			int structuralTestcasesPassedCount;
			int structuralTestcasesFailedCount;
			int passedProceduralActualTC = 0;
			int failedProceduralActualTC = 0;
			int passedProceduralSampleTC = 0;
			int failedProceduralSampleTC = 0;
			JSONObject structuralTestcase;

			for (int index = 0; index < totalStructuralTestcaseCount; index++) {
				structuralTestcase = structuralTestcases.getJSONObject(index);
				if (structuralTestcase.isNull("tcstatus"))
					throw new ExerciseCodeVerifcationException(
							"Jave Eval Analyse Error : structural tcstatus is null or empty ");
				tcStatus = structuralTestcase.getString("tcstatus");

				if (tcStatus.equalsIgnoreCase("pass"))
					passedStructuralTestcases = passedStructuralTestcases.put(structuralTestcase);
				else
					failedStructuralTestcases = failedStructuralTestcases.put(structuralTestcase);

			}
			structuralTestcasesPassedCount = passedStructuralTestcases.length();
			structuralTestcasesFailedCount = failedStructuralTestcases.length();
			structuralTestcases = passedStructuralTestcases.put(failedStructuralTestcases);
			
			responseData.put("structural", structuralTestcases);

			if (resp.isNull("procedural") || !(resp.get("procedural") instanceof JSONArray)) {
				throw new ExerciseCodeVerifcationException(
						"Jave Eval Analyse Error : Structural field not a list or is null or empty");
			}

			// process procedural testcase

			JSONArray proceduralTestcases = resp.getJSONArray("procedural");
			int totalProceduralTestcaseCount = proceduralTestcases.length();
			JSONArray passedProceduralTestcases = new JSONArray();
			JSONArray failedProceduralTestcases = new JSONArray();
			JSONArray sampleProcederalTestcases = new JSONArray();
			JSONArray actualProceduralTestcases = new JSONArray();

			JSONObject proceduralTestcase;

			for (int index = 0; index < totalProceduralTestcaseCount; index++) {
				proceduralTestcase = proceduralTestcases.getJSONObject(index);
				if (proceduralTestcase.isNull("tcstatus"))
					throw new ExerciseCodeVerifcationException(
							"Jave Eval Analyse Error : procedural tcstatus is null or empty ");
				tcStatus = proceduralTestcase.getString("tcstatus");
				if (tcStatus.equalsIgnoreCase("pass"))
					passedProceduralTestcases.put(proceduralTestcase);
				else
					failedProceduralTestcases.put(proceduralTestcase);
			}
			int failedProceduralTestcaseCount = failedProceduralTestcases.length();
			int passedProceduralTestcaseCount = passedProceduralTestcases.length();
			int numberOfFailedTestCasesToShowInSample = 0;
			JSONArray finalProceduralTestcases = new JSONArray();

			// 30% of total number of logical test cases to be displayed as sample

			if (totalProceduralTestcaseCount > 0) {

				// 35% of total logical test cases to be shown as sample
				int maxToShow = (int) (Math.floor(totalProceduralTestcaseCount * 0.35));
				int sampleproceduralTestCount = maxToShow < 5 ? maxToShow : 5;

				// This is done when maxToShow/actualproceduralTestCount turns out to be 0. We
				// do ceil instead of floor
				if (sampleproceduralTestCount == 0) {
					sampleproceduralTestCount = (int) (Math.ceil(totalProceduralTestcaseCount * 0.35));
				}

				// now for actual show minimum of sample procedural test count and failed test
				// cases
				int actualproceduralTestCount = sampleproceduralTestCount < (totalProceduralTestcaseCount
						- passedProceduralTestcaseCount) ? sampleproceduralTestCount
								: (totalProceduralTestcaseCount - passedProceduralTestcaseCount);
				// actualproceduralTestCount = 2;

				// if all procedural test case pass
				if (passedProceduralTestcaseCount == totalProceduralTestcaseCount) {
					if (totalProceduralTestcaseCount <= 5) {
						sampleproceduralTestCount = 1;
						actualproceduralTestCount = 1;
					} else if (totalProceduralTestcaseCount > 5 && totalProceduralTestcaseCount <= 10) {
						sampleproceduralTestCount = 2;
						actualproceduralTestCount = 2;
					} else if (totalProceduralTestcaseCount > 10) {
						sampleproceduralTestCount = 5;
						actualproceduralTestCount = 5;
					}
				}

				// if all test cases fail and number of logical test cases exceed 10 then
				// display only seven failed test cases.
				// Modified sample and actual count.
				// (Here actualproceduralTestCount and sampleproceduralTestcount is equated with
				// 5
				// because their count will remain same and their sum should be equal t0 10
				if (passedProceduralTestcaseCount == 0 && actualproceduralTestCount == 5
						&& sampleproceduralTestCount == 5) {
					sampleproceduralTestCount = 2;
					actualproceduralTestCount = 5;
				}

				// if number of test cases exceeds sample test cases to be shown, then include
				// one failed test case in sample.
				if (sampleproceduralTestCount < failedProceduralTestcaseCount && passedProceduralTestcaseCount > 0
						&& sampleproceduralTestCount > 1) {
					numberOfFailedTestCasesToShowInSample = 1;
				}

				// This is the index from which passed testcases are added to actual procedural
				// testcases
				int actualPassedProceduralTestcaseIndex = 0;

				for (int i = 0; i <= passedProceduralTestcaseCount; i++) {

					JSONObject passedProceduralTestcase = passedProceduralTestcases.getJSONObject(i);
					sampleProcederalTestcases.put(passedProceduralTestcase);
					actualPassedProceduralTestcaseIndex++;
					// add

					if (sampleProcederalTestcases
							.length() == (sampleproceduralTestCount - numberOfFailedTestCasesToShowInSample))
						break;
				}
				
				//set the count of passed procedural testcases that are shown as sample
				passedProceduralSampleTC = actualPassedProceduralTestcaseIndex;
				
				// This is the index from which failed testcases are added to ACTUAL procedural
				// testcases
				// This is incremented when some of the failed testcases are shown in sample
				int actualFailedProceduralTestcaseIndex = 0;

				// here the failed testcases are added to sample
				if (numberOfFailedTestCasesToShowInSample != 0) {
					for (int index = 0; index < numberOfFailedTestCasesToShowInSample; index++) {
						sampleProcederalTestcases.put(failedProceduralTestcases.get(index));
						actualFailedProceduralTestcaseIndex++;
					}
				}

				//store the count of failed sample testcase
				failedProceduralSampleTC = actualFailedProceduralTestcaseIndex;
				
				
				int serialNo = 0;
				serialNo = this.formatJavaProceduralTestcases(sampleProcederalTestcases, "Sample", serialNo,
						finalProceduralTestcases);

				if (actualproceduralTestCount > 0) {
					// failed procedural testcase which are not part of sample testcase are added
					// count of which should be less than or equal to the actual testcases that are
					// to be shown
					for (int index = actualFailedProceduralTestcaseIndex; index < failedProceduralTestcaseCount; index++) {
						actualProceduralTestcases.put(failedProceduralTestcases.get(index));
						if (actualProceduralTestcases.length() == actualproceduralTestCount)
							break;
					}

					// if the actual testcases to be shown are still less the that to be shown then
					// the passed testcases are added
					while (actualProceduralTestcases.length() < actualproceduralTestCount) {
						actualProceduralTestcases
								.put(passedProceduralTestcases.get(actualPassedProceduralTestcaseIndex));
						actualPassedProceduralTestcaseIndex++;
					}
				}
				
				//store the count of passed actual testcases
				passedProceduralActualTC = actualPassedProceduralTestcaseIndex - passedProceduralSampleTC;
				
				// store the count of failed actual testcases
				failedProceduralActualTC = actualFailedProceduralTestcaseIndex - failedProceduralSampleTC;

				serialNo = this.formatJavaProceduralTestcases(actualProceduralTestcases, "Actual", serialNo,
						finalProceduralTestcases);

			}
			
			resultSummary.setFailedProceduralActualTC(failedProceduralActualTC);
			resultSummary.setFailedProceduralSampleTC(failedProceduralSampleTC);
			resultSummary.setPassedProceduralActualTC(passedProceduralActualTC);
			resultSummary.setPassedProceduralSampleTC(passedProceduralSampleTC);
			resultSummary.setPassedStructuralTC(structuralTestcasesPassedCount);
			resultSummary.setFailedStructuralTC(structuralTestcasesFailedCount);
			

			resultSummary.setResponseCode(statusCode);
			resultSummary.setResponse(statusMessage);

			responseData.put("resultSummary", new JSONObject(gson.toJson(resultSummary)));
			responseData.put("codeQuality", resp.getJSONObject("codeQuality"));
			responseData.put("structural", structuralTestcases);
			responseData.put("procedural", finalProceduralTestcases);

		} catch (Exception e) {
			// this initializes all the properties to 0
			resultSummary = new JavaVerificationAnalysis();
			resultSummary.setResponseCode("500");
			resultSummary.setResponse("Error parsing the verification Tool response");
			responseData.put("resultSummary", new JSONObject(gson.toJson(resultSummary)));
			responseData.put("codeQuality", new JSONArray());
			responseData.put("structural", new JSONArray());
			responseData.put("procedural", new JSONArray());
		}
		return responseData;
	}

	
	/*
	 * This method formats the procedural testcases and to result data. If the testcase is actual then the
	 * expected output is not shown.
	 */
	private int formatJavaProceduralTestcases(JSONArray proceduralTestcases, String type, int serialNo,
			JSONArray resultData) throws JSONException {
		for (int i = 0; i < proceduralTestcases.length(); i++) {
			JSONObject proceduralTestcase = proceduralTestcases.getJSONObject(i);
			
			proceduralTestcase.put("sNo", serialNo++);

			proceduralTestcase.put("type", type);
			//actual testcases are not shown
			if (type.equalsIgnoreCase("actual"))
				proceduralTestcase.put("tcexpected", null);

		}
		resultData.put(proceduralTestcases);
		return serialNo;
	}

	/**
	 * This method analyses the verification response. This method doesn't filter
	 * the testcases that are to be displayed.
	 * 
	 */
	@Override
	public JSONObject analyseJavaVerificationResponseForPreview(JSONObject resp) throws JSONException {
		JSONObject responseData = new JSONObject();
		JavaVerificationAnalysis resultSummary = new JavaVerificationAnalysis();
		String tcStatus;
		Gson gson = new Gson();
		try {

			String statusCode = resp.getString("responseCode");
			String statusMessage = resp.getString("message");
			if (resp.isNull("structural") || !(resp.get("structural") instanceof JSONArray)) {
				throw new ExerciseCodeVerifcationException("Jave Eval Analyse Error : Structural field not a list");
			}

			// process structural testcase
			JSONArray structuralTestcases = resp.getJSONArray("structural");
			int totalStructuralTestcaseCount = structuralTestcases.length();
			JSONArray passedStructuralTestcases = new JSONArray();
			JSONArray failedStructuralTestcases = new JSONArray();
			int structuralTestcasesPassedCount;
			int structuralTestcasesFailedCount;
			JSONObject structuralTestcase;

			for (int index = 0; index < totalStructuralTestcaseCount; index++) {
				structuralTestcase = structuralTestcases.getJSONObject(index);
				if (structuralTestcase.isNull("tcstatus"))
					throw new ExerciseCodeVerifcationException(
							"Jave Eval Analyse Error : structural tcstatus is null or empty ");
				tcStatus = structuralTestcase.getString("tcStatus");

				if (tcStatus.equalsIgnoreCase("pass"))
					passedStructuralTestcases = passedStructuralTestcases.put(structuralTestcase);
				else
					failedStructuralTestcases = failedStructuralTestcases.put(structuralTestcase);

			}
			structuralTestcasesPassedCount = passedStructuralTestcases.length();
			structuralTestcasesFailedCount = failedStructuralTestcases.length();
			structuralTestcases = passedStructuralTestcases.put(failedStructuralTestcases);
			responseData.put("structural", structuralTestcases);

			if (resp.isNull("procedural") || !(resp.get("procedural") instanceof JSONArray)) {
				throw new ExerciseCodeVerifcationException(
						"Jave Eval Analyse Error : Structural field not a list or is null or empty");
			}

			// process procedural testcase

			JSONArray proceduralTestcases = resp.getJSONArray("procedural");
			int totalProceduralTestcaseCount = proceduralTestcases.length();
			JSONArray passedProceduralTestcases = new JSONArray();
			JSONArray failedProceduralTestcases = new JSONArray();

			JSONObject proceduralTestcase;

			for (int index = 0; index < totalProceduralTestcaseCount; index++) {
				proceduralTestcase = proceduralTestcases.getJSONObject(index);
				if (proceduralTestcase.isNull("tcstatus"))
					throw new ExerciseCodeVerifcationException(
							"Jave Eval Analyse Error : procedural tcstatus is null or empty ");
				tcStatus = proceduralTestcase.getString("tcstatus");
				if (tcStatus.equalsIgnoreCase("pass"))
					passedProceduralTestcases.put(proceduralTestcase);
				else
					failedProceduralTestcases.put(proceduralTestcase);
			}
			int failedProceduralTestcaseCount = failedProceduralTestcases.length();
			int passedProceduralTestcaseCount = passedProceduralTestcases.length();
			JSONArray finalProceduralTestcases = new JSONArray();

			int serialNo = 0;
			if (totalProceduralTestcaseCount > 0) {
				serialNo = this.formatJavaProceduralTestcases(proceduralTestcases, "Preview", serialNo,
						finalProceduralTestcases);

			}

			
			
			resultSummary.setPassedStructuralTC(structuralTestcasesPassedCount);
			resultSummary.setFailedStructuralTC(structuralTestcasesFailedCount);
			resultSummary.setFailedProceduralActualTC(failedProceduralTestcaseCount);
			resultSummary.setPassedProceduralActualTC(passedProceduralTestcaseCount);
			resultSummary.setResponseCode(statusCode);
			resultSummary.setResponse(statusMessage);
			

			responseData.put("resultSummary", gson.toJson(resultSummary));
			responseData.put("codeQuality", resp.getJSONObject("codeQuality"));
			responseData.put("structural", structuralTestcases);
			responseData.put("procedural", finalProceduralTestcases);

		} catch (Exception e) {
			// this initializes all the properties to 0
			resultSummary = new JavaVerificationAnalysis();
			responseData.put("resultSummary", gson.toJson(resultSummary));
			responseData.put("codeQuality", new JSONArray());
			responseData.put("structural", new JSONArray());
			responseData.put("procedural", new JSONArray());
		}
		return responseData;
	}

}

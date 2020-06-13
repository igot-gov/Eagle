package com.infosys.lexauthoringservices.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.exception.ApplicationLogicError;
import com.infosys.lexauthoringservices.exception.BadRequestException;
import com.infosys.lexauthoringservices.model.cassandra.OrdinalsModel;
import com.infosys.lexauthoringservices.model.cassandra.OrdinalsPrimaryKeyModel;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.OrdinalsRepository;
import com.infosys.lexauthoringservices.service.OrdinalsService;
import com.infosys.lexauthoringservices.util.LexConstants;

@Service
public class OrdinalsServiceImpl implements OrdinalsService{

	@Autowired
	OrdinalsRepository ordinalsRepo;
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getEnums(String rootOrg) throws Exception {
		Map<String,Object> responseMap = new HashMap<>();
		List<Map<String,Object>> tempList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<OrdinalsModel> masterValues = (List<OrdinalsModel>) ordinalsRepo.findByRootOrg(rootOrg);
			if (masterValues == null || masterValues.isEmpty()) {
				throw new ApplicationLogicError("Requested Master Values for rootOrg : " + rootOrg + " could not be found");
			}
			for(OrdinalsModel masterValue:masterValues) {
				String entity = masterValue.getOrdinalsPrimaryKeyModel().getEntity();
				List<String> values = masterValue.getValues();
				Boolean stringify = masterValue.getStringify();
				if(stringify) {
					Map<String, Object> map = new HashMap<>();
					for(String value:values) {
						 map = mapper.readValue(value, Map.class);
						 tempList.add(map);
					}
					responseMap.put(entity, tempList);
					tempList=new ArrayList<>();
				}
				else {
					responseMap.put(entity, values);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return responseMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String upsertMasterValue(Map<String, Object> requestMap) throws Exception {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String rootOrg = (String) requestMap.get(LexConstants.ROOT_ORG);
			String entity = (String) requestMap.get(LexConstants.ENTITY);
			List<Object> masterValues = (List<Object>) requestMap.get(LexConstants.VALUE);
			Boolean stringify = (Boolean) requestMap.get("stringify");
			List<String> insertValue = new ArrayList<>();
			if(rootOrg==null || rootOrg.isEmpty() || entity==null || entity.isEmpty() || masterValues==null || masterValues.isEmpty() || stringify==null) {
				throw new BadRequestException("Incorrect values passed");
			}
			
			if(stringify) {
				for(Object value : masterValues) {
					if(!(value instanceof Map)) {
						throw new BadRequestException("Value is not a Map cannot stringify : " + value.toString() );
					}
					String val = mapper.writeValueAsString(value);
					insertValue.add(val);
				}
			}
			else {
				for(Object value : masterValues) {
					if(!(value instanceof String)) {
						throw new BadRequestException("Value is not a String, incorrect input");
					}
					insertValue.add((String) value);
					Collections.sort(insertValue);
				}
			}
			
			Date today = new Date();
			OrdinalsPrimaryKeyModel primaryKey = new OrdinalsPrimaryKeyModel(rootOrg, entity);
			OrdinalsModel model = new OrdinalsModel(today, insertValue,stringify, primaryKey);
			ordinalsRepo.save(model);
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String updateValueToEntity(Map<String, Object> requestMap) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		String rootOrg = (String) requestMap.get("rootOrg");
		String entity = (String) requestMap.get("entity");
		Boolean add = (Boolean) requestMap.get("toAdd");
		Boolean remove = (Boolean) requestMap.get("toRemove");
		List<Object> valuesToAdd = (List<Object>) requestMap.get("valuesToAdd");
		List<Object> valuesToRemove = (List<Object>) requestMap.get("valuesToRemove");
		
		if(rootOrg==null||rootOrg.isEmpty()||entity==null||entity.isEmpty()||add==null||remove==null||valuesToAdd==null||valuesToRemove==null) {
			throw new BadRequestException("Invalid request");
		}
		
		//getting data for entity
		OrdinalsModel table_row = ordinalsRepo.findByRootOrgEntity(rootOrg, entity);
		
		//if value is to be added
		if(add==true) {
			if(valuesToAdd==null||valuesToAdd.isEmpty()) {
				throw new BadRequestException("No values to add");
			}
			
			//if row is supposed to be stringified
			Boolean isStringify = table_row.getStringify();
			if(isStringify) {
				
				//verifying if value to be added is a valid Map
				for(Object valueToAdd : valuesToAdd) {
					if(!(valueToAdd instanceof Map)) {
						throw new BadRequestException("Cannot add to table because values are supposed to be (Map) stringified");
					}
				}
				
				//removing duplicate values from existing data
				List<String> existingValues = table_row.getValues();
				for(String existingValue:existingValues) {
					Map<String,Object> map = mapper.readValue(existingValue, Map.class);
					if(valuesToAdd.contains(map)) {
						valuesToAdd.remove(map);
					}
				}
				
				//merging new value with existing values before insertion
				List<String> insertValues = existingValues;
				for(Object valueToAdd:valuesToAdd) {
					String val = mapper.writeValueAsString(valueToAdd);
					insertValues.add(val);
				}
				//merged data inserted into table
				Date today = new Date();
				OrdinalsPrimaryKeyModel primaryKey = new OrdinalsPrimaryKeyModel(rootOrg, entity);
				OrdinalsModel model = new OrdinalsModel(today, insertValues,isStringify, primaryKey);
				ordinalsRepo.save(model);
			}
			
			//if value to be added is a normal string
			else {
				
				//verifying if value to be added is a valid string
				for(Object valueToAdd : valuesToAdd) {
					if(!(valueToAdd instanceof String)) {
						throw new BadRequestException("Cannot add to table because values are supposed to be String");
					}
				}
				
				//removing duplicate values from existing data
				List<String> existingValues = table_row.getValues();
				for(String existingValue:existingValues) {
					if(valuesToAdd.contains(existingValue)) {
						valuesToAdd.remove(existingValue);
					}
				}
				
				//merging new value with existing values before insertion
				List<String> insertValues = existingValues;
				for(Object valueToAdd:valuesToAdd) {
					insertValues.add((String) valueToAdd);
				}
				
				Collections.sort(insertValues);
				//merged data inserted into table
				Date today = new Date();
				OrdinalsPrimaryKeyModel primaryKey = new OrdinalsPrimaryKeyModel(rootOrg, entity);
				OrdinalsModel model = new OrdinalsModel(today, insertValues,isStringify, primaryKey);
				ordinalsRepo.save(model);
				
			}
		}
		
		//if value is to be removed
		if(remove==true) {
			if(valuesToRemove==null||valuesToRemove.isEmpty()) {
				throw new BadRequestException("No values to remove");
			}
			
			Boolean isStringify = table_row.getStringify();
			if(isStringify) {
				
				//verifying if value to be removed is a valid Map
				for(Object valueToRemove:valuesToRemove) {
					if(!(valueToRemove instanceof Map)) {
						throw new BadRequestException("Cannot remove from table because values are supposed to be (Map) stringified");
					}
				}
				
				//removing values that are not present in existing data
				List<String> existingValues = table_row.getValues();
				for(String existingValue: existingValues) {
					if(!(valuesToRemove.contains(existingValue))) {
						valuesToRemove.remove(existingValue);
					}
				}
				
				//final set of data that is to be updated is formed
				List<String> insertValues = existingValues;
				for(Object valueToRemove:valuesToRemove) {
					insertValues.remove(valueToRemove);
				}
				
				//data after removal is inserted into table
				Date today = new Date();
				OrdinalsPrimaryKeyModel primaryKey = new OrdinalsPrimaryKeyModel(rootOrg, entity);
				OrdinalsModel model = new OrdinalsModel(today, insertValues,isStringify, primaryKey);
				ordinalsRepo.save(model);
			}
			
			else {
				
				//verifying if values to be removed 
				for(Object valueToRemove : valuesToRemove) {
					if(!(valueToRemove instanceof String)) {
						throw new BadRequestException("Cannot remove from table because values are supposed to be String");
					}
				}
				
				//removing values that are not present in existing data
				List<String> existingValues = table_row.getValues();
				for(String existingValue: existingValues) {
					if(!(valuesToRemove.contains(existingValue))) {
						valuesToRemove.remove(existingValue);
					}
				}
				
				//merging new value with existing values before insertion
				List<String> insertValues = existingValues;
				for(Object valueToRemove:valuesToRemove) {
					insertValues.remove(valueToRemove);
				}
				
				//data after removal is inserted into table
				Collections.sort(insertValues);
				Date today = new Date();
				OrdinalsPrimaryKeyModel primaryKey = new OrdinalsPrimaryKeyModel(rootOrg, entity);
				OrdinalsModel model = new OrdinalsModel(today, insertValues,isStringify, primaryKey);
				ordinalsRepo.save(model);
			}
		}
		
		return null;
	}
	
}

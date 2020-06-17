package com.infosys.lexauthoringservices.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.exception.BadRequestException;
import com.infosys.lexauthoringservices.model.cassandra.ConfigurableContentHierarchyModel;
import com.infosys.lexauthoringservices.model.cassandra.ConfigurableContentHierarchyPrimaryKeyModel;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.ConfigurableContentHierarchyRepository;
import com.infosys.lexauthoringservices.service.ConfigurableContentHierarchyService;
import com.infosys.lexauthoringservices.util.LexConstants;

@Service
public class ConfigurableContentHierarchyServiceImpl implements ConfigurableContentHierarchyService {

	@Autowired
	ConfigurableContentHierarchyRepository cassandraRepository;
	
	@Autowired
	ObjectMapper mapper;
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public List<Map<String, Object>> getAllContentHierarchy(String rootOrg)
			throws BadRequestException, Exception {
		
		List<Map<String,Object>> resultList = new ArrayList<>();
		
		List<ConfigurableContentHierarchyModel> raw_data = (List<ConfigurableContentHierarchyModel>) cassandraRepository.findAllByRootOrg(rootOrg);
		
		for(ConfigurableContentHierarchyModel data:raw_data) {
			
			Map<String,Object> resultMap = new HashMap<>();
			Map<String,Object> conditionMap = new HashMap<>();
			ConfigurableContentHierarchyPrimaryKeyModel primaryKeyData = data.getConfigurableContentHierarchyPrimaryKeyModel();
			String positions = data.getPosition();
			String condition = data.getCondition();
			System.out.println(condition.isEmpty());
			
			if(condition!=null && !condition.isEmpty()) {
				conditionMap = mapper.readValue(condition, Map.class);
			}
			
			resultMap.put("childContentType", primaryKeyData.getChildContentType());
			resultMap.put(LexConstants.CONTENT_TYPE, primaryKeyData.getContentType());
			resultMap.put(LexConstants.ROOT_ORG, primaryKeyData.getRootOrg());
			resultMap.put("position", positions);
			resultMap.put("condition", conditionMap);
			
			resultList.add(resultMap);
		}
		
		return resultList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getContentHierarchyForContentType(String rootOrg, String contentType)
			throws BadRequestException, Exception {
		
		List<Map<String,Object>> resultList = new ArrayList<>();
		
		
		if(contentType==null||contentType.isEmpty()) {
			throw new BadRequestException("contentType is invalid");
		}
		
		List<ConfigurableContentHierarchyModel> raw_data = cassandraRepository.findById(rootOrg, contentType);
		
		if(raw_data==null) {
			throw new BadRequestException("No data in table for rootOrg,ContentType : " + rootOrg + " " +contentType );
		}
		
		for(ConfigurableContentHierarchyModel data:raw_data) {
			
			Map<String,Object> resultMap = new HashMap<>();
			ConfigurableContentHierarchyPrimaryKeyModel primaryKeyData = data.getConfigurableContentHierarchyPrimaryKeyModel();
			
			String condition = data.getCondition();
			String position = data.getPosition();
			
			Map<String,Object> conditionMap = new HashMap<>();
			
			if(condition!=null && !condition.isEmpty()) {
				conditionMap = mapper.readValue(condition, Map.class);
			}
			
			resultMap.put("childContentType", primaryKeyData.getChildContentType());
			resultMap.put(LexConstants.CONTENT_TYPE, primaryKeyData.getContentType());
			resultMap.put(LexConstants.ROOT_ORG, primaryKeyData.getRootOrg());
			resultMap.put("position", position);
			resultMap.put("condition", conditionMap);
			
			resultList.add(resultMap);
		}
		
		return resultList;
	}
	
	
	

}

/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.filters.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.filters.dto.FieldDTO;
import com.infosys.lex.filters.dto.FiltersUpsertDTO;
import com.infosys.lex.filters.dto.ValueDTO;
import com.infosys.lex.filters.postgredb.entity.TranslatedField;
import com.infosys.lex.filters.postgredb.entity.TranslatedValue;
import com.infosys.lex.filters.postgredb.entity.TranslatedValuePrimaryKey;
import com.infosys.lex.filters.postgredb.projection.FieldValueProjection;
import com.infosys.lex.filters.postgredb.repo.FiltersFieldRepo;
import com.infosys.lex.filters.postgredb.repo.FiltersValueRepo;

@Service
public class FiltersServiceImpl implements FiltersService {

	@Autowired
	FiltersFieldRepo filtersFieldRepo;
	
	@Autowired
	FiltersValueRepo filtersValueRepo;

	public static final String DEFAULT_ROOT_ORG = "Infosys";
	public static final String DEFAULT_ORG = "Infosys Ltd";
	public static final String DEFAULT_LANG = "en";
	public static final String ROOT_ORG = "rootOrg";
	public static final String ORG = "org";
	public static final String LANGUAGE = "language";
	public static final String TRANSLATED_FIELD = "translatedField";
	public static final String VALUE = "value";

	@Override
	public Map<String, Object> getAllFiltersAndValues(String rootOrg, String org, String language) throws Exception {
		
		Map<String, Object> response = new HashMap<>();
		response.put(ROOT_ORG, rootOrg);
		response.put(ORG, org);
		response.put(LANGUAGE, language);
		
		List<FieldValueProjection> fieldAndValues = filtersFieldRepo.getAllFilterFieldAndValues(DEFAULT_ROOT_ORG, DEFAULT_ORG, DEFAULT_LANG);
		
		for (FieldValueProjection fieldValue : fieldAndValues) {
			createMapFromProjection(response, fieldValue);
		}
		
		fieldAndValues = filtersFieldRepo.getAllFilterFieldAndValues(rootOrg, org, language);
		
		for (FieldValueProjection fieldValue : fieldAndValues) {
			createMapFromProjection(response, fieldValue);
		}
		
		return response;
	}

	@Override
	public Map<String, Object> postAllFiltersAndValues(String rootOrg, String org, FiltersUpsertDTO filtersDTO)
			throws Exception {
		
		String language = filtersDTO.getLanguage();
		
		List<FieldDTO> fieldDTOs = filtersDTO.getData();
		
		if (fieldDTOs == null || fieldDTOs.isEmpty()) {
			throw new BadRequestException("Please send the proper request");
		}
		
		List<TranslatedField> translatedFields = new ArrayList<TranslatedField>();
		
		List<TranslatedValue> translatedValues = new ArrayList<TranslatedValue>();
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		result.put("messasge", "success");
		
		for (FieldDTO fieldDTO : fieldDTOs) {
			
			if (fieldDTO.getField() == null) {
				throw new BadRequestException("Please send the field filter");
			}
			
			TranslatedField translatedField = filtersFieldRepo.findByRootorgAndOrgAndLangAndField(rootOrg, org, language, fieldDTO.getField());
			
			String fieldId;
			
			if (translatedField == null) {
				fieldId = UUID.randomUUID().toString();
				if (fieldDTO.getTranslatedField() != null) {
					translatedField = new TranslatedField(fieldId, rootOrg, org, language, fieldDTO.getField(), fieldDTO.getTranslatedField());					
				} else {
					translatedField = new TranslatedField(fieldId, rootOrg, org, language, fieldDTO.getField(), fieldDTO.getField());
				}
			} else {
				fieldId = translatedField.getId();
				if (fieldDTO.getTranslatedField() != null) {
					translatedField.setTranslatedField(fieldDTO.getTranslatedField());
				}
			}
			
			translatedFields.add(translatedField);
			
			List<ValueDTO> valueDTOs = fieldDTO.getValues();
			
			if (valueDTOs == null || valueDTOs.isEmpty()) {
				continue;
			}
			
			for (ValueDTO valueDTO : valueDTOs) {	
				TranslatedValue translatedValue = new TranslatedValue(new TranslatedValuePrimaryKey(valueDTO.getValue(), fieldId), valueDTO.getTranslatedValue());
				translatedValues.add(translatedValue);
			}
		}
		
		filtersFieldRepo.saveAll(translatedFields);
		filtersValueRepo.saveAll(translatedValues);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public void createMapFromProjection(Map<String, Object> response, FieldValueProjection fieldValue) {
		if (!response.containsKey(fieldValue.getField())) {
			response.put(fieldValue.getField(), new HashMap<>());
			((Map<String, Object>) response.get(fieldValue.getField())).put(VALUE, new HashMap<>());
		}
		if (fieldValue.getValue() != null) {
			((Map<String, Object>) ((Map<String, Object>) response.get(fieldValue.getField())).get(VALUE))
					.put(fieldValue.getValue(), fieldValue.getTranslatedValue());
		}
		((Map<String, Object>) response.get(fieldValue.getField())).put(TRANSLATED_FIELD,
				fieldValue.getTranslatedField());
	}
}

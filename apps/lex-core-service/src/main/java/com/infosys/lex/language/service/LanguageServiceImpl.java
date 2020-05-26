/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.language.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.utils.UUIDs;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.language.entity.LanguageMaster;
import com.infosys.lex.language.entity.LanguageMasterKey;
import com.infosys.lex.language.entity.LanguageTenant;
import com.infosys.lex.language.postgredb.repo.LanguageMasterRepo;
import com.infosys.lex.language.postgredb.repo.LanguageTenantRepo;
import com.infosys.lex.language.projection.LanguageProjection;

@Service
public class LanguageServiceImpl implements LanguageService {
	
	public static final String ROOT_ORG = "root_org";
	public static final String FLAG_URL = "flag_url";
	public static final String LANGUAGE = "language";
	public static final String COUNTRY_USER_FRIENDLY = "country_user_friendly";
	public static final String LANGUAGE_USER_FRIENDLY = "language_user_friendly";
	public static final String COUNTRY = "country";
	public static final String LOCALES = "locales";
	
	
	@Autowired
	LanguageTenantRepo languageTenantRepo;

	@Autowired
	LanguageMasterRepo languageMasterRepo;

	/* (non-Javadoc)
substitute url based on requirement
	 */
	@Override
	public Map<String, Object> getLanguages(String rootOrg, String lang,String country) throws Exception {

		LanguageProjection languageProjection = languageTenantRepo.findRecord(rootOrg, lang,country);
		if (languageProjection == null) {
			return new HashMap<String, Object>();
		}
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put(ROOT_ORG, languageProjection.getrootOrg());
		hashMap.put(LANGUAGE, languageProjection.getlanguage());
		if (languageProjection.getflagUrl() != null) {
			hashMap.put(FLAG_URL, languageProjection.getflagUrl());
		}
		if (languageProjection.getcountry() != null) {
			hashMap.put(COUNTRY, languageProjection.getcountry());
		}
		if (languageProjection.getcountryUserFriendly() != null) {
			hashMap.put(COUNTRY_USER_FRIENDLY, languageProjection.getcountryUserFriendly());
		}

		if (languageProjection.getlanguageUserFriendly() != null) {
			hashMap.put(LANGUAGE_USER_FRIENDLY, languageProjection.getlanguageUserFriendly());
		}
		if (languageProjection.getlocales() != null) {
			String[] locales = languageProjection.getlocales().split(",");
			hashMap.put(LOCALES, locales);
		}
		return hashMap;
	}

	/* (non-Javadoc)
substitute url based on requirement
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void createLanguages(String rootOrg, String lang, Map<String, Object> languages) {

		LanguageTenant languageTenant = new LanguageTenant(rootOrg, lang);
		// check if country is not empty
		if (languages.get(COUNTRY) == null || languages.get(COUNTRY).toString().isEmpty()) {
			throw new InvalidDataInputException("invalid.country");
		}

		LanguageTenant languageRecord = languageTenantRepo.findTenantRecord(rootOrg, lang);
		if (languageRecord != null) {
			throw new InvalidDataInputException("already.present");
		}
		LanguageMaster languageMaster = new LanguageMaster();
		languageMaster.setLanguageTenantKey(new LanguageMasterKey((String) languages.get(COUNTRY), lang));

		// check for locale and put them in a comma separated string
		if (languages.get(LOCALES) != null) {
			List<String> locales = (List<String>) languages.get(LOCALES);
			StringBuilder listOfLocales = new StringBuilder();
			for (String locale : locales) {
				if (!locale.isEmpty()) {
					listOfLocales.append(locale + ",");
				}
			}
			int length = listOfLocales.length();
			listOfLocales.deleteCharAt(length - 1);
			languageMaster.setLocale(listOfLocales.toString());
		}

		// set all the variables
		languageMaster.setFlagUrl((String) languages.get(FLAG_URL));
		languageMaster.setLanguageUserFriendly((String) languages.get(LANGUAGE_USER_FRIENDLY));
		languageMaster.setCountryUserFriendly((String) languages.get(COUNTRY_USER_FRIENDLY));
		languageTenant.setId(UUIDs.timeBased().toString());
		languageMasterRepo.save(languageMaster);
		languageTenantRepo.save(languageTenant);
	}

	/* (non-Javadoc)
substitute url based on requirement
	 */
	@Override
	@Transactional
	public void deleteLanguage(String rootOrg, String lang) {
		
		LanguageTenant languageRecord = languageTenantRepo.findTenantRecord(rootOrg, lang);
		if (languageRecord == null) {
			throw new InvalidDataInputException("record.notPresent");
		}
		languageTenantRepo.delete(rootOrg,lang);
		languageMasterRepo.delete(lang);
		
	}

	/* (non-Javadoc)
substitute url based on requirement
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void updateLanguage(String rootOrg, String lang, Map<String, Object> masterLanguage) {

		if(masterLanguage.get(COUNTRY)==null || masterLanguage.get(COUNTRY).toString().isEmpty()) {
			throw new InvalidDataInputException("invalid.countryCode");
		}
		LanguageProjection languageProjection = languageTenantRepo.findRecord(rootOrg, lang,(String) masterLanguage.get(COUNTRY));
		if (languageProjection == null) {
			throw new InvalidDataInputException("data.notPresent");
		}
		
		LanguageMaster languageMaster=new LanguageMaster();
		languageMaster.setLanguageTenantKey(new LanguageMasterKey(languageProjection.getcountry(), languageProjection.getlanguage()));

		// update language projection if it is in masterLanguage map
		if (masterLanguage.get(FLAG_URL) != null) {
			languageMaster.setFlagUrl((String) masterLanguage.get(FLAG_URL));
		}else {
			languageMaster.setFlagUrl(languageProjection.getflagUrl());
		}
		
		// update locales 
		if(masterLanguage.get(LOCALES)!=null) {
			List<String> locales = (List<String>) masterLanguage.get(LOCALES);
			StringBuilder listOfLocales = new StringBuilder();
			for (String locale : locales) {
				if (!locale.isEmpty()) {
					listOfLocales.append(locale + ",");
				}
			}
			int length = listOfLocales.length();
			listOfLocales.deleteCharAt(length - 1);
			languageMaster.setLocale(listOfLocales.toString());
		}else {
			languageMaster.setLocale(languageProjection.getlocales());
		}
		
		// update country user friendly 
		if(masterLanguage.get(COUNTRY_USER_FRIENDLY)!=null) {
			languageMaster.setCountryUserFriendly((String) masterLanguage.get(COUNTRY_USER_FRIENDLY));
		}else {
			languageMaster.setCountryUserFriendly(languageProjection.getcountryUserFriendly());
		}
		
		// update language friendly if it is given in the map
		if(masterLanguage.get(LANGUAGE_USER_FRIENDLY)!=null) {
			languageMaster.setCountryUserFriendly((String) masterLanguage.get(LANGUAGE_USER_FRIENDLY));
		}else {
			languageMaster.setCountryUserFriendly(languageProjection.getlanguageUserFriendly());
		}
		
		// delete previous record and insert new record
		languageMasterRepo.delete(lang);
		languageMasterRepo.save(languageMaster);
		
	}

}

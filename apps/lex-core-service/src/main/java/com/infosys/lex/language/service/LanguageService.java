/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.language.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface LanguageService {


	/**
	 * Get language details of the root_org and lang
	 * @param rootOrg
	 * @param lang
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLanguages(String rootOrg, String lang,String country) throws Exception;

	/**
	 * create a languge for root_org and lang
	 * @param rootOrg
	 * @param lang
	 * @param languages
	 * @return
	 */
	public void createLanguages(String rootOrg, String lang, Map<String, Object> languages);

	/**
	 * delete record of language and root_org
	 * @param rootOrg
	 * @param lang
	 * @return
	 */
	public void deleteLanguage(String rootOrg, String lang);

	/**
	 * update the record for a root_org and lang
	 * @param rootOrg
	 * @param lang
	 * @param masterLanguage
	 * @return
	 */
	public void updateLanguage(String rootOrg, String lang, Map<String, Object> masterLanguage);

}

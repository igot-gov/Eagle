/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.contentsource.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.infosys.lex.contentsource.dto.ContentSourceNameListDto;
import com.infosys.lex.contentsource.dto.ContentSourceUserDetailDto;
import com.infosys.lex.contentsource.postgres.projection.ContentSourceProj;
import com.infosys.lex.contentsource.postgres.projection.ContentSourceShortNameProj;
import com.infosys.lex.core.exception.InvalidDataInputException;

public interface ContentSourceService {

	/**
	 * Checks whether the user has registered in the content-source
	 * @throws Exception 
	 */
	
	Map<String, Object> getUser(String rootOrg, String sourceId, String userId) throws Exception;

	
	/**
	 * Fetches all the content-source details based on user Registration required
	 * 
	 * @param rootOrg
	 * @return
	 */
	List<ContentSourceProj> fetchAllContentsourcesForRootOrg(String rootOrg,Boolean registrationProvided );


	/**
	 * fetch content source detail for rootOrg
	 * 
	 * @param rootOrg
	 * @param sourceName
	 * @return
	 */
	ContentSourceProj fetchContentsourceDetails(String rootOrg, String sourceName, Boolean registrationProvided) throws InvalidDataInputException;


	/**
	 * This method registers the given users for the contentSource
	 * @param rootOrg
	 * @param contentSource
	 * @param usersDetailList
	 * @return
	 */



	Map<String, Object> registerContentSourceForUsers(String rootOrg, String contentSource,
			List<ContentSourceUserDetailDto> usersDetailList);


	Collection<Object> getRegisteredUsers(String rootOrg, String sourceShortName);


	void deRegisterUser(String rootOrg, String sourceShortName, List<String> registeredUsers) throws Exception;


	Map<String, ContentSourceShortNameProj>  fetchContentsourceDetailsForSourceName(String rootOrg, ContentSourceNameListDto sourceName)
			throws InvalidDataInputException;

}
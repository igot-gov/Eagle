/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.common.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ContentService {

	/**
	 * gets specific meta for a list of content ids
	 * 
	 * @param ids
	 * @param source
	 * @param Status
	 * @return
	 * @throws IOException
	 */
	List<Map<String, Object>> getMetaByIDListandSource(List<String> ids, String[] source, String status)
			throws IOException;
	
	List<Map<String, Object>> getMetaByIDListandStatusList(List<String> ids, String[] source, String[] statusList)
			throws IOException;

	/**
	 * gets the data in a map given the url from the content store
	 * 
	 * @param artifactUrl
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> getMapFromContentStore(String artifactUrl) throws Exception;

	/**
	 * gets the data in a string given the url from the content store
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	String getContentStoreData(String url) throws Exception;

	/**
	 * inserts file in the content store given the url to insert it in and the
	 * type(artifact, asset ..)
	 * 
	 * @param f
	 * @param insertionUrl
	 * @param type
	 * @return
	 * @throws Exception
	 */
	String insertFileInContentStore(File f, String insertionUrl, String type) throws Exception;

	/**
	 * gets mime types categories from the database
	 * 
	 * @return
	 */
	Map<String, String> getMimeTypes();
	
	
	/**
	 * Fetch content meta only if fields exists
	 * 
	 * @param ids
	 * @param source
	 * @param status
	 * @return
	 * @throws IOException
	 */
	List<Map<String, Object>> getMetaByIDListandSourceIfSourceFieldsExists(List<String> ids, String[] source,String[] fields,
			String status) throws IOException;

	/**
	 * Fetch content from S3 instance
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	String getKeyFromContentStore(String url) throws Exception;

	Map<String, Object> getAssessmentKeyFromContentStore(String url) throws Exception;

	/**
	 * Checks whether the content Id exists for given status
	 * 
	 * @param contentId
	 * @param status
	 * @return
	 * @throws IOException
	 * @throws ParseException 
	 */
	boolean validateContentIdToShow(String contentId) throws IOException, ParseException;
	
	Map<String, Map<String, Object>> filterAndFetchContentMetaToShow(List<String> contentIds, Set<String> sourceFields)
			throws IOException, ParseException;


	Map<String, String> getMACConfiguration(String rootOrg);

	List<Map<String, Object>> getMetaByIDListandSourceRest(List<String> asList, String[] strings, String string) throws IOException;

	List<Map<String, Object>> searchMatchedData(String index, String type, Map<String, Object> searchData,
												List<String> sourceData, int size) throws IOException;





}

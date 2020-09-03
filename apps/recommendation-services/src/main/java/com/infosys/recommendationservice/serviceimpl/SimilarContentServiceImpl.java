/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.recommendationservice.serviceimpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.infosys.recommendationservice.exception.ApplicationServiceError;
import com.infosys.recommendationservice.model.Response;
import com.infosys.recommendationservice.service.SimilarContentService;
import com.infosys.recommendationservice.util.SearchConstants;
import com.infosys.recommendationservice.util.SearchTemplateUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class SimilarContentServiceImpl implements SimilarContentService {

    @Value("${related.content.search.fields}")
    private String[] fieldsToSearch;

    @Value("${auth.tool.ip}")
    private String authserviceIp;

    @Value("${auth.tool.read.content.endpoint}")
    private String authserviceEndpoint;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

/*    @Autowired
    RestTemplate restTemplate;*/

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SearchTemplateUtil searchTemplateUtil;

    private List<String> searchFields = new ArrayList<>();


    @Override
    public Response findSimilarContents(String userId, String rootOrg, String org, String locale, String contentId, int pageNo, int pageSize, Set<String> sourceFields) {

        Response response = new Response();

        try {
            //TODO: Validate userId
            searchFields = Arrays.asList(fieldsToSearch);
            if(fieldsToSearch.length == 0)
                throw new  ApplicationServiceError("Consider configuring search fields");

            if(!searchFields.contains("name") || !searchFields.get(0).equalsIgnoreCase("name")){
                searchFields.add(0, "name");
            }

            //get content metadata through Auth API /read/{id};
            Map<String, Object> contentMeta = getContentMetadata(rootOrg, org, contentId);
            System.out.println("content metadata # " + objectMapper.writeValueAsString(contentMeta));

            //create the script params/paramsMap to build the search query
            Map<String, Object> prepareScriptParams = new HashMap<>();
            searchTemplateUtil.addBaseScriptParams(prepareScriptParams, pageNo, pageSize, sourceFields);

            ObjectNode node = objectMapper.convertValue(contentMeta, ObjectNode.class);
            parseMeta(node, prepareScriptParams);

            //Fetch ES search result
            SearchResponse searchResponse = searchTemplateUtil.searchTemplate(locale, prepareScriptParams, SearchConstants.ML_SEARCH_TEMPLATE);
            System.out.println("searchResponse = " + searchResponse);

            List<Object> results = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits()) {
                results.add(hit.getSourceAsMap());
            }

            response.put(response.MESSAGE, response.SUCCESSFUL);
            response.put(response.DATA, results);
            response.put("hitCount", searchResponse.getHits().getHits().length);
            response.put("totalHits", searchResponse.getHits().totalHits);
            response.put(response.STATUS, HttpStatus.OK);

        } catch (Exception e) {

            e.printStackTrace();
            throw new ApplicationServiceError("Failed to search contents: " + e.getMessage());
        }
        return response;
    }

    /**
     * Get content metadata for a given content id
     *
     * @param rootOrg
     * @param org
     * @param contentId
     * @return
     */
    private Map<String, Object> getContentMetadata(String rootOrg, String org, String contentId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("identifier", contentId);
        params.put("rootOrg", rootOrg);
        params.put("org", org);

        final String uri = authserviceIp.concat(authserviceEndpoint);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, Map.class, params);
    }


    /**
     * Parse the metaNode to prepare the script params
     *
     * @param metaNode
     * @param prepareScriptParams
     */
    private void parseMeta(ObjectNode metaNode, Map<String, Object> prepareScriptParams) {

        for (String field : fieldsToSearch) {

            JsonNode fieldNode = null;
            String[] fieldArray = field.split("[.]");

            if(fieldArray.length == 1){
                fieldNode = metaNode.get(field);

            } else if(fieldArray.length > 1){
                fieldNode = metaNode;

                for(String fieldName : fieldArray){
                    fieldNode = parseTree(fieldNode, fieldName);

                }
            }

            Object value = new Object();

            if(fieldNode.isTextual()){
                value = fieldNode.asText();
            }
            if(fieldNode.isArray()){
               value = objectMapper.convertValue(fieldNode, List.class);
            }
            if(fieldNode.isObject()){
                value = objectMapper.convertValue(fieldNode, Map.class);
            }

            prepareScriptParams.put(fieldArray[0] + SearchConstants.SEARCHTEMPLATE_KEY_SUFFIX, true);
            prepareScriptParams.put(fieldArray[0] + SearchConstants.SEARCHTEMPLATE_VALUE_SUFFIX, value);

        }

    }


    private JsonNode parseTree(JsonNode fieldNode, String fieldName){
        if(fieldNode.isObject()){
            System.out.println("object field node --- "+fieldNode);
            return  fieldNode = fieldNode.path(fieldName);
        }
        if(fieldNode.isArray()){
            System.out.println("Array one field node --- "+fieldNode.get(0));
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            for (int i = 0; i < fieldNode.size(); i++) {
                arrayNode.add(fieldNode.get(i).path(fieldName));
            }
            return  arrayNode;//fieldNode = fieldNode.iterator().next().path(fieldName);
        }
        return null;
    }


    /*private JsonNode findNodeQ(ObjectNode root, String fieldName) {

        if (fieldName == null || fieldName.isEmpty())
            throw new IllegalArgumentException("Invalid field name :"+fieldName);

        String [] fieldArray = fieldName.split(".");
        if(fieldArray.length == 1){
            return root.get(fieldArray[0]);

        } else if(fieldArray.length > 1) {
            for(String f : fieldArray){

            }
            root.fields().forEachRemaining(entry -> {
                JsonNode entryValue = entry.getValue();
                if (entryValue.isObject()) {
                    findNodeQ((ObjectNode) entry.getValue(), fieldName);
                }
                if (entryValue.isArray()) {
                    for (int i = 0; i < entryValue.size(); i++) {
                        if (entry.getValue().get(i).isObject())
                            findNodeQ((ObjectNode) entry.getValue().get(i), fieldName);
                    }
                }

            });
        }
        return null;

    }*/

//    private SearchResponse searchTemplate(String locale, Map<String, Object> scriptParams) throws Exception {
//
//        System.out.println("fetchFromES-paramsMap: "+scriptParams);
//        List<String> indices = new ArrayList<>();
//        indices.add(SearchConstants.SEARCH_INDEX_NAME_PREFIX + SearchConstants.SEARCH_INDEX_LOCALE_DELIMITER + locale);
//
//
//        SearchRequest searchRequest = new SearchRequest().searchType(SearchType.QUERY_THEN_FETCH);
//        searchRequest.indices(indices.toArray(new String[0]));
//        searchRequest.types(SearchConstants.SEARCH_INDEX_TYPE);
//
//        SearchTemplateRequest templateRequest = new SearchTemplateRequest();
//        templateRequest.setScript(SearchConstants.ML_SEARCH_TEMPLATE);
//        templateRequest.setScriptType(ScriptType.STORED);
//        templateRequest.setScriptParams(scriptParams);
//        templateRequest.setRequest(searchRequest);
//        templateRequest.getRequest();
//
//        return restHighLevelClient.searchTemplate(templateRequest, RequestOptions.DEFAULT).getResponse();
//    }

}

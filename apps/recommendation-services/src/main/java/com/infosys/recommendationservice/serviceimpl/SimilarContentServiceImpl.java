/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.recommendationservice.serviceimpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SearchTemplateUtil searchTemplateUtil;


    private List<String> defaultSourceFields = new ArrayList<>(Arrays.asList("hasAssessment", "locale", "subTitle", "totalLikes", "sourceName", "sourceShortName", "sourceIconUrl", "isStandAlone", "isInIntranet", "deliveryLanguages", "deliveryCountries", "costCenter", "exclusiveContent", "instanceCatalog", "price", "isContentEditingDisabled", "isMetaEditingDisabled", "labels", "publishedOn", "expiryDate", "hasTranslations", "isTranslationOf", "viewCount", "averageRating", "uniqueUsersCount", "totalRating", "collections", "unit", "status", "isExternal", "learningMode", "uniqueLearners", "name", "identifier", "description", "resourceType", "contentType", "isExternal", "appIcon", "artifactUrl", "children", "mimeType", "creatorContacts", "downloadUrl", "duration", "me_totalSessionsCount", "size", "complexityLevel", "lastUpdatedOn", "resourceCategory", "msArtifactDetails", "isIframeSupported", "contentUrlAtSource", "certificationUrl", "certificationList", "skills", "topics", "creatorDetails", "catalogPaths", "learningObjective", "preRequisites", "softwareRequirements", "systemRequirements", "track", "idealScreenSize", "minLexVersion", "preContents", "postContents", "isExternal", "certificationStatus", "subTitles", "publisherDetails", "trackContacts", "creatorContacts", "appIcon", "trackContacts", "publisherDetails"));



    @Override
    public Response findSimilarContents(String userId, String rootOrg, String org, String locale, String contentId, int pageNo, int pageSize) throws Exception {

        //TODO: Validate userId

        //get content metadata through Auth API /read/{id};
        Map<String, Object> contentMeta = getContentMetadata(rootOrg, org, contentId);
        System.out.println("content metadata = "+contentMeta);

        //create the script params/paramsMap to build the search query

        ObjectNode node = objectMapper.convertValue(contentMeta, ObjectNode.class);
        Map<String, Object> prepareScriptParams = new HashMap<>();
        prepareScriptParams.put("fetchSource", defaultSourceFields);
        prepareScriptParams.put("from", pageNo * pageSize);
        prepareScriptParams.put("size", pageSize);

        searchTemplateUtil.addBaseScriptParams(prepareScriptParams, pageNo, pageSize, null);
        parseMeta(node, prepareScriptParams);

        //Fetch ES search result
        SearchResponse searchResponse = searchTemplateUtil.searchTemplate(locale, prepareScriptParams);
        System.out.println("searchResponse = "+searchResponse);


        return null;
    }

    /**
     * Get content metadata for a given content id
     * @param rootOrg
     * @param org
     * @param contentId
     * @return
     */
    private Map<String, Object> getContentMetadata(String rootOrg, String org, String contentId){
        Map<String, String> params = new HashMap<String, String>();
        params.put("identifier", contentId);
        params.put("rootOrg", rootOrg);
        params.put("org", org);

        final String uri = authserviceIp.concat(authserviceEndpoint);
        return restTemplate.getForObject(uri, Map.class, params);
    }

    /**
     * Parse the metaNode to prepare the script params
     * @param metaNode
     * @param prepareScriptParams
     */
    private void parseMeta(ObjectNode metaNode, Map<String, Object> prepareScriptParams){

        for(String field : fieldsToSearch){

            JsonNode fieldNode =metaNode.findValue(field);

            prepareScriptParams.put(field + SearchConstants.SEARCHTEMPLATE_KEY_SUFFIX, true);
            prepareScriptParams.put(field + SearchConstants.SEARCHTEMPLATE_VALUE_SUFFIX, fieldNode);

        }

    }


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

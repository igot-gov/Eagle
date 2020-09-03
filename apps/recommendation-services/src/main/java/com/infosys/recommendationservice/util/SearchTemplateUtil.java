/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.recommendationservice.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchTemplateUtil {


    private List<String> defaultSourceFields = new ArrayList<>(Arrays.asList("keywords","hasAssessment", "locale", "subTitle", "totalLikes", "sourceName", "sourceShortName", "sourceIconUrl", "isStandAlone", "isInIntranet", "deliveryLanguages", "deliveryCountries", "costCenter", "exclusiveContent", "instanceCatalog", "price", "isContentEditingDisabled", "isMetaEditingDisabled", "labels", "publishedOn", "expiryDate", "hasTranslations", "isTranslationOf", "viewCount", "averageRating", "uniqueUsersCount", "totalRating", "collections", "unit", "status", "isExternal", "learningMode", "uniqueLearners", "name", "identifier", "description", "resourceType", "contentType", "isExternal", "appIcon", "artifactUrl", "children", "mimeType", "creatorContacts", "downloadUrl", "duration", "me_totalSessionsCount", "size", "complexityLevel", "lastUpdatedOn", "resourceCategory", "msArtifactDetails", "isIframeSupported", "contentUrlAtSource", "certificationUrl", "certificationList", "skills", "topics", "creatorDetails", "catalogPaths", "learningObjective", "preRequisites", "softwareRequirements", "systemRequirements", "track", "idealScreenSize", "minLexVersion", "preContents", "postContents", "isExternal", "certificationStatus", "subTitles", "publisherDetails", "trackContacts", "creatorContacts", "appIcon", "trackContacts", "publisherDetails"));
    private List<String> searchOn = Arrays.asList("learningObjective^1","preRequisites^1","subTitle^1","catalogPaths^1","childrenDescription^1","childrenTitle^1","concepts.name^1","description^1","keywords^1","sourceShortName^1","sourceName^1","name^2");


    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    ObjectMapper objectMapper;


    /**
     * Adds the base params to build the script map for stored template
     * @param prepareScriptParams
     * @param pageNo
     * @param pageSize
     * @param additionalSources
     */
    public void addBaseScriptParams(Map<String, Object> prepareScriptParams, int pageNo, int pageSize, Set<String> additionalSources){

        if (prepareScriptParams==null){
            prepareScriptParams = new HashMap<>();
        }

        if(additionalSources!=null)
            defaultSourceFields.addAll(additionalSources);

        prepareScriptParams.put("fetchSource", defaultSourceFields);
        prepareScriptParams.put("from", pageNo * pageSize);
        prepareScriptParams.put("size", pageSize);

        //prepareScriptParams.put("searchFieldsWithBoost", searchOn);

    }

    public void addSortingScriptParams(Map<String, Object> prepareScriptParams, List<String> descSortParams, List<String> ascSortParams){

        if (prepareScriptParams==null){
            prepareScriptParams = new HashMap<>();
        }

        Map<String, Object> sortableParams = new HashMap<>();
        if(descSortParams!=null){

            sortableParams.putAll(descSortParams.stream().collect(
                    Collectors.toMap(x -> x, x -> "desc")));
        }

        if(ascSortParams!=null){

            sortableParams.putAll(ascSortParams.stream().collect(
                    Collectors.toMap(x -> x, x -> "asc")));
        }

        prepareScriptParams.put("sort",true);
        prepareScriptParams.put("sortVal", sortableParams);

        //prepareScriptParams.put("searchFieldsWithBoost", searchOn);

    }


    public SearchResponse searchTemplate(String locale, Map<String, Object> scriptParams, String script) throws Exception {

        System.out.println("fetchFromES-paramsMap: "+objectMapper.writeValueAsString(scriptParams));
        List<String> indices = new ArrayList<>();
        indices.add(SearchConstants.SEARCH_INDEX_NAME_PREFIX + SearchConstants.SEARCH_INDEX_LOCALE_DELIMITER + locale);


        SearchRequest searchRequest = new SearchRequest().searchType(SearchType.QUERY_THEN_FETCH);
        searchRequest.indices(indices.toArray(new String[0]));
        searchRequest.types(SearchConstants.SEARCH_INDEX_TYPE);

        SearchTemplateRequest templateRequest = new SearchTemplateRequest();
        templateRequest.setScript(script);
        templateRequest.setScriptType(ScriptType.STORED);
        templateRequest.setScriptParams(scriptParams);
        templateRequest.setRequest(searchRequest);
        //templateRequest.getRequest();

        System.out.println("Query : "+templateRequest.getRequest());

        return restHighLevelClient.searchTemplate(templateRequest, RequestOptions.DEFAULT).getResponse();
    }




}

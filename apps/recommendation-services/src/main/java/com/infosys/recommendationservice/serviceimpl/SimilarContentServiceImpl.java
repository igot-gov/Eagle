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

    @Value("${related.content.sorting.desc.fields}")
    private String[] fieldsToSortDesc;

    @Value("${related.content.sorting.asc.fields}")
    private String[] fieldsToSortAsc;

    @Value("${related.content.sorting.enabled}")
    private boolean enableSorting;

    @Value("${auth.tool.ip}")
    private String authserviceIp;

    @Value("${auth.tool.read.content.endpoint}")
    private String authserviceEndpoint;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SearchTemplateUtil searchTemplateUtil;

    private Set<String> searchFields = new HashSet<>();
    private final static String defaultSearchField = "name";


    @Override
    public Response findSimilarContents(String userId, String rootOrg, String org, String locale, String contentId, int pageNo, int pageSize, Set<String> sourceFields) {

        Response response = new Response();

        try {
            //TODO: Validate userId
            System.out.println("Default Search fields-- "+objectMapper.writeValueAsString(fieldsToSearch));

            if(fieldsToSearch.length == 0)
                throw new  ApplicationServiceError("Consider configuring search fields");

            if(!Arrays.asList(fieldsToSearch).contains(defaultSearchField)){
                searchFields.add(defaultSearchField);
            }
            searchFields.addAll(Arrays.asList(fieldsToSearch));
            System.out.println("Final Search fields-- "+searchFields);

            //get content metadata through Auth API /read/{id};
            Map<String, Object> contentMeta = getContentMetadata(rootOrg, org, contentId);
            System.out.println("content metadata # " + objectMapper.writeValueAsString(contentMeta));

            //create the script params/paramsMap to build the search query
            Map<String, Object> prepareScriptParams = new HashMap<>();
            searchTemplateUtil.addBaseScriptParams(prepareScriptParams, pageNo, pageSize, sourceFields);

            if(enableSorting){
                searchTemplateUtil.addSortingScriptParams(prepareScriptParams, Arrays.asList(fieldsToSortDesc), Arrays.asList(fieldsToSortAsc));
            }

            ObjectNode node = objectMapper.convertValue(contentMeta, ObjectNode.class);
            parseMeta(node, prepareScriptParams);

            //Fetch ES search result
            SearchResponse searchResponse = searchTemplateUtil.searchTemplate(locale, prepareScriptParams, SearchConstants.ML_SEARCH_TEMPLATE);
            System.out.println("searchResponse hit size= " + searchResponse.getHits().getHits().length);

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
        System.out.println("contentId "+contentId);

        Map<String, String> params = new HashMap<String, String>();
        params.put("identifier", contentId);
        params.put("rootOrg", rootOrg);
        params.put("org", org);

        final String uri = authserviceIp.concat(authserviceEndpoint);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> res = restTemplate.getForObject(uri, Map.class, params);
        System.out.println("response -> "+res);
        return res;
    }


    /**
     * Parse the metaNode to prepare the script params
     *
     * @param metaNode
     * @param prepareScriptParams
     */
    private void parseMeta(ObjectNode metaNode, Map<String, Object> prepareScriptParams) {

        for (String field : searchFields) {

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
            System.out.println("set params for field, "+field+ "-> "+fieldNode);

            if(null != fieldNode){
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

    //TODO: removed this dummy
    private Map<String, Object> getDummyMeta() throws Exception{
        String strJson = "{\"childrenDescription\":[],\"references\":[],\"accessibility\":[],\"creatorContacts\":[{\"name\":\"Pritha Chattopadhyay\",\"id\":\"f3834e1b-315a-4de2-88a7-895ce0b7f46d\"}],\"downloadUrl\":\"http://private-content-service/contentv3/download/igot%2Fdopt%2FPublic%2Flex_auth_01309515125189836876%2Fartifacts%2FCitationNeededBookSample1598529064209.pdf?type=main\",\"publishedOn\":\"20200716T123131+0000\",\"playgroundResources\":[],\"skills\":[],\"sampleCertificates\":[],\"preContents\":[],\"systemRequirements\":[],\"kArtifacts\":[],\"identifier\":\"lex_auth_01309515125189836876\",\"audience\":[],\"jobProfile\":\"\",\"isExternal\":false,\"studyMaterials\":[],\"playgroundInstructions\":\"\",\"categoryType\":\"Article\",\"postContents\":[],\"lastPublishedOn\":\"20200716T123131+0000\",\"exclusiveContent\":false,\"concepts\":[],\"size\":1066176,\"publicationId\":\"\",\"status\":\"Live\",\"creatorThumbnail\":\"\",\"plagScan\":\"\",\"clients\":[],\"posterImage\":\"#003F5C\",\"authoringDisabled\":false,\"creatorDetails\":[{\"id\":\"f3834e1b-315a-4de2-88a7-895ce0b7f46d\",\"name\":\"Pritha Chattopadhyay\"}],\"certificationList\":[],\"projectCode\":\"\",\"creator\":\"f3834e1b-315a-4de2-88a7-895ce0b7f46d\",\"subTitles\":[],\"childrenTitle\":[],\"creatorLogo\":\"\",\"resourceCategory\":[],\"versionKey\":\"1598947322711\",\"introductoryVideo\":\"\",\"actor\":\"f3834e1b-315a-4de2-88a7-895ce0b7f46d\",\"accessPaths\":[\"igot/dopt\"],\"customClassifiers\":[],\"registrationInstructions\":[],\"learningObjective\":\"\",\"studyDuration\":0,\"resourceType\":\"Article\",\"publishedBy\":\"f3834e1b-315a-4de2-88a7-895ce0b7f46d\",\"keywords\":[\"Digitalization as key driver\",\"Ecosystems in new agile work\"],\"certificationUrl\":\"\",\"softwareRequirements\":[],\"isMetaEditingDisabled\":false,\"isIframeSupported\":\"Yes\",\"equivalentCertifications\":[],\"mimeType\":\"application/pdf\",\"body\":\"\",\"appIcon\":\"https://d12iy889j4sfk0.cloudfront.net/content-store/igot/dopt/Public/lex_auth_01309515125189836876/artifacts/books548x3311598529076018.jpg\",\"subTitle\":\"Sample Pdf\",\"trackContacts\":[{\"id\":\"f3834e1b-315a-4de2-88a7-895ce0b7f46d\",\"name\":\"Pritha Chattopadhyay\"}],\"creatorPosterImage\":\"\",\"hasAssessment\":false,\"introductoryVideoIcon\":\"\",\"artifactUrl\":\"https://d136953gtttd92.cloudfront.net/content-store/igot/dopt/Public/lex_auth_01309515125189836876/artifacts/CitationNeededBookSample1598529064209.pdf\",\"passPercentage\":0,\"contentType\":\"Resource\",\"thumbnail\":\"https://d12iy889j4sfk0.cloudfront.net/content-store/igot/dopt/Public/lex_auth_01309515125189836876/artifacts/books548x3311598529076018.jpg\",\"visibility\":\"Private\",\"publisherDetails\":[],\"nodeType\":\"LEARNING_CONTENT\",\"versionDate\":\"20200827T115053+0000\",\"complexityLevel\":\"Beginner\",\"unit\":\"\",\"name\":\"Sample Pdf\",\"isRejected\":false,\"region\":[],\"contentIdAtSource\":\"\",\"verifiers\":[],\"description\":\"Sample Pdf\",\"learningTrack\":\"\",\"idealScreenSize\":\"\",\"locale\":\"en\",\"isContentEditingDisabled\":false,\"learningMode\":\"Self-Paced\",\"expiryDate\":\"99991231T235959+0000\",\"duration\":3600,\"lastUpdatedOn\":\"20200901T080202+0000\",\"isSearchable\":true,\"dimension\":\"\",\"editors\":[],\"rootOrg\":\"igot\",\"preRequisites\":\"\",\"comments\":[{\"date\":\"20200901T080129+0000\",\"comment\":\"ok\",\"id\":\"f3834e1b-315a-4de2-88a7-895ce0b7f46d\"},{\"date\":\"20200901T080145+0000\",\"comment\":\"ok\",\"id\":\"f3834e1b-315a-4de2-88a7-895ce0b7f46d\"},{\"date\":\"20200901T080202+0000\",\"comment\":\"ok\",\"id\":\"f3834e1b-315a-4de2-88a7-895ce0b7f46d\"}],\"org\":[{\"validTill\":\"20700827T115053+0000\",\"org\":\"dopt\"}],\"catalogPaths\":[],\"isInIntranet\":false,\"isStandAlone\":true,\"sourceName\":\"ISTM (Institute of Secretariat Training and Management)\",\"category\":\"Resource\",\"sourceShortName\":\"\",\"fileType\":\"Document\"}\n";

        Map<String, Object> meta = objectMapper.readValue(strJson, Map.class);

        return meta;

    }


}

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class SimilarContentServiceImpl implements SimilarContentService {

    @Value("${related.content.search.fields}")
    private String[] fieldsToSearch;

    @Value("${auth.tool.ip}")
    private String authserviceIp;

    @Value("${auth.tool.read.content.endpoint}")
    private String authserviceEndpoint;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private final static String SEARCHTEMPLATE_KEY_SUFFIX = "Enable";
    private final static String SEARCHTEMPLATE_VALUE_SUFFIX = "Val";


    @Override
    public Response findSimilarContents(String userId, String rootOrg, String org, String locale, String contentId, int pageNo, int pageSize) throws Exception {

        //TODO: Validate userId

        //get content metadata through Auth API /read/{id};
        Map<String, Object> contentMeta = getContentMetadata(rootOrg, org, contentId);
        System.out.println("content metadata = "+contentMeta);

        //create the script params/paramsMap to build the search query

        ObjectNode node = objectMapper.convertValue(contentMeta, ObjectNode.class);
        Map<String, Object> prepareScriptParams = new HashMap<>();
        parseMeta(node, prepareScriptParams);

        //TODO: build ES search request and fire the query.

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

    private void parseMeta(ObjectNode metaNode, Map<String, Object> prepareScriptParams){
        for(String field : fieldsToSearch){

            JsonNode fieldNode =metaNode.findValue(field);

            prepareScriptParams.put(field + SEARCHTEMPLATE_KEY_SUFFIX, true);
            prepareScriptParams.put(field + SEARCHTEMPLATE_VALUE_SUFFIX, fieldNode);

        }


    }
}

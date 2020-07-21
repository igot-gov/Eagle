/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */
package com.infosys.recommendationservice.serviceimpl;

import com.infosys.recommendationservice.model.Response;
import com.infosys.recommendationservice.model.cassandra.UserCompetency;
import com.infosys.recommendationservice.repository.cassandra.bodhi.UserCompetencyRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.core.env.Environment;


@Service
@ConfigurationProperties("es")
public class ContentSearchServiceImpl {


    private final String[] source = new String[]{};

    @Autowired
    private Environment environment;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    UserCompetencyRepository userCompetencyRepository;

    public Response search(Map<String, Object> request, String rootOrg, String org, int pageSize, int pageNo) {

        Response response = new Response();
        try{

            String userId = (String) request.get("user_id");
            String userRole = (String) request.get("user_role");

            //finds the user competency keywords
            List<String> competencies = competencyKeywords(rootOrg, org, userId, userRole);

            //Build the search request to fire ES
            SearchRequest searchRequest = buildSearchRequest("tags", pageNo, pageSize, competencies);
            SearchHits searchHits = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT).getHits();

            //Parse the ES response
            List<Map<String, Object>> results = new ArrayList<>();
            for (SearchHit hit : searchHits) {
                results.add(hit.getSourceAsMap());
            }

            response.put("message", "Successful");
            response.put("contents", results);

        } catch (Exception e){
            response.put("message", "Failed");
            response.put("cause",e.getCause());
            response.put("contents", new ArrayList<>());
        }

        return response;
    }

    private List<String> competencyKeywords(String rootOrg, String org, String userId, String userrole) {
        List<UserCompetency> userCompetencies = userCompetencyRepository.findAllByUser(rootOrg, org, userId, userrole);
        return userCompetencies.stream().filter(userCompetency -> userCompetency.getDelta() > 0.0)
                .map(UserCompetency::getCompetency).collect(Collectors.toList());

    }

    private SearchRequest buildSearchRequest(String name, int offset, int limit, List<String> values) throws Exception{

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(environment.getProperty("es.index"));
        searchRequest.types(environment.getProperty("es.index.type"));

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if (values != null) {
            boolQueryBuilder.must(QueryBuilders.termsQuery(name.concat(".keyword"), values));
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(limit);
        searchSourceBuilder.from(offset);

        if (source != null && source.length > 0)
            searchSourceBuilder.fetchSource(source, new String[]{});
        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }


}

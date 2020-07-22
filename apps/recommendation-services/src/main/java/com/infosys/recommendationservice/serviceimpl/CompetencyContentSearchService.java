/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */
package com.infosys.recommendationservice.serviceimpl;

import com.infosys.recommendationservice.exception.ApplicationServiceError;
import com.infosys.recommendationservice.exception.BadRequestException;
import com.infosys.recommendationservice.model.Response;
import com.infosys.recommendationservice.model.cassandra.UserPositionCompetency;
import com.infosys.recommendationservice.repository.cassandra.bodhi.UserPositionCompetencyRepository;
import org.apache.lucene.search.join.ScoreMode;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.*;
import org.springframework.core.env.Environment;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;


@Service
@ConfigurationProperties("es")
public class CompetencyContentSearchService {


    private final String NESTED_PATH = "tagmapping";
    private final String COMPETENCY = "competency";
    private final String LEVEL = "level";
    private final String SEPERATOR_DOT =".";
    private final String USER_ID ="user_id";
    private final String USER_ROLE ="user_role";
    private final String[] source = new String[]{};


    @Autowired
    private Environment environment;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    UserPositionCompetencyRepository userPositionCompetencyRepository;

    /**
     * Search contents for a a given user and user position
     * @param request
     * @param rootOrg
     * @param org
     * @param pageSize
     * @param pageNo
     * @return
     */
    public Response search(Map<String, Object> request, String rootOrg, String org, int pageSize, int pageNo) {

        Response response = new Response();;

        try{

            String userId = (String) request.get(USER_ID);
            String userRole = (String) request.get(USER_ROLE);

            //TODO: validate - user_id and access control path
            if(userId==null || userId.isEmpty() || userRole==null || userRole.isEmpty()){
                throw new BadRequestException("user_id and user_role cant be null or empty");

            }
            //finds the user competencies
            List<UserPositionCompetency> userCompetencies = userPositionCompetencyRepository.findAllByUserAndPosition(rootOrg, org, userId, userRole);

            //Build the search request to fire ES
            SearchRequest searchRequest = buildCompetencySearchRequest(pageNo, pageSize, userCompetencies);

            if(searchRequest.source().query()==null) {
                response.put(response.MESSAGE, "No data recommended");
                response.put(response.DATA, new ArrayList<>());
                response.put(response.STATUS, HttpStatus.OK);
            } else {
                SearchHits searchHits = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT).getHits();

                //Parse the ES response
                List<Map<String, Object>> results = new ArrayList<>();
                for (SearchHit hit : searchHits) {
                    results.add(hit.getSourceAsMap());
                }
                response.put(response.MESSAGE, response.SUCCESSFUL);
                response.put(response.DATA, results);
                response.put(response.STATUS, HttpStatus.OK);
            }

        } catch (Exception e){
            e.printStackTrace();
            throw new ApplicationServiceError("Failed to search contents: "+e.getMessage());

        }

        return response;
    }


    /**
     * Builds a search request with filter of n- competencies
     * @param offset
     * @param limit
     * @param userPositionCompetencies
     * @return
     * @throws Exception
     */
    private SearchRequest buildCompetencySearchRequest(int offset, int limit, List<UserPositionCompetency> userPositionCompetencies) throws Exception{

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(environment.getProperty("es.index"));
        searchRequest.types(environment.getProperty("es.index.type"));

        BoolQueryBuilder query = QueryBuilders.boolQuery();

        for(UserPositionCompetency upc: userPositionCompetencies){
            if(upc.getDelta()!=null || upc.getDelta().size()>0){
                query.should(
                        QueryBuilders.boolQuery()
                                .must(QueryBuilders.termQuery(NESTED_PATH.concat(SEPERATOR_DOT).concat(COMPETENCY), upc.getUserCompetency()))
                                .must(QueryBuilders.termsQuery(NESTED_PATH.concat(SEPERATOR_DOT).concat(LEVEL), upc.getDelta())));
            }
        }

        BoolQueryBuilder qb = null;
        if(query.hasClauses()){
            qb = boolQuery().must(QueryBuilders.nestedQuery(NESTED_PATH, query, ScoreMode.Avg));
        }
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(qb);
        searchSourceBuilder.size(limit);
        searchSourceBuilder.from(offset);

        if (source != null && source.length > 0)
            searchSourceBuilder.fetchSource(source, new String[]{});
        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }


}

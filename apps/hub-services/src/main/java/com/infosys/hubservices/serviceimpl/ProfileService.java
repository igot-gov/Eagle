/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.serviceimpl;

import com.infosys.hubservices.exception.ApplicationException;
import com.infosys.hubservices.model.MultiSearch;
import com.infosys.hubservices.model.Response;
import com.infosys.hubservices.model.Search;
import com.infosys.hubservices.model.cassandra.UserConnection;
import com.infosys.hubservices.service.IProfileService;
import com.infosys.hubservices.util.ConnectionProperties;
import com.infosys.hubservices.util.Constants;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProfileService implements IProfileService {

    private Logger logger = LoggerFactory.getLogger(ProfileService.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    ConnectionProperties connectionProperties;

    @Autowired
    ConnectionService connectionService;



    @Override
    public Response findCommonProfile(String userId, int offset, int limit) {

        Response responseConnections = connectionService.findSuggestedConnections(userId, offset, limit);
        return getProfiles(responseConnections);


    }

    @Override
    public Response findProfiles(String userId, int offset, int limit) {

        Response responseConnections = connectionService.findConnections(userId, offset, limit);
        return getProfiles(responseConnections);

    }



    @Override
    public Response findProfileRequested(String userId, int offset, int limit) {
        Response responseConnections = connectionService.findConnectionsRequested(userId, offset, limit);
        return getProfiles(responseConnections);

    }


    @Override
    public Response findProfiles(List<String> userIds, String[] sourceFields) {

        Response response = new Response();
        try{
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices(connectionProperties.getEsProfileIndex());
            searchRequest.types(connectionProperties.getEsProfileIndexType());

            BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("id.keyword", userIds));

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(query);
            if (sourceFields != null && sourceFields.length>0) {
                String[] mergedSource = (String[])Stream.of(connectionProperties.getEsProfileSourceFields(), sourceFields).flatMap(Stream::of).toArray();
                searchSourceBuilder.fetchSource(mergedSource, new String[] {});

            } else {
                searchSourceBuilder.fetchSource(connectionProperties.getEsProfileSourceFields(), new String[] {});

            }
            searchSourceBuilder.size(userIds.size());
            searchRequest.source(searchSourceBuilder);
            SearchHits searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT).getHits();
            logger.info("ids: {} and user profiles searched : {}", userIds, searchResponse.getHits().length );
            List<Object> results = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits()) {
                results.add(hit.getSourceAsMap());
            }

            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.DATA, results);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);
        } catch (IOException e){
            throw new ApplicationException(Constants.Message.FAILED_CONNECTION + e.getMessage());

        }

        return response;
    }

    @Override
    public Response multiSearchProfiles(String rootOrg, MultiSearch mSearchRequest, String[] sourceFields) {
        Response response = new Response();
        try{

            List<String> tags = new ArrayList<>();

            MultiSearchRequest request = new MultiSearchRequest();
            for(Search sRequest: mSearchRequest.getSearch()) {

                SearchRequest searchRequest = new SearchRequest();

                searchRequest.indices(connectionProperties.getEsProfileIndex());
                searchRequest.types(connectionProperties.getEsProfileIndexType());

                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                tags.add(sRequest.getField());
                BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(sRequest.getField(), sRequest.getValues()));
                searchSourceBuilder.query(query);
                searchRequest.source(searchSourceBuilder);
                if (sourceFields != null && sourceFields.length>0) {
                    String[] mergedSource = (String[])Stream.of(connectionProperties.getEsProfileSourceFields(), sourceFields).flatMap(Stream::of).toArray();
                    searchSourceBuilder.fetchSource(mergedSource, new String[] {});

                } else {
                    searchSourceBuilder.fetchSource(connectionProperties.getEsProfileSourceFields(), new String[] {});

                }
                searchSourceBuilder.from(mSearchRequest.getOffset());
                searchSourceBuilder.size(mSearchRequest.getSize());

                request.add(searchRequest);

            }

            MultiSearchResponse multiSearchResponse = restHighLevelClient.msearch(request, RequestOptions.DEFAULT);

            List<Object> finalRes = new ArrayList<>();
            for(int i=0; i< multiSearchResponse.getResponses().length; i++){
                SearchResponse searchResponse = multiSearchResponse.getResponses()[i].getResponse();

                Map<String, Object> resObjects = new HashMap<>();
                List<Object> results = new ArrayList<>();
                for (SearchHit hit : searchResponse.getHits()) {
                    results.add(hit.getSourceAsMap());
                }
                resObjects.put("field",tags.get(i));
                resObjects.put("results", results);
                finalRes.add(resObjects);


            }
            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.DATA, finalRes);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);



        } catch (IOException e){
            logger.error(Constants.Message.CONNECTION_EXCEPTION_OCCURED, e);
            throw new ApplicationException(Constants.Message.FAILED_CONNECTION );

        }

        return response;
    }

    private Response getProfiles(Response connections){

        List<UserConnection> userConnections = (List<UserConnection>)connections.get(Constants.ResponseStatus.DATA);
        if(userConnections.isEmpty()){
            return connections;
        }

        List<String> connectionIds = userConnections.stream().map(uc -> uc.getUserConnectionPrimarykey().getConnectionId()).collect(Collectors.toList());
        return findProfiles(connectionIds,null);

    }
}

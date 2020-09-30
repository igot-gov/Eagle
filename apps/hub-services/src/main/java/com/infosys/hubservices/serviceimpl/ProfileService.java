/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.serviceimpl;

import com.infosys.hubservices.exception.ApplicationServiceError;
import com.infosys.hubservices.model.Response;
import com.infosys.hubservices.model.cassandra.UserConnection;
import com.infosys.hubservices.service.IProfileService;
import com.infosys.hubservices.util.ConnectionProperties;
import com.infosys.hubservices.util.Constants;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProfileService implements IProfileService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    ConnectionProperties connectionProperties;

    @Autowired
    ConnectionService connectionService;


    @Override
    public Response findCommonProfile(String userId, int offset, int limit) {

        Response responseConnections = connectionService.findCommonConnection(userId, offset, limit);
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

            BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("id", userIds));

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
            List<Object> results = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits()) {
                results.add(hit.getSourceAsMap());
            }

            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.DATA, results);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);
        } catch (IOException e){
            throw new ApplicationServiceError(Constants.Message.FAILED_CONNECTION + e.getMessage());

        }

        return response;
    }


    private Response getProfiles(Response connections){

        List<UserConnection> userConnections = (List<UserConnection>)connections.get(Constants.ResponseStatus.DATA);
        if(userConnections.size()==0){
            return connections;
        }

        List<String> connectionIds = userConnections.stream().map(uc -> uc.getUserConnectionPrimarykey().getConnectionId()).collect(Collectors.toList());
        return findProfiles(connectionIds, connectionProperties.getEsProfileSourceFields());

    }
}

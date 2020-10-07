/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.serviceimpl;

import com.infosys.hubservices.model.Response;
import com.infosys.hubservices.model.cassandra.UserConnection;
import com.infosys.hubservices.model.cassandra.UserConnectionPrimarykey;
import com.infosys.hubservices.util.ConnectionProperties;
import com.infosys.hubservices.util.Constants;
import jdk.nashorn.internal.objects.annotations.Where;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ProfileServiceTest {

    @InjectMocks
    ProfileService profileService;
    @Mock
    ConnectionService connectionService;
    @Mock
    ConnectionProperties connectionProperties;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void findCommonProfile() {

        List<UserConnection> mockConList = new ArrayList<>();

        Response mockresponse = new Response();
        mockresponse.put("data", mockConList);
        when(connectionService.findSuggestedConnections("user_id", 0, 1)).thenReturn(mockresponse);

        Response res = profileService.findCommonProfile("user_id", 0, 1);
        System.out.println("RES "+res.getResult());
        assertTrue(((List)res.getResult().get("data")).size()==0);
    }

/*    @Test
    void findCommonProfile() {

        List<UserConnection> mockConList = new ArrayList<>();

        UserConnectionPrimarykey pk = new UserConnectionPrimarykey("rootOrg", "user_id", "connection_id");
        UserConnection mockConn = new UserConnection(pk, "status", "", new Date());
        mockConList.add(mockConn);

        Response mockresponse = new Response();
        mockresponse.put("data", mockConList);
        when(connectionService.findSuggestedConnections("user_id", 0, 1)).thenReturn(mockresponse);

        Response response = new Response();
        response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
        response.put(Constants.ResponseStatus.DATA, new ArrayList<>());
        response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);
        when(profileService.findProfiles(Arrays.asList("connection_id"),null)).thenReturn(response);

        Response res = profileService.findCommonProfile("user_id", 0, 1);
        System.out.println("RES "+res.getResult());
        assertTrue(((List)res.getResult().get("data")).size()==0);
    }*/

    @Test
    void findProfiles() throws IOException {
//        RestHighLevelClient restHighLevelClient = mock(RestHighLevelClient.class, RETURNS_DEEP_STUBS);
//        System.out.println("restHighLevelClient=="+restHighLevelClient);
//
//        String [] sourceFields = {"id"};
//        List<String> ids = Arrays.asList("user_id");
//        when(connectionProperties.getEsProfileIndex()).thenReturn("index");
//        when(connectionProperties.getEsProfileIndexType()).thenReturn("type");
//        when(connectionProperties.getEsProfileSourceFields()).thenReturn(sourceFields);
//        SearchRequest sr = new SearchRequest();
//        sr.indices("index");
//        sr.types("type");
//        BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("id.keyword", ids));
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(query);
//        searchSourceBuilder.fetchSource(connectionProperties.getEsProfileSourceFields(), new String[] {});
//        searchSourceBuilder.size(ids.size());
//        sr.source(searchSourceBuilder);
//
//        when(restHighLevelClient.search(sr, RequestOptions.DEFAULT).getHits()).thenReturn(mock(SearchHits.class));
//
//        Response res = profileService.findProfiles(ids,null);

    }

    @Test
    void findProfileRequested() {

        List<UserConnection> mockConList = new ArrayList<>();

        Response mockresponse = new Response();
        mockresponse.put("data", mockConList);
        when(connectionService.findConnectionsRequested("user_id", 0, 1)).thenReturn(mockresponse);

        Response res = profileService.findProfileRequested("user_id", 0, 1);
        assertTrue(((List)res.getResult().get("data")).size()==0);
    }


    @Test
    void multiSearchProfiles() {
    }
}
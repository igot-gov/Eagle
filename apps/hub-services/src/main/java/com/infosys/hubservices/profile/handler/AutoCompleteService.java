package com.infosys.hubservices.profile.handler;

import com.infosys.hubservices.exception.ApplicationException;
import com.infosys.hubservices.util.ConnectionProperties;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AutoCompleteService {

    @Autowired
    private ConnectionProperties connectionProperties;

    @Autowired
    private RestHighLevelClient esClient;

    public List<Map<String, Object>> getUserSearchData(String searchTerm) throws IOException {
        if (StringUtils.isEmpty(searchTerm))
            throw new ApplicationException("Search term should not be empty!");
        List<Map<String, Object>> resultArray = new ArrayList<>();
        Map<String, Object> result;
        final BoolQueryBuilder query = QueryBuilders.boolQuery();
        query
                .should(QueryBuilders.matchPhrasePrefixQuery("personalDetails.primaryEmail", searchTerm))
                .should(QueryBuilders.matchPhrasePrefixQuery("personalDetails.firstname", searchTerm))
                .should(QueryBuilders.matchPhrasePrefixQuery("personalDetails.surname", searchTerm));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(query);
        String[] includeFields = {"personalDetails.firstname", "personalDetails.surname", "personalDetails.primaryEmail", "id", "professionalDetails.name"};
        sourceBuilder.fetchSource(includeFields, new String[]{});
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(connectionProperties.getEsProfileIndex());
        searchRequest.types(connectionProperties.getEsProfileIndexType());
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits()) {
            Map<String, Object> searObjectMap = hit.getSourceAsMap();
            Map<String, Object> personalDetails = (Map<String, Object>) searObjectMap.get("personalDetails");
            result = new HashMap<>();
            result.put("first_name", personalDetails.get("firstname"));
            result.put("last_name", personalDetails.get("surname"));
            result.put("email", personalDetails.get("primaryEmail"));
            result.put("wid", searObjectMap.get("id"));
            result.put("department_name", ((Map<String, Object>) searObjectMap.get("professionalDetails")).get("name"));
            result.put("rank", hit.getScore());
            resultArray.add(result);
        }
        return resultArray;
    }
}

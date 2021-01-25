
package org.sunbird.scoringengine.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@Service
public class IndexerService {
    private RestHighLevelClient esClient;

    @Value("${es.auth.enabled}")
    private boolean esAuthEnabled;

    @Value("${es.host}")
    private String esHost;

    @Value("${es.port}")
    private String esPort;

    @Value("${es.username}")
    private String esUsername;

    @Value("${es.password}")
    private String esPassword;

    private Logger logger = LoggerFactory.getLogger(IndexerService.class);


    @PostConstruct
    public void init() {
        logger.info("#INITIALIZING SCORE INDEXER");
        HttpHost[] hosts = new HttpHost[1];
        hosts[0] = new HttpHost(esHost, Integer.parseInt(esPort));
        logger.info("  " + esHost);
        logger.info("  " + esPort);
        logger.info("  " + esUsername);
        logger.info("  " + esPassword);
        RestClientBuilder builder = RestClient.builder(hosts);
        if (esAuthEnabled) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(esUsername, esPassword));
            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }
        esClient = new RestHighLevelClient(builder);
        logger.info("#ES CLIENT INITIALIZED");
        logger.info("#INITIALIZING SCORE INDEXER FINISHED");
    }

    public RestStatus addEntity(String index, String indexType, String entityId, Map<String, Object> indexDocument) {
        logger.debug("addEntity starts with index {} and entityId {}", index, entityId);
        IndexResponse response = null;
        try {
            response = esClient.index(new IndexRequest(index, indexType, entityId).source(indexDocument), RequestOptions.DEFAULT);
            logger.info("response for entityId {} is {}", entityId, response);
        } catch (IOException e) {
            logger.error("Exception in adding record to ElasticSearch", e);
        }
        return response.status();
    }

    public JsonNode search(String index, Map<String, Object> searchQuery) throws IOException {
        boolean getLatestRecord = (boolean)searchQuery.get("isGetLatestRecordEnabled");
        searchQuery.remove("isGetLatestRecordEnabled");
        BoolQueryBuilder query = buildQuery(searchQuery);
        logger.info("Search query " + new ObjectMapper().writeValueAsString(query));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(query);
        if(getLatestRecord){
            sourceBuilder.sort(SortBuilders.fieldSort("timeStamp.keyword").order(SortOrder.DESC));
            sourceBuilder.size(1);
        }
        SearchRequest searchRequest = new SearchRequest(index).source(sourceBuilder);

        ArrayNode resultArray = JsonNodeFactory.instance.arrayNode();
        ObjectMapper mapper = new ObjectMapper();
        SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits()) {
            JsonNode node = mapper.readValue(hit.getSourceAsString(), JsonNode.class);
            resultArray.add(node);
        }
        logger.debug("Total search records found " + resultArray.size());

        return resultArray;

    }

    private BoolQueryBuilder buildQuery(Map<String,Object> searchQuery) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        for ( Map.Entry<String, Object> filter : searchQuery.entrySet()) {
            String field = filter.getKey();
            Object value = filter.getValue();
            boolean emptyValue = (value instanceof String) ? ((String) value).isEmpty() : false;
            if(value != null && !emptyValue) query = query.must(QueryBuilders.matchQuery(field, value));
        }
        return query;
    }
    public Map<String, Object> readEntity(String index, String indexType, String entityId) throws IOException {
        logger.info("readEntity starts with index {} and entityId {}", index, entityId);
        GetResponse response = null;
        response = esClient.get(new GetRequest(index, indexType, entityId), RequestOptions.DEFAULT);
        return response.getSourceAsMap();
    }


    public RestStatus updateEntity(String index, String indexType, String entityId, Map<String, ?> indexDocument) {
        logger.debug("updateEntity starts with index {} and entityId {}", index, entityId);
        UpdateResponse response = null;
        try {
            response = esClient.update(new UpdateRequest(index.toLowerCase(), indexType, entityId).doc(indexDocument), RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("Exception in updating a record to ElasticSearch", e);
        }
        return response.status();
    }




}

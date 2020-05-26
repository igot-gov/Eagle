/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.interest.repo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InterestCRUDImpl implements InterestCRUD {

//	@Autowired
//	AppConfigRepository appConfigRepo;

	@Autowired
	RestHighLevelClient restHighLevelClient;

	public static final String alias = "topicautocomplete_";
	public static final String scriptTopic = "topicsactemplate";
	public static final String type = "autocomplete";

//	@Override
//	@Cacheable("interests")
//	public List<String> allowedLanguages() throws ResourceNotFoundException {
//
//		Optional<AppConfig> appconfig = appConfigRepo.findById(new AppConfigPrimaryKey("default", "allowedlanguages"));
//		if (!appconfig.isPresent()) {
//			throw new ResourceNotFoundException("default language is not found");
//		} else {
//			return Arrays.asList(appconfig.get().getValue().split(","));
//		}
//	}

	@Override
	public List<String> suggestedComplete(String rootOrg, String org, @NotNull String language) throws IOException {
		List<String> interestDefault = new ArrayList<String>();
		GetIndexRequest request = new GetIndexRequest(alias + language);
		boolean exist = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
		SearchRequest searchRequest = new SearchRequest();
		if (exist) {
			searchRequest.indices(alias + language);
			searchRequest.types(type);

		} else {
			searchRequest.indices(alias + "en");
			searchRequest.types(type);
		}
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		query.must(QueryBuilders.termQuery("rootOrg", rootOrg));
		query.must(QueryBuilders.termQuery("isSuggested", true));
		query.must(QueryBuilders.termQuery("org", org));
		sourceBuilder.query(query);
		sourceBuilder.size(200); // change this to scroll - es later
		searchRequest.source(sourceBuilder);
		SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		// explore aggs
		for (SearchHit hit : response.getHits()) {
			interestDefault.add(hit.getSourceAsMap().get("searchTerm").toString());
		}
		return interestDefault;
	}

}

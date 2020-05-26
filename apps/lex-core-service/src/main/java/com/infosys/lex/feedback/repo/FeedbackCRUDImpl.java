/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.feedback.repo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.infosys.lex.feedback.dto.Feedback;
import com.infosys.lex.feedback.dto.FeedbackSearchDTO;

@Repository
public class FeedbackCRUDImpl implements FeedbackCRUD {

	@Autowired
	RestHighLevelClient restHighLevelClient;

	private final static String FEEDBACK_INDEX = "feedback_index";
	private final static String FEEDBACK_TYPE = "feedback";

	@Override
	public Boolean createThread(Feedback feedback) throws IOException {
		IndexRequest indexRequest = new IndexRequest(FEEDBACK_INDEX, FEEDBACK_TYPE, feedback.getFeedbackId());
		indexRequest.source(feedback.toMap());
		IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
		return response.getId().equals(feedback.getFeedbackId());
	}

	@Override
	public List<Feedback> fetchThreads(String rootOrg, String rootFeedbackId) throws IOException {
		List<Feedback> feedbacks = new ArrayList<Feedback>();
		SearchRequest searchRequest = new SearchRequest(FEEDBACK_INDEX);
		searchRequest.searchType(SearchType.QUERY_THEN_FETCH);
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		query.must(QueryBuilders.termQuery("rootFeedbackId", rootFeedbackId));
		query.must(QueryBuilders.termQuery("rootOrg", rootOrg));
		System.out.println(query);
		sourceBuilder.query(query);
		sourceBuilder.sort("createdOn", SortOrder.ASC);
		searchRequest.source(sourceBuilder);
		SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		for (SearchHit hit : response.getHits()) {
			feedbacks.add(Feedback.fromMap(hit.getSourceAsMap()));
		}
		return feedbacks;

	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> searchThreads(FeedbackSearchDTO feedbackSearchDTO, String rootOrg) throws IOException {

		Map<String, Object> response = new HashMap<>();
		List<Feedback> feedbacks = new ArrayList<Feedback>();
		String searchQuery = feedbackSearchDTO.getQuery();
		String userId = feedbackSearchDTO.getUserId();
		String viewer = feedbackSearchDTO.getViewedBy().toLowerCase();
		Map<String, Object> filters = feedbackSearchDTO.getFilters();

		SearchRequest searchRequest = new SearchRequest(FEEDBACK_INDEX);
		searchRequest.searchType(SearchType.QUERY_THEN_FETCH);
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder query = QueryBuilders.boolQuery();

		Script script = new Script(ScriptType.INLINE, "painless",
				"doc['feedbackId'].value == doc['rootFeedbackId'].value", new HashMap<String, Object>());
		query.filter(QueryBuilders.scriptQuery(script));
		query.must(QueryBuilders.termQuery("rootOrg", rootOrg));

		if (!searchQuery.isEmpty()) {
			if (searchQuery.startsWith("lex_")) {
				query.must(QueryBuilders.termQuery("contentId", searchQuery));

			} else {
				query.must(QueryBuilders.multiMatchQuery(searchQuery, "feedbackText", "contentTitle"));
			}
		}

		if ("user".equals(viewer)) {
			query.must(QueryBuilders.termQuery("feedbackBy", userId));

		} else {
			if ("author".equals(viewer)) {
				query.must(QueryBuilders.termQuery("assignedTo", userId));
			}
			if ("content-feedback-admin".equals(viewer)) {
				query.must(QueryBuilders.termQuery("feedbackType", "content_feedback"));
			}
		}

		for (String filter : filters.keySet()) {
			query.must(QueryBuilders.termsQuery(filter, (List<String>) filters.get(filter)));
		}

		if (!feedbackSearchDTO.getReplyFilter()) {
			if ("user".equals(feedbackSearchDTO.getViewedBy())) {
				query.must(QueryBuilders.termQuery("seenReply", false));
				query.must(QueryBuilders.termQuery("replied", true));
			} else {
				query.must(QueryBuilders.termQuery("replied", false));
			}
		}
		
		
		System.out.println(query);
		sourceBuilder.query(query);
		sourceBuilder.from(feedbackSearchDTO.getFrom());
		sourceBuilder.size(feedbackSearchDTO.getOffset());
		sourceBuilder.sort("lastActivityOn", SortOrder.DESC);
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		for (SearchHit hit : searchResponse.getHits()) {
			feedbacks.add(Feedback.fromMap(hit.getSourceAsMap()));
		}

		response.put("totalHits", searchResponse.getHits().getTotalHits());
		response.put("hits", feedbacks);
		return response;
	}

	@Override
	public Boolean updateThread(String feedbackId, Map<String, Object> updateMap) throws IOException {
		GetRequest getRequest = new GetRequest(FEEDBACK_INDEX, FEEDBACK_TYPE, feedbackId);
		GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		Map<String, Object> update = response.getSourceAsMap();
		for (String key : updateMap.keySet())
			update.put(key, updateMap.get(key));
		IndexRequest indexRequest = new IndexRequest(FEEDBACK_INDEX, FEEDBACK_TYPE, feedbackId);
		indexRequest.source(update);
		IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
		return indexResponse.getId().equals(feedbackId);
	}

	@Override
	public Feedback fetchThread(String feedbackId) throws IOException {
		GetRequest getRequest = new GetRequest(FEEDBACK_INDEX, FEEDBACK_TYPE, feedbackId);
		GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		return Feedback.fromMap(response.getSourceAsMap());
	}

	@Override
	public Map<String, Object> fetchHits(String userId, String roles, Integer assign) throws IOException {
		Map<String, Object> result = new HashMap<>();
		SearchRequest searchRequest = new SearchRequest(FEEDBACK_INDEX);
		searchRequest.searchType(SearchType.QUERY_THEN_FETCH);
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder query = QueryBuilders.boolQuery();

		Script script = new Script(ScriptType.INLINE, "painless",
				"doc['feedbackId'].value == doc['rootFeedbackId'].value", new HashMap<String, Object>());
		query.filter(QueryBuilders.scriptQuery(script));

		if (!roles.isEmpty()) {
			query.must(QueryBuilders.termQuery("feedbackType", roles));
		}

		if (assign > 0 && "content_feedback".equals(roles.toLowerCase())) {
			query.must(QueryBuilders.termsQuery("assignedTo", userId));
		} else {
			if (assign == 0 && roles.isEmpty()) {
				query.must(QueryBuilders.termQuery("feedbackBy", userId));
			}
		}

		System.out.println(query);
		sourceBuilder.query(query);
		searchRequest.source(sourceBuilder);
		SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		result.put("total", response.getHits().getTotalHits());

		if (assign != 0) {
			query.must(QueryBuilders.termQuery("replied", false));
		} else {
			query.must(QueryBuilders.termQuery("replied", true));
			query.must(QueryBuilders.termQuery("seenReply", false));
		}
		System.out.println(query);
		sourceBuilder.query(query);
		searchRequest.source(sourceBuilder);
		response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		result.put("notSeen", response.getHits().getTotalHits());
		return result;
	}

	@Override
	public Map<String, Object> paginatedDump(String rootOrg, Long startDate, Long endDate, Integer size,
			String scrollId) throws IOException {
		Map<String, Object> result = new HashMap<>();
		SearchRequest searchRequest = new SearchRequest(FEEDBACK_INDEX);
		searchRequest.searchType(SearchType.QUERY_THEN_FETCH);
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		query.must(QueryBuilders.termQuery("rootOrg", rootOrg));
		RangeQueryBuilder range = QueryBuilders.rangeQuery("lastActivityOn");
		range.gte(startDate);
		range.lt(endDate);
		query.filter(range);
		System.out.println(query);
		sourceBuilder.query(query);
		sourceBuilder.size(size);
		searchRequest.source(sourceBuilder);

		if ("none".equalsIgnoreCase(scrollId)) {
			searchRequest.scroll(new TimeValue(100000l));

			SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
			List<Map<String, Object>> hits = new ArrayList<>();

			for (SearchHit hit : response.getHits()) {
				hits.add(hit.getSourceAsMap());
			}

			result.put("hits", hits);
			if (response.getHits().totalHits > Long.parseLong(size.toString())) {
				result.put("scrollId", response.getScrollId());
			}
		} else {
			SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
			scrollRequest.scroll(new TimeValue(100000l));

			SearchResponse response = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);

			List<Map<String, Object>> hits = new ArrayList<>();

			for (SearchHit hit : response.getHits()) {
				hits.add(hit.getSourceAsMap());
			}

			result.put("hits", hits);
			if (response.getHits().totalHits > Long.parseLong(size.toString())) {
				result.put("scrollId", response.getScrollId());
			}
		}

		return result;

	}

}

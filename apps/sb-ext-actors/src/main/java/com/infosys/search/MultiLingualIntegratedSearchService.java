/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at 
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
 * © 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
 * Version: 1.10
 * <p>
 * Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
 * this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
 * the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
 * by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of
 * this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
 * under the law.
 * <p>
 * Highly Confidential
 */
package com.infosys.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.elastic.helper.ConnectionManager;
import com.infosys.exception.BadRequestException;
import com.infosys.exception.NoContentException;
import com.infosys.model.FilterItem;
import com.infosys.search.validations.model.*;
import com.infosys.util.LexProjectUtil;
import com.infosys.util.SearchConstants;
import org.apache.commons.lang.WordUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.global.ParsedGlobal;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sunbird.common.models.response.Response;

import javax.annotation.PostConstruct;
import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

//@Component("MultiLingualIntegratedSearchService")
@Service("MultiLingualIntegratedSearchService")
//@Validated
class MultiLingualIntegratedSearchService {

    @Autowired
    private MultiLingualIntegratedSearchValidator validator;

    private RestHighLevelClient elasticClient = ConnectionManager.getClient();
    private Map<String, PropertyDescriptor> propertyDescriptors = null;

    @PostConstruct
    private void extractPropertyDescriptors() {
        propertyDescriptors = Arrays.stream(BeanUtils.getPropertyDescriptors(Filters.class)).filter(pd -> Objects.nonNull(pd.getReadMethod()) && pd.getReadMethod().getDeclaringClass() == Filters.class).collect(Collectors.toMap(FeatureDescriptor::getName, pd -> pd));
    }

    @SuppressWarnings("unchecked")
    private Response searchService(ValidatedSearchData validatedSearchData) throws Exception {
        Map<String, Object> paramsMap = new HashMap<>();

        boolean accessControlEnabled = MultiLingualIntegratedSearchController.isAccessControlEnabled;
        paramsMap.put(SearchConstants.IS_ACCESS_CONTROL_ENABLED, accessControlEnabled);
        if (accessControlEnabled) {
            List<String> accessPaths = validatedSearchData.getAccessPaths();
            List<String> orgs = validatedSearchData.getOrgs();
            paramsMap.put(SearchConstants.ACCESS_PATHS, accessPaths);
            paramsMap.put(SearchConstants.ORG, orgs);
        }

        String rootOrg = validatedSearchData.getRootOrg();
        paramsMap.put(SearchConstants.ROOT_ORG, rootOrg);

        int pageNumber = validatedSearchData.getPageNo();
        int pageSize = validatedSearchData.getPageSize();
        paramsMap.put("from", pageNumber * pageSize);
        paramsMap.put("size", pageSize);

        handleAggregationsSorting(validatedSearchData, paramsMap);

        String query = validatedSearchData.getQuery();
        if (null == query || query.isEmpty() || query.toLowerCase().equals("all") || query.equals("*")) {
            List<Map<SortableFields, SortOrders>> sort = validatedSearchData.getSort();
            if (null != sort && !sort.isEmpty()) {
                List<Map<String, Object>> sortVal = new ArrayList<>();
                for (Map<SortableFields, SortOrders> sortableFieldsSortOrdersMap : sort) {
                    sortableFieldsSortOrdersMap.forEach((k,v)->{
                        if (k.equals(SortableFields.averageRating) || k.equals(SortableFields.totalRating) || k.equals(SortableFields.viewCount) || k.equals(SortableFields.uniqueUsersCount)){
                            sortVal.add(Collections.singletonMap(k.name() + "." + validatedSearchData.getRootOrg(), new HashMap<Object, Object>(){{put("order",v.name());put("nested_path",k.name());}}));
                        }else
                            sortVal.add(Collections.singletonMap(k.name(), v.name()));
                    });
                }
                paramsMap.put("sort", true);
                paramsMap.put("sortVal", sortVal);
            }
        } else if (isPhraseQuery(query)) {
            query = query.substring(1, query.length() - 1);
            paramsMap.put("must", true);
            paramsMap.put("should", false);
            paramsMap.put("searchTerm", query);
        } else {
            if (query.startsWith("lex_") || query.startsWith("do_")) {
                String[] identifierQuery = query.split(SearchConstants.IDENTIFIER_QUERY_DELIMITER);
                List<String> identifiersWithImage = new ArrayList<>();
                for (String s : identifierQuery) {
                    if (!s.endsWith(".img"))
                        identifiersWithImage.add(s+".img");
                    identifiersWithImage.add(s);
                }

                paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + WordUtils.capitalize(SearchConstants.FILTER_IDENTIFIER_FIELD_KEY), true);
                paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + WordUtils.capitalize(SearchConstants.FILTER_IDENTIFIER_FIELD_KEY) + SearchConstants.TEMPLATE_FILTER_SUFFIX, identifiersWithImage);
//                paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + WordUtils.capitalize(SearchConstants.FILTER_STATUS_FIELD_KEY) + SearchConstants.TEMPLATE_FILTER_SUFFIX,validatedSearchData.getFilters().getStatus());
//                validatedSearchData.setLocale(ValidatedSearchData.supportedLocales);
//                SearchResponse searchResponse = fetchFromES(validatedSearchData, paramsMap);
//                return packageResponse(packageSearchResponse(searchResponse, validatedSearchData));
            } else {
                paramsMap.put("must", true);
                paramsMap.put("should", true);
                paramsMap.put("searchTerm", query);
            }
        }

        Boolean isStandAlone = validatedSearchData.getIsStandAlone();
        if (null != isStandAlone) {
            paramsMap.put(SearchConstants.IS_STAND_ALONE, true);
            paramsMap.put(SearchConstants.IS_STAND_ALONE + SearchConstants.TEMPLATE_FILTER_SUFFIX, isStandAlone);
        }


        List userIds = new ArrayList(Arrays.asList(validatedSearchData.getUuid().toString().split(",")));
        boolean isUnderReview = validatedSearchData.getFilters().getStatus().contains(SearchStatuses.Reviewed) || validatedSearchData.getFilters().getStatus().contains(SearchStatuses.InReview);
//         if(isUnderReview){
//             paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + WordUtils.capitalize(SearchConstants.FILTER_TRACK_CONTACTS_FIELD_KEY) , true);
//             paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + WordUtils.capitalize(SearchConstants.FILTER_TRACK_CONTACTS_FIELD_KEY) + SearchConstants.TEMPLATE_FILTER_SUFFIX, userIds);
//         }

        //Added filter records belong to the user // apply for all status
        if(null != validatedSearchData.getIsUserRecordEnabled()){
            Boolean isUserRecordEnabled = validatedSearchData.getIsUserRecordEnabled();
            if (isUserRecordEnabled) {
                paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + WordUtils.capitalize(SearchConstants.FILTER_CREATOR_CONTACTS_FIELD_KEY) , true);
                paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + WordUtils.capitalize(SearchConstants.FILTER_CREATOR_CONTACTS_FIELD_KEY) + SearchConstants.TEMPLATE_FILTER_SUFFIX, userIds);
            }
            //Added filter records not belong to the user //apply for status InReview, Reviewed
            if(!isUserRecordEnabled && (isUnderReview)){
                paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + "Not" +WordUtils.capitalize(SearchConstants.FILTER_CREATOR_CONTACTS_FIELD_KEY) , true);
                paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + WordUtils.capitalize(SearchConstants.FILTER_CREATOR_CONTACTS_FIELD_KEY) + SearchConstants.TEMPLATE_FILTER_SUFFIX, userIds);

            }
        }

        List<String> filtersUsed = new ArrayList<>();
        for (Map.Entry<String, PropertyDescriptor> stringPropertyDescriptorEntry : propertyDescriptors.entrySet()) {
            String capitalizedName = WordUtils.capitalize(stringPropertyDescriptorEntry.getKey());
            List<Object> value = (List<Object>) stringPropertyDescriptorEntry.getValue().getReadMethod().invoke(validatedSearchData.getFilters());
            if (!value.isEmpty()) {
                filtersUsed.add(stringPropertyDescriptorEntry.getKey());
                switch (stringPropertyDescriptorEntry.getKey()) {
//                    case SearchConstants.FILTER_RESOURCE_CATEGORY_FIELD_KEY:
//                    case SearchConstants.FILTER_RESOURCE_TYPE_FIELD_KEY:
//                        if (validatedSearchData.getFilters().getContentType().contains(SearchConstants.RESOURCE)) {
//                            paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + capitalizedName + SearchConstants.TEMPLATE_FILTER_SUFFIX, value);
//                            paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + capitalizedName, true);
//                        } else filtersUsed.remove(stringPropertyDescriptorEntry.getKey());
//                        break;
//                    case SearchConstants.FILTER_IS_EXTERNAL_FIELD_KEY:
//                    case SearchConstants.FILTER_LEARNING_MODE_FIELD_KEY:
//                        if (validatedSearchData.getFilters().getContentType().contains(SearchConstants.COURSE)) {
//                            paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + capitalizedName + SearchConstants.TEMPLATE_FILTER_SUFFIX, value);
//                            paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + capitalizedName, true);
//                        } else filtersUsed.remove(stringPropertyDescriptorEntry.getKey());
//                        break;
                    case SearchConstants.FILTER_LAST_UPDATED_ON_FIELD_KEY:
                        String val = (String) value.get(0);
                        boolean flg = true;
                        switch (val) {
                            case "week":
                                val = "now-1w";
                                break;
                            case "month":
                                val = "now-1M";
                                break;
                            case "year":
                                val = "now-1y";
                                break;
                            default:
                                val = "now/d";
                                flg = false;
                                break;
                        }
                        if (flg) {
                            paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + capitalizedName + "Gte", val);
                            paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + capitalizedName + "Lte", "now/d");
                            paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + capitalizedName, true);
                        }
                        break;
                    case SearchConstants.FILTER_DURATION_FIELD_KEY:
                        Object[] data = (Object[]) value.toArray();
                        int size = data.length;
                        List<Map<String, Object>> items = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            Long[] range = getDurationRangeArray(String.valueOf(data[i]));
                            if (range == null) {
                                throw new BadRequestException("DURATION RANGE INVALID");
                            }
                            Map<String, Object> tempMap = new HashMap<>();
                            tempMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + capitalizedName + "ItemGte", range[0]);
                            tempMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + capitalizedName + "ItemLte", range[1]);
                            if (i != size - 1) {
                                tempMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + capitalizedName + "ItemComma", true);
                            }
                            items.add(tempMap);
                        }
                        paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + capitalizedName + "Item", items);
                        paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + capitalizedName, true);
                        break;
                        // Intentional no break;
                    // ALWAYS KEEP CONTENT TYPE AS LAST CASE
                    case SearchConstants.FILTER_CONTENT_TYPE_FIELD_KEY:
                        if (validatedSearchData.getFilters().getContentType().contains(SearchConstants.COURSE)) {
                            paramsMap.put(SearchConstants.FILTER_LEARNING_MODE_FIELD_KEY + SearchConstants.TEMPLATE_AGGS_SUFFIX, true);
//                            paramsMap.put(SearchConstants.FILTER_IS_EXTERNAL_FIELD_KEY + SearchConstants.TEMPLATE_AGGS_SUFFIX, true);
                        }
                        if (validatedSearchData.getFilters().getContentType().contains(SearchConstants.RESOURCE)) {
                            paramsMap.put(SearchConstants.FILTER_RESOURCE_TYPE_FIELD_KEY + SearchConstants.TEMPLATE_AGGS_SUFFIX, true);
                            paramsMap.put(SearchConstants.FILTER_RESOURCE_CATEGORY_FIELD_KEY + SearchConstants.TEMPLATE_AGGS_SUFFIX, true);
                        }
                        // intentional no break;
                    default:
                        paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + capitalizedName + SearchConstants.TEMPLATE_FILTER_SUFFIX, value);
                        paramsMap.put(SearchConstants.TEMPLATE_FILTER_PREFIX + capitalizedName, true);
                        break;
                }
            }
        }

//        if (Strings.isNullOrEmpty(query) && filtersUsed.isEmpty()) {
//            throw new BadRequestException("Both query or filters can not be empty");
//        }

        System.out.println("Params map "+new ObjectMapper().writeValueAsString(paramsMap));
        SearchResponse searchResponse = fetchFromES(validatedSearchData, paramsMap);

        if (null == isStandAlone && searchResponse.getHits().totalHits == 0)
            throw new NoContentException("NO DATA FOUND FOR QUERY AND FILTERS COMBINATIONS");

        if (searchResponse.getHits().totalHits > 0) {
//            packageSearchResponse(searchResponse, validatedSearchData);
        } else {
            if (!validatedSearchData.getIsCatalog()) {
                if (null != isStandAlone && isStandAlone) {
                    validatedSearchData.setIsStandAlone(null);
                    return searchService(validatedSearchData);
                }
            }
        }


        Map<String, Object> result = packageSearchResponse(searchResponse, validatedSearchData);
        result.put("filtersUsed", filtersUsed);
        return packageResponse(result);
    }

    private void handleAggregationsSorting(ValidatedSearchData validatedSearchData, Map<String, Object> paramsMap) {
//        Map<String, List<String>> aggAndFilterNamesMap = getAggAndFilterNamesMap(validatedSearchData);
//        for (Map.Entry<String, List<String>> pair : aggAndFilterNamesMap.entrySet()) {
//            String key = pair.getKey().replace("_aggs", "");
//            SortableAggregations x = null;
//            try {
//                x = SortableAggregations.valueOf(key);
//            } catch (IllegalArgumentException ex){
//
//            }
//            if (null == validatedSearchData.getAggregationsSorting().getOrDefault(x, null))
//                paramsMap.put(pair.getKey()+"_orders", Collections.singletonList(Collections.singletonMap("_count","desc")));
//            else {
//                List<Map<AggregationSortType, SortOrders>> sort = validatedSearchData.getAggregationsSorting().get(x);
////                List<Map<String, String>> sortVal = new ArrayList<>();
////                sort.forEach(i->{
////
////                });
//                paramsMap.put(pair.getKey()+"_orders", sort);
//            }
//        }
    }

    private Response packageResponse(Map<String, Object> result) {
        Response response = new Response();
        response.put("response", result);
        return response;
    }

    private Map<String, Object> packageSearchResponse(SearchResponse searchResponse, ValidatedSearchData validatedSearchData) {

        List<Map<String, Object>> filtersMap = new ArrayList<>();
        List<Map<String, Object>> nonDisplayableFiltersMap = new ArrayList<>();
        List<Object> resultMetaList = new ArrayList<>();

        if (searchResponse.getHits().totalHits > 0) {

            if (null != searchResponse.getAggregations()) {
                Map<String, List<String>> aggAndFilterNamesMap = getAggAndFilterNamesMap(validatedSearchData);

                for (Map.Entry<String, List<String>> pair : aggAndFilterNamesMap.entrySet()) {
                    if (pair.getKey().equals(SearchConstants.LABELS_AGGS_KEY))
                        parseFilters(nonDisplayableFiltersMap, searchResponse, pair.getKey(), pair.getValue().get(0), pair.getValue().get(1));
                    else
                        parseFilters(filtersMap, searchResponse, pair.getKey(), pair.getValue().get(0), pair.getValue().get(1));
                }

                if (!validatedSearchData.getRootOrg().equals("")) {
                    List<FilterItem> filterItemList = new ArrayList<>();
                    FilterItem filterItem = new FilterItem();
                    filterItem.setType("week");
                    filterItem.setDisplayName("Last Week");
                    filterItemList.add(filterItem);
                    filterItem = new FilterItem();
                    filterItem.setType("month");
                    filterItem.setDisplayName("Last Month");
                    filterItemList.add(filterItem);
                    filterItem = new FilterItem();
                    filterItem.setType("year");
                    filterItem.setDisplayName("Last Year");
                    filterItemList.add(filterItem);
                    filterItem = new FilterItem();
                    filterItem.setType("older");
                    filterItem.setDisplayName("Older");
                    filterItemList.add(filterItem);

                    Map<String, Object> topicAggMap = new HashMap<>();
                    topicAggMap.put("type", SearchConstants.FIELD_DISPLAYNAME_PAIR.get(SearchConstants.LAST_UPDATED_ON_AGGS_KEY).get(0));
                    topicAggMap.put("displayName", SearchConstants.FIELD_DISPLAYNAME_PAIR.get(SearchConstants.LAST_UPDATED_ON_AGGS_KEY).get(1));
                    topicAggMap.put("content", filterItemList);
                    filtersMap.add(topicAggMap);
                }
            }

            for (SearchHit hit : searchResponse.getHits()) {
                postProcessSearchHit(hit.getSourceAsMap(),validatedSearchData);
                resultMetaList.add(hit.getSourceAsMap());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalHits", searchResponse.getHits().totalHits);
        result.put("result", resultMetaList);
        result.put("filters", filtersMap);
        result.put("notToBeShownFilters", nonDisplayableFiltersMap);

        return result;
    }

    private void postProcessSearchHit(Map<String, Object> data, ValidatedSearchData validatedSearchData) {
        Object viewCount = ((Map<String, Object>) data.getOrDefault(SearchConstants.VIEW_COUNT, new HashMap<>())).getOrDefault(validatedSearchData.getRootOrg(), 0.0);
        Object averageRating = ((Map<String, Object>) data.getOrDefault(SearchConstants.AVERAGE_RATING, new HashMap<>())).getOrDefault(validatedSearchData.getRootOrg(), 0.0);
        Object uniqueUsersCount = ((Map<String, Object>) data.getOrDefault(SearchConstants.UNIQUE_USERS_COUNT, new HashMap<>())).getOrDefault(validatedSearchData.getRootOrg(), 0.0);
        Object totalRating = ((Map<String, Object>) data.getOrDefault(SearchConstants.TOTAL_RATING, new HashMap<>())).getOrDefault(validatedSearchData.getRootOrg(), 0.0);

        data.put("viewCount",viewCount);
        data.put("averageRating",averageRating);
        data.put("uniqueUsersCount",uniqueUsersCount);
        data.put("totalRating",totalRating);
    }

    private SearchResponse fetchFromES(ValidatedSearchData validatedSearchData, Map<String, Object> paramsMap) throws Exception {
        List<String> indices = new ArrayList<>();
        if (validatedSearchData.getLocale().isEmpty())
            validatedSearchData.setLocale(ValidatedSearchData.supportedLocales);
        for (String searchIndexLocale : validatedSearchData.getLocale()) {
            indices.add(LexProjectUtil.EsIndex.multi_lingual_search_index.getIndexName() + SearchConstants.SEARCH_INDEX_LOCALE_DELIMITER + searchIndexLocale);
        }
        SearchRequest searchRequest = new SearchRequest().searchType(SearchType.QUERY_THEN_FETCH);
        searchRequest.indices(indices.toArray(new String[0]));
        searchRequest.types(LexProjectUtil.EsType.new_lex_search.getTypeName());

        SearchTemplateRequest templateRequest = new SearchTemplateRequest();
        templateRequest.setScript(SearchConstants.ML_SEARCH_TEMPLATE);
        templateRequest.setScriptType(ScriptType.STORED);
        templateRequest.setScriptParams(paramsMap);
        templateRequest.setRequest(searchRequest);

        return elasticClient.searchTemplate(templateRequest, RequestOptions.DEFAULT).getResponse();
    }

    private void parseFilters(List<Map<String, Object>> completeMap, SearchResponse searchResponse, String aggregationName, String filterName, String displayName) {
        ParsedGlobal internalGlobalAggs = searchResponse.getAggregations().get("TotalAggs");
        boolean isTagsAgg = false;
        List<Object> filterItemList = new ArrayList<>();
        List<Map<String, Object>> allRootNodes = new ArrayList<>();
        if (aggregationName.equals(SearchConstants.DURATION_AGGS_KEY)) {
            ParsedFilter internalFilter = internalGlobalAggs.getAggregations().get(aggregationName);
            Range aggregation = internalFilter.getAggregations().get(aggregationName);
            for (Range.Bucket bucket : aggregation.getBuckets()) {
                FilterItem filterItem = new FilterItem();
                if (bucket.getKeyAsString() == null || bucket.getKeyAsString().isEmpty()) {
                } else if (bucket.getDocCount() > 0) {
                    if (bucket.getKeyAsString().equals(SearchConstants.DURATION_SHORT_DISPLAY_NAME))
                        filterItem.setType(SearchConstants.DURATION_SHORT);
                    else if (bucket.getKeyAsString().equals(SearchConstants.DURATION_MEDIUM_DISPLAY_NAME))
                        filterItem.setType(SearchConstants.DURATION_MEDIUM);
                    else if (bucket.getKeyAsString().equals(SearchConstants.DURATION_LONG_DISPLAY_NAME))
                        filterItem.setType(SearchConstants.DURATION_LONG);
                    else
                        filterItem.setType(SearchConstants.DURATION_DETAILED);
                    filterItem.setDisplayName(bucket.getKeyAsString());
                    filterItem.setCount(bucket.getDocCount());
                    filterItemList.add(filterItem);
                }
            }
        } else if (aggregationName.equals(SearchConstants.CATALOG_PATHS_AGGS_KEY)) {
            isTagsAgg = true;
            ParsedFilter internalFilter = internalGlobalAggs.getAggregations().get(aggregationName);
            Terms aggregation = internalFilter.getAggregations().get(aggregationName);
            TreeNode root = new TreeNode("ROOT", "ROOT", -1l);

            for (Terms.Bucket bucket : aggregation.getBuckets()) {
                if (bucket.getKeyAsString() == null || bucket.getKeyAsString().isEmpty()) {
                } else if (bucket.getDocCount() > 0) {
                    TreeNode cur = root;
                    for (String key : bucket.getKeyAsString().split(SearchConstants.TAGS_PATH_DELIMITER)) {
                        cur = cur.addChild(new TreeNode(key, bucket.getKeyAsString(), bucket.getDocCount()));
                    }
                }
            }

            for (TreeNode child : root.children) {
                allRootNodes.add(parseTree(child));
            }
        } else if (aggregationName.equals(SearchConstants.CONCEPTS_AGGS_KEY)) {

            ParsedFilter internalFilter = internalGlobalAggs.getAggregations().get(aggregationName);
            ParsedNested internalNested = internalFilter.getAggregations().get(aggregationName);
            Map<String, Aggregation> subAggregation = internalNested.getAggregations().asMap();
            Terms termsAgg = (Terms) subAggregation.get(SearchConstants.CONCEPTS_IDENTIFIER_AGGS_KEY);
            for (Terms.Bucket bucket : termsAgg.getBuckets()) {
                Map<String, Aggregation> subSubAggregation = bucket.getAggregations().asMap();
                Terms subTermsAgg = (Terms) subSubAggregation.get(SearchConstants.CONCEPTS_NAME_AGGS_KEY);
                Terms.Bucket subBucket = subTermsAgg.getBuckets().get(0);
                FilterItem filterItem = new FilterItem();
                if (bucket.getKeyAsString() == null || bucket.getKeyAsString().isEmpty()) {
                } else if (bucket.getDocCount() > 0) {
                    filterItem.setType(subBucket.getKeyAsString());
                    filterItem.setDisplayName(subBucket.getKeyAsString());
                    filterItem.setId(bucket.getKeyAsString());
                    filterItem.setCount(bucket.getDocCount());
                    filterItemList.add(filterItem);
                }
            }
        } else if (aggregationName.equals(SearchConstants.IS_EXTERNAL_AGGS_KEY)) {
            ParsedFilter internalFilter = internalGlobalAggs.getAggregations().get(aggregationName);
            ParsedLongTerms stringTerms = internalFilter.getAggregations().get(aggregationName);
            for (Terms.Bucket bucket : stringTerms.getBuckets()) {
                FilterItem filterItem = new FilterItem();
                if (bucket.getKeyAsString() == null || bucket.getKeyAsString().isEmpty()) {
                } else if (bucket.getDocCount() > 0) {
                    filterItem.setType(bucket.getKeyAsString());
                    if (bucket.getKeyAsString().equals("true"))
                        filterItem.setDisplayName("Yes");
                    else
                        filterItem.setDisplayName("No");
                    filterItem.setCount(bucket.getDocCount());
                    filterItemList.add(filterItem);
                }
            }
        }  else if (aggregationName.equals(SearchConstants.EXCLUSIVE_CONTENT_AGGS_KEY)) {
            ParsedFilter internalFilter = internalGlobalAggs.getAggregations().get(aggregationName);
            ParsedLongTerms stringTerms = internalFilter.getAggregations().get(aggregationName);
            for (Terms.Bucket bucket : stringTerms.getBuckets()) {
                FilterItem filterItem = new FilterItem();
                if (bucket.getKeyAsString() == null || bucket.getKeyAsString().isEmpty()) {
                } else if (bucket.getDocCount() > 0) {
                    filterItem.setType(bucket.getKeyAsString());
                    if (bucket.getKeyAsString().equals("true"))
                        filterItem.setDisplayName("With costs");
                    else
                        filterItem.setDisplayName("Free");
                    filterItem.setCount(bucket.getDocCount());
                    filterItemList.add(filterItem);
                }
            }
        } else if (aggregationName.equals(SearchConstants.CONTENT_TYPE_AGGS_KEY)) {
            ParsedFilter internalFilter = internalGlobalAggs.getAggregations().get(aggregationName);
            ParsedStringTerms stringTerms = internalFilter.getAggregations().get(aggregationName);
            for (Terms.Bucket bucket : stringTerms.getBuckets()) {
                FilterItem filterItem = new FilterItem();
                if (bucket.getKeyAsString() == null || bucket.getKeyAsString().isEmpty()) {
                } else if (bucket.getDocCount() > 0) {
                    filterItem.setType(bucket.getKeyAsString());
                    if (bucket.getKeyAsString().equals(SearchConstants.LEARNING_MODULE))
                        filterItem.setDisplayName("Module");
                    else if (bucket.getKeyAsString().equals(SearchConstants.LEARNING_PATH))
                        filterItem.setDisplayName("Program");
                    else
                        filterItem.setDisplayName(bucket.getKeyAsString());
                    filterItem.setCount(bucket.getDocCount());
                    filterItemList.add(filterItem);
                }
            }
        } else {
            ParsedFilter internalFilter = internalGlobalAggs.getAggregations().get(aggregationName);
            ParsedStringTerms stringTerms = internalFilter.getAggregations().get(aggregationName);
            for (Terms.Bucket bucket : stringTerms.getBuckets()) {
                FilterItem filterItem = new FilterItem();
                if (bucket.getKeyAsString() == null || bucket.getKeyAsString().isEmpty()) {
                } else if (bucket.getDocCount() > 0) {
                    filterItem.setType(bucket.getKeyAsString());
                    filterItem.setDisplayName(bucket.getKeyAsString());
                    filterItem.setCount(bucket.getDocCount());
                    filterItemList.add(filterItem);
                }
            }
        }

        if (!allRootNodes.isEmpty() || !filterItemList.isEmpty()) {
            Map<String, Object> topicAggMap = new HashMap<String, Object>();
            topicAggMap.put("type", filterName);
            topicAggMap.put("displayName", displayName);
            if (isTagsAgg) {
                topicAggMap.put("content", allRootNodes);
            } else {
                topicAggMap.put("content", filterItemList);
            }
            completeMap.add(topicAggMap);
        }
    }

    private Map<String, Object> parseTree(TreeNode root) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> childrenList = new ArrayList<>();
        map.put("type", root.type);
        map.put("displayName", root.key);
        map.put("count", root.count);
        if (!root.children.isEmpty()) {
            for (TreeNode child : root.children) {
                childrenList.add(parseTree(child));
            }
        }
        map.put("children", childrenList);
        return map;
    }

    private Map<String, List<String>> getAggAndFilterNamesMap(ValidatedSearchData validatedSearchData) {
        Map<String, List<String>> data = new HashMap<>(SearchConstants.aggAndFilterNamesMap);
//        if (validatedSearchData.getFilters().getContentType().contains(SearchConstants.COURSE)) {
//            data.put(SearchConstants.IS_EXTERNAL_AGGS_KEY, SearchConstants.FIELD_DISPLAYNAME_PAIR.get(SearchConstants.IS_EXTERNAL_AGGS_KEY));
            data.put(SearchConstants.LEARNING_MODE_AGGS_KEY, SearchConstants.FIELD_DISPLAYNAME_PAIR.get(SearchConstants.LEARNING_MODE_AGGS_KEY));
//        }
//        if (validatedSearchData.getFilters().getContentType().contains(SearchConstants.RESOURCE)) {
//            data.put(SearchConstants.RESOURCE_CATEGORY_AGGS_KEY, SearchConstants.FIELD_DISPLAYNAME_PAIR.get(SearchConstants.RESOURCE_CATEGORY_AGGS_KEY));
            data.put(SearchConstants.RESOURCE_TYPE_AGGS_KEY, SearchConstants.FIELD_DISPLAYNAME_PAIR.get(SearchConstants.RESOURCE_TYPE_AGGS_KEY));
//        }
//        System.out.println(data);
        return data;
    }

    private Long[] getDurationRangeArray(String string) {
        Long[] rangeArray = new Long[2];
        switch (string) {
            case SearchConstants.DURATION_SHORT:
                rangeArray[0] = Long.MIN_VALUE;
                rangeArray[1] = SearchConstants.DURATION_SHORT_MAX;
                break;
            case SearchConstants.DURATION_MEDIUM:
                rangeArray[0] = SearchConstants.DURATION_MEDIUM_MIN;
                rangeArray[1] = SearchConstants.DURATION_MEDIUM_MAX;
                break;
            case SearchConstants.DURATION_LONG:
                rangeArray[0] = SearchConstants.DURATION_LONG_MIN;
                rangeArray[1] = SearchConstants.DURATION_LONG_MAX;
                break;
            case SearchConstants.DURATION_DETAILED:
                rangeArray[0] = SearchConstants.DURATION_DETAILED_MIN;
                rangeArray[1] = Long.MAX_VALUE;
                break;

            default:
                return null;
        }
        return rangeArray;
    }

    private boolean isPhraseQuery(String query) {
        return !query.equals("\"") && !query.equals("\"\"") && query.charAt(0) == '"'
                && query.charAt(query.length() - 1) == '"'
                || (!query.equals("'") && !query.equals("''") && query.charAt(0) == '\''
                && query.charAt(query.length() - 1) == '\'');
    }

    final Response callSearchService(ValidatedSearchData validatedSearchData) throws Exception {
        validatedSearchData = validator.doValidations(validatedSearchData);
        return searchService(validatedSearchData);
    }

    private class TreeNode {
        private final Set<TreeNode> children = new LinkedHashSet<>();
        private final String type;
        private final Long count;
        private final String key;

        TreeNode(String key, String type, Long count) {
            this.type = type;
            this.count = count;
            this.key = key;
        }

        TreeNode addChild(TreeNode newChild) {
            for (TreeNode child : children) {
                if (child.key.equals(newChild.key)) {
                    return child;
                }
            }
            children.add(new TreeNode(newChild.key, newChild.type, newChild.count));
            return newChild;
        }
    }

}

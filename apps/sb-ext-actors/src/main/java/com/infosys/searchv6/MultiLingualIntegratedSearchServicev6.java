/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
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
package com.infosys.searchv6;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.infosys.elastic.helper.ConnectionManager;
import com.infosys.exception.BadRequestException;
import com.infosys.exception.NoContentException;
import com.infosys.model.FilterItem;
import com.infosys.searchv6.util.SearchConstantsv6;
import com.infosys.searchv6.validations.model.*;
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
import org.elasticsearch.search.suggest.Suggest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

//@Component("MultiLingualIntegratedSearchServicev6")
@Service
//@Validated
class MultiLingualIntegratedSearchServicev6 {

    private List<String> defaultSourceFields = new ArrayList<>(Arrays.asList("hasAssessment", "locale", "subTitle", "totalLikes", "sourceName", "sourceShortName", "sourceIconUrl", "isStandAlone", "isInIntranet", "deliveryLanguages", "deliveryCountries", "costCenter", "exclusiveContent", "instanceCatalog", "price", "isContentEditingDisabled", "isMetaEditingDisabled", "labels", "publishedOn", "expiryDate", "hasTranslations", "isTranslationOf", "viewCount", "averageRating", "uniqueUsersCount", "totalRating", "collections", "unit", "status", "isExternal", "learningMode", "uniqueLearners", "name", "identifier", "description", "resourceType", "contentType", "isExternal", "appIcon", "artifactUrl", "children", "mimeType", "creatorContacts", "downloadUrl", "duration", "me_totalSessionsCount", "size", "complexityLevel", "lastUpdatedOn", "resourceCategory", "msArtifactDetails", "isIframeSupported", "contentUrlAtSource", "certificationUrl", "certificationList", "skills", "topics", "creatorDetails", "catalogPaths", "learningObjective", "preRequisites", "softwareRequirements", "systemRequirements", "track", "idealScreenSize", "minLexVersion", "preContents", "postContents", "isExternal", "certificationStatus", "subTitles", "publisherDetails", "trackContacts", "creatorContacts", "appIcon", "trackContacts", "publisherDetails"));

    @Autowired
    private MultiLingualIntegratedSearchValidatorV6 validator;

    @Autowired
    private NestedSortableFieldsMappings nestedSortableFieldsMappings;

    @Value("${com.infosys.search.did-you-mean.suggestion}")
    private boolean didYouMeanSuggestionEnabled;

    @Value("${com.infosys.search.did-you-mean.defaulting}")
    private boolean didYouMeanDefaultingEnabled;

    private RestHighLevelClient elasticClient = ConnectionManager.getClient();
    private Map<String, PropertyDescriptor> andFiltersPropertyDescriptors = null;
    private Map<String, PropertyDescriptor> notFiltersPropertyDescriptors = null;

    @PostConstruct
    private void extractPropertyDescriptors() {
        andFiltersPropertyDescriptors = Arrays.stream(BeanUtils.getPropertyDescriptors(AndFilters.class)).filter(pd -> Objects.nonNull(pd.getReadMethod()) && pd.getReadMethod().getDeclaringClass() == AndFilters.class).collect(Collectors.toMap(FeatureDescriptor::getName, pd -> pd));
        notFiltersPropertyDescriptors = Arrays.stream(BeanUtils.getPropertyDescriptors(NotFilters.class)).filter(pd -> Objects.nonNull(pd.getReadMethod()) && pd.getReadMethod().getDeclaringClass() == NotFilters.class).collect(Collectors.toMap(FeatureDescriptor::getName, pd -> pd));
    }

    @SuppressWarnings("unchecked")
    private Map<String,Object> searchService(ValidatedSearchData validatedSearchData) throws Exception {
        Map<String, Object> paramsMap = new HashMap<>();

        handleAccessControl(validatedSearchData, paramsMap);

        handleMisc(validatedSearchData, paramsMap);

        handleQueryString(validatedSearchData, paramsMap);

        Boolean isStandAlone = validatedSearchData.getIsStandAlone();
        if (null != isStandAlone) {
            paramsMap.put(SearchConstantsv6.IS_STAND_ALONE, true);
            paramsMap.put(SearchConstantsv6.IS_STAND_ALONE + SearchConstantsv6.TEMPLATE_FILTER_SUFFIX, isStandAlone);
        }

        List<String> filtersUsed = new ArrayList<>();
        handleFilters(validatedSearchData, paramsMap, filtersUsed);

        handleAggregations(validatedSearchData, paramsMap);

        if (Strings.isNullOrEmpty(validatedSearchData.getQuery()) && filtersUsed.isEmpty()) {
            throw new BadRequestException("Both query or filters can not be empty");
        }

        if(didYouMeanSuggestionEnabled || didYouMeanDefaultingEnabled)
            paramsMap.put("didYouMean",true);

        SearchResponse searchResponse = fetchFromES(validatedSearchData, paramsMap);

        Optional<Suggest.Suggestion.Entry.Option> bestOption = Optional.empty();

        if (didYouMeanSuggestionEnabled && validatedSearchData.getDidYouMean()) {
            if (null != searchResponse.getSuggest()) {
                List<Suggest.Suggestion.Entry.Option> options = new ArrayList<>();

                searchResponse.getSuggest()
                        .iterator()
                        .forEachRemaining(item -> item.getEntries()
                                .forEach(entry -> options.addAll(entry.getOptions())));

                bestOption = options.stream()
                        .max(Comparator.comparing(Suggest.Suggestion.Entry.Option::getScore));
            }
        }

        if (null == isStandAlone && searchResponse.getHits().totalHits == 0) {
            if (didYouMeanDefaultingEnabled && validatedSearchData.getDidYouMean()) {
                if (bestOption.isPresent()) {
                    if (!validatedSearchData.getIsCatalog()) {
                        validatedSearchData.setQuery(bestOption.get().getText().string());
                        validatedSearchData.setIsStandAlone(true);
                        validatedSearchData.setChangedQuery(bestOption.get().getText().string());
                        validatedSearchData.setDidYouMean(false);
                        return searchService(validatedSearchData);
                    }
                } else
                    throw new NoContentException("NO DATA FOUND FOR QUERY AND FILTERS COMBINATIONS");
            } else
                throw new NoContentException("NO DATA FOUND FOR QUERY AND FILTERS COMBINATIONS");
        }

        if (searchResponse.getHits().totalHits == 0) {
            if (!validatedSearchData.getIsCatalog()) {
                if (null != isStandAlone && isStandAlone) {
                    validatedSearchData.setIsStandAlone(null);
                    return searchService(validatedSearchData);
                }
            }
        }

        return packageSearchResponse(searchResponse, validatedSearchData, filtersUsed, bestOption);
    }

    private void handleAggregations(ValidatedSearchData validatedSearchData, Map<String, Object> paramsMap) {
        validatedSearchData.getVisibleFilters()
                            .forEach((key,val) -> {
                                paramsMap.put(key + SearchConstantsv6.TEMPLATE_AGGS_SUFFIX, true);
                                paramsMap.put(key + SearchConstantsv6.TEMPLATE_AGGS_ORDER_SUFFIX, val.getOrder());
                            });

        validatedSearchData.getNotVisibleFilters()
                            .forEach((key,val) -> {
                                paramsMap.put(key + SearchConstantsv6.TEMPLATE_AGGS_SUFFIX, true);
                                paramsMap.put(key + SearchConstantsv6.TEMPLATE_AGGS_ORDER_SUFFIX, val.getOrder());
                            });
    }

    private void handleQueryString(ValidatedSearchData validatedSearchData, Map<String, Object> paramsMap) {
        String query = validatedSearchData.getQuery();
        if (null == query || query.isEmpty() || query.toLowerCase().equals("all") || query.equals("*")) {
            handleSorting(validatedSearchData, paramsMap);
        } else if (isPhraseQuery(query)) {
            query = query.substring(1, query.length() - 1);
            paramsMap.put("must", true);
            paramsMap.put("should", false);
            paramsMap.put("searchTerm", query);
        } else {
            if (query.startsWith("lex_") || query.startsWith("do_")) {
                String[] identifierQuery = validatedSearchData.getQuery().split(SearchConstants.IDENTIFIER_QUERY_DELIMITER);
                List<String> identifiersWithImage = new ArrayList<>();
                for (String s : identifierQuery) {
                    if (!s.endsWith(SearchConstantsv6.IMAGE_POSTFIX))
                        identifiersWithImage.add(s + SearchConstantsv6.IMAGE_POSTFIX);
                    identifiersWithImage.add(s);
                }
                paramsMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + WordUtils.capitalize(SearchConstantsv6.FILTER_IDENTIFIER_FIELD_KEY), true);
                paramsMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + WordUtils.capitalize(SearchConstantsv6.FILTER_IDENTIFIER_FIELD_KEY) + SearchConstantsv6.TEMPLATE_FILTER_SUFFIX, identifiersWithImage);
            } else {
                paramsMap.put("must", false);
                paramsMap.put("should", true);
                paramsMap.put("searchTerm", query);
            }
        }
    }

    private void handleSorting(ValidatedSearchData validatedSearchData, Map<String, Object> paramsMap) {
        List<Map<SortableFields, SortOrders>> sort = validatedSearchData.getSort();
        if (null != sort && !sort.isEmpty()) {
            List<Map<String, Object>> sortVal = new ArrayList<>();
            for (Map<SortableFields, SortOrders> sortableFieldsSortOrdersMap : sort) {
                sortableFieldsSortOrdersMap.forEach((k,v)->{
                    if (k.equals(SortableFields.averageRating) || k.equals(SortableFields.totalRating) || k.equals(SortableFields.viewCount) || k.equals(SortableFields.uniqueUsersCount))
                        sortVal.add(Collections.singletonMap(k.name() + "." + validatedSearchData.getRootOrg(), new HashMap<Object, Object>(){{put("order",v.name());put("nested_path",k.name());}}));
                    else if (k.equals(SortableFields.addedOn))
                        sortVal.add(Collections.singletonMap(nestedSortableFieldsMappings.getParent(k) + "." + k.name(),new HashMap<Object, Object>(){{put("order",v.name());put("nested_path",nestedSortableFieldsMappings.getParent(k));}}));
                    else
                        sortVal.add(Collections.singletonMap(k.name(), v.name()));
                });
            }
            paramsMap.put("sort", true);
            paramsMap.put("sortVal", sortVal);
        }
    }

    private void handleMisc(ValidatedSearchData validatedSearchData, Map<String, Object> paramsMap) {
        paramsMap.put(SearchConstantsv6.ROOT_ORG, validatedSearchData.getRootOrg());

        paramsMap.put("from", validatedSearchData.getPageNo() * validatedSearchData.getPageSize());
        paramsMap.put("size", validatedSearchData.getPageSize());

        List<String> sourceFields = new ArrayList<>(defaultSourceFields);
        if (!validatedSearchData.getExcludeSourceFields().isEmpty())
            sourceFields.removeAll(validatedSearchData.getExcludeSourceFields());
        if (!validatedSearchData.getIncludeSourceFields().isEmpty())
            sourceFields.addAll(validatedSearchData.getIncludeSourceFields());
        paramsMap.put("fetchSource", sourceFields);

        paramsMap.put("searchFieldsWithBoost", validatedSearchData.getSearchOn());
    }

    private void handleAccessControl(ValidatedSearchData validatedSearchData, Map<String, Object> paramsMap) {
        boolean accessControlEnabled = MultiLingualIntegratedSearchControllerv6.isAccessControlEnabled;
        paramsMap.put(SearchConstantsv6.IS_ACCESS_CONTROL_ENABLED, accessControlEnabled);
        if (accessControlEnabled) {
            List<String> accessPaths = validatedSearchData.getAccessPaths();
            List<String> orgs = validatedSearchData.getOrgs();
            paramsMap.put(SearchConstantsv6.ACCESS_PATHS, accessPaths);
            paramsMap.put(SearchConstantsv6.ORG, orgs);
        }
    }

    private void handleFilters(ValidatedSearchData validatedSearchData, Map<String, Object> paramsMap, List<String> filtersUsed) throws InvocationTargetException, IllegalAccessException {
        if (validatedSearchData.getFilters().size() == 0){
            validatedSearchData.setFilters(Collections.singletonList(new FiltersGroup() {{
                setAndFilters(Collections.singletonList(new AndFilters() {{
                    setStatus(Arrays.asList(SearchStatuses.Live, SearchStatuses.MarkedForDeletion));
                }}));
            }}));
        }

        paramsMap.put("filters",true);
        List<Object> filterItems = new ArrayList<>();
        boolean moreFilterItem = false;
        for (FiltersGroup filtersGroup : validatedSearchData.getFilters()) {
            Map<String,Object> filtersGroupMap = new HashMap<>();
            if (filtersGroup.getAndFilters().size() == 0){
                filtersGroup.setAndFilters(Collections.singletonList(new AndFilters() {{
                    setStatus(Arrays.asList(SearchStatuses.Live, SearchStatuses.MarkedForDeletion));
                }}));
            }
            for (AndFilters andFilter : filtersGroup.getAndFilters()) {
                filtersGroupMap.put("mustFilter",true);
                populateAndFilters(andFilter,filtersGroupMap,filtersUsed);
            }
            for (NotFilters notFilter : filtersGroup.getNotFilters()) {
                filtersGroupMap.put("mustNotFilter",true);
                populateNotFilters(notFilter,filtersGroupMap,filtersUsed);
            }
            if (moreFilterItem)
                filtersGroupMap.put("filterItemComma",true);
            moreFilterItem = true;
            filterItems.add(filtersGroupMap);
        }
        paramsMap.put("filterItem",filterItems);
    }

    private void populateAndFilters(AndFilters filters, Map<String, Object> filtersGroupMap, List<String> filtersUsed) throws InvocationTargetException, IllegalAccessException {

        for (Map.Entry<String, PropertyDescriptor> stringPropertyDescriptorEntry : andFiltersPropertyDescriptors.entrySet()) {
            String name = stringPropertyDescriptorEntry.getKey();
            String capitalizedName = WordUtils.capitalize(stringPropertyDescriptorEntry.getKey());
            List<Object> value = (List<Object>) stringPropertyDescriptorEntry.getValue().getReadMethod().invoke(filters);
            populateFilters(name, capitalizedName, value, filtersGroupMap, filtersUsed, SearchConstantsv6.TEMPLATE_AND_FILTER_PREFIX);
        }
    }

    private void populateNotFilters(NotFilters filters, Map<String, Object> filtersGroupMap, List<String> filtersUsed) throws InvocationTargetException, IllegalAccessException {

        for (Map.Entry<String, PropertyDescriptor> stringPropertyDescriptorEntry : notFiltersPropertyDescriptors.entrySet()) {
            String name = stringPropertyDescriptorEntry.getKey();
            String capitalizedName = WordUtils.capitalize(stringPropertyDescriptorEntry.getKey());
            List<Object> value = (List<Object>) stringPropertyDescriptorEntry.getValue().getReadMethod().invoke(filters);
            populateFilters(name, capitalizedName, value, filtersGroupMap, filtersUsed, SearchConstantsv6.TEMPLATE_NOT_FILTER_PREFIX);
        }
    }

    private void populateFilters(String name, String capitalizedName, List<Object> value, Map<String, Object> filtersGroupMap, List<String> filtersUsed, String templateFilterPrefix){
        if (!value.isEmpty()) {
            filtersUsed.add(name);
            switch (name) {
//                    case SearchConstantsv6.FILTER_RESOURCE_CATEGORY_FIELD_KEY:
//                    case SearchConstantsv6.FILTER_RESOURCE_TYPE_FIELD_KEY:
//                        if (filters.getContentType().contains(SearchConstantsv6.RESOURCE)) {
//                            filtersGroupMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + templateFilterPrefix +  capitalizedName + SearchConstantsv6.TEMPLATE_FILTER_SUFFIX, value);
//                            filtersGroupMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + templateFilterPrefix +  capitalizedName, true);
//                        } else filtersUsed.remove(stringPropertyDescriptorEntry.getKey());
//                        break;
//                    case SearchConstantsv6.FILTER_IS_EXTERNAL_FIELD_KEY:
//                    case SearchConstantsv6.FILTER_LEARNING_MODE_FIELD_KEY:
//                        if (validatedSearchData.getFilters().getContentType().contains(SearchConstantsv6.COURSE)) {
//                            paramsMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + templateFilterPrefix +  capitalizedName + SearchConstantsv6.TEMPLATE_FILTER_SUFFIX, value);
//                            paramsMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + templateFilterPrefix +  capitalizedName, true);
//                        } else filtersUsed.remove(stringPropertyDescriptorEntry.getKey());
//                        break;
                case SearchConstantsv6.FILTER_LAST_UPDATED_ON_FIELD_KEY:
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
                        filtersGroupMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + templateFilterPrefix +  capitalizedName + "Gte", val);
                        filtersGroupMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + templateFilterPrefix +  capitalizedName + "Lte", "now/d");
                        filtersGroupMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + templateFilterPrefix +  capitalizedName, true);
                    }
                    break;
                case SearchConstantsv6.FILTER_DURATION_FIELD_KEY:
                    Object[] data = value.toArray();
                    int size = data.length;
                    List<Map<String, Object>> items = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        Long[] range = getDurationRangeArray(String.valueOf(data[i]));
                        if (range == null) {
                            throw new BadRequestException("DURATION RANGE INVALID");
                        }
                        Map<String, Object> tempMap = new HashMap<>();
                        tempMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + templateFilterPrefix +  capitalizedName + "ItemGte", range[0]);
                        tempMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + templateFilterPrefix +  capitalizedName + "ItemLte", range[1]);
                        if (i != size - 1) {
                            tempMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + templateFilterPrefix +  capitalizedName + "ItemComma", true);
                        }
                        items.add(tempMap);
                    }
                    filtersGroupMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + templateFilterPrefix +  capitalizedName + "Item", items);
                    filtersGroupMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + templateFilterPrefix +  capitalizedName, true);
                    break;
                // ALWAYS KEEP CONTENT TYPE AS LAST CASE
//                    case SearchConstantsv6.FILTER_CONTENT_TYPE_FIELD_KEY:
//                        if (filters.getContentType().contains(SearchConstantsv6.COURSE)) {
//                            filtersGroupMap.put(SearchConstantsv6.FILTER_LEARNING_MODE_FIELD_KEY + SearchConstantsv6.TEMPLATE_AGGS_SUFFIX, true);
////                            paramsMap.put(SearchConstantsv6.FILTER_IS_EXTERNAL_FIELD_KEY + SearchConstantsv6.TEMPLATE_AGGS_SUFFIX, true);
//                        }
//                        if (filters .getContentType().contains(SearchConstantsv6.RESOURCE)) {
//                            filtersGroupMap.put(SearchConstantsv6.FILTER_RESOURCE_TYPE_FIELD_KEY + SearchConstantsv6.TEMPLATE_AGGS_SUFFIX, true);
//                            filtersGroupMap.put(SearchConstantsv6.FILTER_RESOURCE_CATEGORY_FIELD_KEY + SearchConstantsv6.TEMPLATE_AGGS_SUFFIX, true);
//                        }

                // Intentional no break;
                default:
                    filtersGroupMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + templateFilterPrefix +  capitalizedName + SearchConstantsv6.TEMPLATE_FILTER_SUFFIX, value);
                    filtersGroupMap.put(SearchConstantsv6.TEMPLATE_FILTER_PREFIX + templateFilterPrefix +  capitalizedName, true);
                    break;
            }
        }
    }

    private Map<String, Object> packageSearchResponse(SearchResponse searchResponse, ValidatedSearchData validatedSearchData, List<String> filtersUsed, Optional<Suggest.Suggestion.Entry.Option> bestOption) {

        List<Map<String, Object>> filtersMap = new ArrayList<>();
        List<Map<String, Object>> notVisibleFiltersMap = new ArrayList<>();
        List<Object> resultMetaList = new ArrayList<>();

        if (searchResponse.getHits().totalHits > 0) {

            if (null != searchResponse.getAggregations()) {
                Map<String, List<String>> visibleAggAndFilterNamesMap = getVisibleAggAndFilterNamesMap(validatedSearchData);

                for (Map.Entry<String, List<String>> pair : visibleAggAndFilterNamesMap.entrySet()) {
                    parseFilters(filtersMap, searchResponse, pair.getKey(), pair.getValue().get(0), pair.getValue().get(1));
                }

                Map<String, List<String>> notVisibleAggAndFilterNamesMap = getNotVisibleAggAndFilterNamesMap(validatedSearchData);

                for (Map.Entry<String, List<String>> pair : notVisibleAggAndFilterNamesMap.entrySet()) {
                    parseFilters(notVisibleFiltersMap, searchResponse, pair.getKey(), pair.getValue().get(0), pair.getValue().get(1));
                }

                if(validatedSearchData.getVisibleFilters().containsKey(SearchConstantsv6.FILTER_LAST_UPDATED_ON_FIELD_KEY)) {
                    if (filtersMap.size() > 0) {
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
                        topicAggMap.put("type", SearchConstantsv6.FIELD_DISPLAYNAME_PAIR.get(SearchConstantsv6.LAST_UPDATED_ON_AGGS_KEY).get(0));
                        topicAggMap.put("displayName", validatedSearchData.getVisibleFilters().get(SearchConstantsv6.FILTER_LAST_UPDATED_ON_FIELD_KEY).getDisplayName());
                        topicAggMap.put("content", filterItemList);
                        filtersMap.add(topicAggMap);
                    }
                }
            }

            for (SearchHit hit : searchResponse.getHits()) {
                resultMetaList.add(hit.getSourceAsMap());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalHits", searchResponse.getHits().totalHits);
        result.put("result", resultMetaList);
        result.put("filters", filtersMap);
        result.put("notVisibleFilters", notVisibleFiltersMap);
        result.put("filtersUsed", filtersUsed);

        bestOption.ifPresent(option -> result.put("doYouMean", option.getHighlighted().string()));

        if(null != validatedSearchData.getChangedQuery())
            result.put("queryUsed", validatedSearchData.getChangedQuery());

        return result;
    }

    private SearchResponse fetchFromES(ValidatedSearchData validatedSearchData, Map<String, Object> paramsMap) throws Exception {

        List<String> indices = new ArrayList<>();
        if (validatedSearchData.getLocale().isEmpty())
            validatedSearchData.setLocale(ValidatedSearchData.supportedLocales);
        for (String searchIndexLocale : validatedSearchData.getLocale()) {
            indices.add(SearchConstantsv6.SEARCH_INDEX_NAME_PREFIX + SearchConstantsv6.SEARCH_INDEX_LOCALE_DELIMITER + searchIndexLocale);
        }
        System.out.println("Search Template "+ SearchConstantsv6.ML_SEARCH_TEMPLATE);

        System.out.println("UUID "+ validatedSearchData.getUuid().toString());

        System.out.println("Params map "+ new ObjectMapper().writeValueAsString(paramsMap));


        SearchRequest searchRequest = new SearchRequest().searchType(SearchType.QUERY_THEN_FETCH);
        searchRequest.indices(indices.toArray(new String[0]));
        searchRequest.types(LexProjectUtil.EsType.new_lex_search.getTypeName());

        SearchTemplateRequest templateRequest = new SearchTemplateRequest();
        templateRequest.setScript(SearchConstantsv6.ML_SEARCH_TEMPLATE);
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
        switch (aggregationName) {
            case SearchConstantsv6.DURATION_AGGS_KEY: {
                ParsedFilter internalFilter = internalGlobalAggs.getAggregations().get(aggregationName);
                Range aggregation = internalFilter.getAggregations().get(aggregationName);
                for (Range.Bucket bucket : aggregation.getBuckets()) {
                    FilterItem filterItem = new FilterItem();
                    if (bucket.getKeyAsString() != null && !bucket.getKeyAsString().isEmpty()) {
                        if (bucket.getDocCount() > 0) {
                            switch (bucket.getKeyAsString()) {
                                case SearchConstantsv6.DURATION_SHORT_DISPLAY_NAME:
                                    filterItem.setType(SearchConstantsv6.DURATION_SHORT);
                                    break;
                                case SearchConstantsv6.DURATION_MEDIUM_DISPLAY_NAME:
                                    filterItem.setType(SearchConstantsv6.DURATION_MEDIUM);
                                    break;
                                case SearchConstantsv6.DURATION_LONG_DISPLAY_NAME:
                                    filterItem.setType(SearchConstantsv6.DURATION_LONG);
                                    break;
                                default:
                                    filterItem.setType(SearchConstantsv6.DURATION_DETAILED);
                                    break;
                            }
                            filterItem.setDisplayName(bucket.getKeyAsString());
                            filterItem.setCount(bucket.getDocCount());
                            filterItemList.add(filterItem);
                        }
                    }
                }
                break;
            }
            case SearchConstantsv6.CATALOG_PATHS_AGGS_KEY: {
                isTagsAgg = true;
                ParsedFilter internalFilter = internalGlobalAggs.getAggregations().get(aggregationName);
                Terms aggregation = internalFilter.getAggregations().get(aggregationName);
                TreeNode root = new TreeNode("ROOT", "ROOT", -1L);

                for (Terms.Bucket bucket : aggregation.getBuckets()) {
                    if (bucket.getKeyAsString() != null && !bucket.getKeyAsString().isEmpty()) {
                        if (bucket.getDocCount() > 0) {
                            TreeNode cur = root;
                            for (String key : bucket.getKeyAsString().split(SearchConstantsv6.TAGS_PATH_DELIMITER)) {
                                cur = cur.addChild(new TreeNode(key, bucket.getKeyAsString(), bucket.getDocCount()));
                            }
                        }
                    }
                }

                for (TreeNode child : root.children) {
                    allRootNodes.add(parseTree(child));
                }
                break;
            }
            case SearchConstantsv6.CONCEPTS_AGGS_KEY: {

                ParsedFilter internalFilter = internalGlobalAggs.getAggregations().get(aggregationName);
                ParsedNested internalNested = internalFilter.getAggregations().get(aggregationName);
                Map<String, Aggregation> subAggregation = internalNested.getAggregations().asMap();
                Terms termsAgg = (Terms) subAggregation.get(SearchConstantsv6.CONCEPTS_IDENTIFIER_AGGS_KEY);
                for (Terms.Bucket bucket : termsAgg.getBuckets()) {
                    Map<String, Aggregation> subSubAggregation = bucket.getAggregations().asMap();
                    Terms subTermsAgg = (Terms) subSubAggregation.get(SearchConstantsv6.CONCEPTS_NAME_AGGS_KEY);
                    Terms.Bucket subBucket = subTermsAgg.getBuckets().get(0);
                    FilterItem filterItem = new FilterItem();
                    if (bucket.getKeyAsString() != null && !bucket.getKeyAsString().isEmpty()) {
                        if (bucket.getDocCount() > 0) {
                            filterItem.setType(subBucket.getKeyAsString());
                            filterItem.setDisplayName(subBucket.getKeyAsString());
                            filterItem.setId(bucket.getKeyAsString());
                            filterItem.setCount(bucket.getDocCount());
                            filterItemList.add(filterItem);
                        }
                    }
                }
                break;
            }
            case SearchConstantsv6.IS_EXTERNAL_AGGS_KEY: {
                ParsedFilter internalFilter = internalGlobalAggs.getAggregations().get(aggregationName);
                ParsedLongTerms stringTerms = internalFilter.getAggregations().get(aggregationName);
                for (Terms.Bucket bucket : stringTerms.getBuckets()) {
                    FilterItem filterItem = new FilterItem();
                    if (bucket.getKeyAsString() != null && !bucket.getKeyAsString().isEmpty()) {
                        if (bucket.getDocCount() > 0) {
                            filterItem.setType(bucket.getKeyAsString());
                            if (bucket.getKeyAsString().equals("true"))
                                filterItem.setDisplayName("Yes");
                            else
                                filterItem.setDisplayName("No");
                            filterItem.setCount(bucket.getDocCount());
                            filterItemList.add(filterItem);
                        }
                    }
                }
                break;
            }
            case SearchConstantsv6.EXCLUSIVE_CONTENT_AGGS_KEY: {
                ParsedFilter internalFilter = internalGlobalAggs.getAggregations().get(aggregationName);
                ParsedLongTerms stringTerms = internalFilter.getAggregations().get(aggregationName);
                for (Terms.Bucket bucket : stringTerms.getBuckets()) {
                    FilterItem filterItem = new FilterItem();
                    if (bucket.getKeyAsString() != null && !bucket.getKeyAsString().isEmpty()) {
                        if (bucket.getDocCount() > 0) {
                            filterItem.setType(bucket.getKeyAsString());
                            if (bucket.getKeyAsString().equals("true"))
                                filterItem.setDisplayName("With costs");
                            else
                                filterItem.setDisplayName("Free");
                            filterItem.setCount(bucket.getDocCount());
                            filterItemList.add(filterItem);
                        }
                    }
                }
                break;
            }
            case SearchConstantsv6.CONTENT_TYPE_AGGS_KEY: {
                ParsedFilter internalFilter = internalGlobalAggs.getAggregations().get(aggregationName);
                ParsedStringTerms stringTerms = internalFilter.getAggregations().get(aggregationName);
                for (Terms.Bucket bucket : stringTerms.getBuckets()) {
                    FilterItem filterItem = new FilterItem();
                    if (bucket.getKeyAsString() != null && !bucket.getKeyAsString().isEmpty()) {
                        if (bucket.getDocCount() > 0) {
                            filterItem.setType(bucket.getKeyAsString());
                            switch (bucket.getKeyAsString()) {
                                case SearchConstantsv6.LEARNING_MODULE:
                                    filterItem.setDisplayName("Module");
                                    break;
                                case SearchConstantsv6.LEARNING_PATH:
                                    filterItem.setDisplayName("Program");
                                    break;
                                default:
                                    filterItem.setDisplayName(bucket.getKeyAsString());
                                    break;
                            }
                            filterItem.setCount(bucket.getDocCount());
                            filterItemList.add(filterItem);
                        }
                    }
                }
                break;
            }
            default: {
                ParsedFilter internalFilter = internalGlobalAggs.getAggregations().get(aggregationName);
                ParsedStringTerms stringTerms = internalFilter.getAggregations().get(aggregationName);
                for (Terms.Bucket bucket : stringTerms.getBuckets()) {
                    FilterItem filterItem = new FilterItem();
                    if (bucket.getKeyAsString() != null && !bucket.getKeyAsString().isEmpty()) {
                        if (bucket.getDocCount() > 0) {
                            filterItem.setType(bucket.getKeyAsString());
                            filterItem.setDisplayName(bucket.getKeyAsString());
                            filterItem.setCount(bucket.getDocCount());
                            filterItemList.add(filterItem);
                        }
                    }
                }
                break;
            }
        }

        if (!allRootNodes.isEmpty() || !filterItemList.isEmpty()) {
            Map<String, Object> topicAggMap = new HashMap<>();
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

    private Map<String, List<String>> getVisibleAggAndFilterNamesMap(ValidatedSearchData validatedSearchData) {
        Map<String, List<String>> data = new HashMap<>();
        validatedSearchData.getVisibleFilters().forEach((key, val) -> data.put(key + SearchConstantsv6.TEMPLATE_AGGS_NAME_SUFFIX, Arrays.asList(key, val.getDisplayName())));
        data.remove(SearchConstantsv6.FILTER_LAST_UPDATED_ON_FIELD_KEY + SearchConstantsv6.TEMPLATE_AGGS_NAME_SUFFIX);
        return data;
    }

    private Map<String, List<String>> getNotVisibleAggAndFilterNamesMap(ValidatedSearchData validatedSearchData) {
        Map<String, List<String>> data = new HashMap<>();
        validatedSearchData.getNotVisibleFilters().forEach((key, val) -> data.put(key + SearchConstantsv6.TEMPLATE_AGGS_NAME_SUFFIX, Arrays.asList(key, val.getDisplayName())));
        return data;
    }

    private Long[] getDurationRangeArray(String string) {
        Long[] rangeArray = new Long[2];
        switch (string) {
            case SearchConstantsv6.DURATION_SHORT:
                rangeArray[0] = Long.MIN_VALUE;
                rangeArray[1] = SearchConstantsv6.DURATION_SHORT_MAX;
                break;
            case SearchConstantsv6.DURATION_MEDIUM:
                rangeArray[0] = SearchConstantsv6.DURATION_MEDIUM_MIN;
                rangeArray[1] = SearchConstantsv6.DURATION_MEDIUM_MAX;
                break;
            case SearchConstantsv6.DURATION_LONG:
                rangeArray[0] = SearchConstantsv6.DURATION_LONG_MIN;
                rangeArray[1] = SearchConstantsv6.DURATION_LONG_MAX;
                break;
            case SearchConstantsv6.DURATION_DETAILED:
                rangeArray[0] = SearchConstantsv6.DURATION_DETAILED_MIN;
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

    final Map<String,Object> callSearchService(ValidatedSearchData validatedSearchData) throws Exception {
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
}

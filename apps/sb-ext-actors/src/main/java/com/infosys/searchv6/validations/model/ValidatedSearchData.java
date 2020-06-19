/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.searchv6.validations.model;

import com.infosys.search.MultiLingualIntegratedSearchController;
import com.infosys.searchv6.validations.groups.ValidationGroupAuthoringToolSearch;
import com.infosys.searchv6.validations.groups.ValidationGroupGeneralSearch;
import joptsimple.internal.Strings;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class ValidatedSearchData implements Serializable {

    public static List<String> supportedLocales = Arrays.asList("ar","br","cz","da","de","el","en","es","fr","fr_ca","hr","hu","id","it","ja","ko","nl","no","pl","pt","ru","sl","sv","th","tr","zh");

	@PositiveOrZero
    private int pageNo = 0;

    @PositiveOrZero
    private int pageSize = 10;

    @NotBlank
    private String rootOrg;

    @Size(min = 1, groups = {ValidationGroupGeneralSearch.class, ValidationGroupAuthoringToolSearch.class})
    private List<String> orgs = Collections.emptyList();

    @Valid
    private List<FiltersGroup> filters = Collections.emptyList();//Collections.singletonList(new FiltersGroup());

    private String searchType;
    private String query;
    private String changedQuery = null;
    private List<String> locale = Collections.emptyList();
    private UUID uuid;
    private Boolean isCatalog = false;
    private Boolean isStandAlone;
    private Boolean didYouMean = true;
    private List<String> accessPaths = Collections.emptyList();
    private List<Map<SortableFields, SortOrders>> sort = Collections.emptyList();
    private List<String> searchOn = Arrays.asList("learningObjective^1","preRequisites^1","subTitle^1","catalogPaths^1","childrenDescription^1","childrenTitle^1","concepts.name^1","description^1","keywords^1","sourceShortName^1","sourceName^1","name^2");

    @Size(max = 20, message = "Maximum only 20 aggregations can be requested")
    private Map<String, AggregationRequest> visibleFilters = Collections.emptyMap();

    @Size(max = 20, message = "Maximum only 20 aggregations can be requested")
    private Map<String, AggregationRequest> notVisibleFilters = Collections.emptyMap();

    @Size(max = 50, message = "Maximum only 50 fields can be requested")
    private List<String> excludeSourceFields = Collections.emptyList();

    @Size(max = 50, message = "Maximum only 50 fields can be requested")
    private List<String> includeSourceFields = Collections.emptyList();

    @AssertTrue(message = "uuid can not be empty")
    private boolean isUuid() {
        if (MultiLingualIntegratedSearchController.isAccessControlEnabled) {
            return Objects.nonNull(uuid);
        } else return true;
    }

    @AssertTrue(groups = {ValidationGroupGeneralSearch.class, ValidationGroupAuthoringToolSearch.class}, message = "No accessPaths found, User Unauthorized")
    private boolean isAccessPaths() {
        if (MultiLingualIntegratedSearchController.isAccessControlEnabled) {
            return accessPaths.size() > 0;
        } else return true;
    }

    @AssertTrue(message = "sort only applicable if query is empty or if it's an all search")
    private boolean isSort() {
        if (sort.size() > 0) {
            if (sort.stream().filter(item -> item.size() > 0).collect(Collectors.toList()).size() > 0)
                return Strings.isNullOrEmpty(query) || query.equals("*") || query.equalsIgnoreCase("all");
            else
                return true;
        } else return true;
    }

    @AssertTrue
    private boolean isLocale() {
        List<String> x = locale.stream().map(String::toLowerCase).map(i -> i.replace("-", "_")).collect(Collectors.toList());
        x.retainAll(supportedLocales);
        locale = x;
        return true;
    }

    public @PositiveOrZero int getPageNo() {
        return this.pageNo;
    }

    public @PositiveOrZero int getPageSize() {
        return this.pageSize;
    }

    public @NotBlank String getRootOrg() {
        return this.rootOrg;
    }

    public @Size(min = 1, groups = {ValidationGroupGeneralSearch.class, ValidationGroupAuthoringToolSearch.class}) List<String> getOrgs() { return this.orgs; }

    public @Valid List<FiltersGroup> getFilters() {
        return this.filters;
    }

    public String getQuery() {
        return this.query;
    }

    public List<String> getLocale() {
        return this.locale;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Boolean getIsCatalog() {
        return this.isCatalog;
    }

    public Boolean getIsStandAlone() {
        return this.isStandAlone;
    }

    public List<String> getAccessPaths() {
        return this.accessPaths;
    }

    public List<Map<SortableFields, SortOrders>> getSort() {
        return this.sort;
    }

    public void setPageNo(@PositiveOrZero int pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(@PositiveOrZero int pageSize) {
        this.pageSize = pageSize;
    }

    public void setRootOrg(@NotBlank String rootOrg) {
        this.rootOrg = rootOrg;
    }

    public void setOrgs(@Size(min = 1, groups = {ValidationGroupGeneralSearch.class, ValidationGroupAuthoringToolSearch.class}) List<String> orgs) { this.orgs = orgs; }

    public void setFilters(List<FiltersGroup> filters) {
        this.filters = filters;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setLocale(List<String> locale) {
        this.locale = locale;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setIsCatalog(Boolean isCatalog) {
        this.isCatalog = isCatalog;
    }

    public void setIsStandAlone(Boolean isStandAlone) {
        this.isStandAlone = isStandAlone;
    }

    public void setAccessPaths(List<String> accessPaths) {
        this.accessPaths = accessPaths;
    }

    public void setSort(List<Map<SortableFields, SortOrders>> sort) {
        this.sort = sort;
    }

    public Map<String, AggregationRequest> getVisibleFilters() { return visibleFilters; }

    public void setVisibleFilters(Map<String, AggregationRequest> visibleFilters) { this.visibleFilters = visibleFilters; }

    public Map<String, AggregationRequest> getNotVisibleFilters() { return notVisibleFilters; }

    public void setNotVisibleFilters(Map<String, AggregationRequest> notVisibleFilters) { this.notVisibleFilters = notVisibleFilters; }

    public List<String> getSearchOn() {
        return searchOn;
    }

    public void setSearchOn(List<String> searchOn) {
        this.searchOn = searchOn;
    }

    public String getChangedQuery() { return changedQuery; }

    public void setChangedQuery(String changedQuery) { this.changedQuery = changedQuery; }

    public List<String> getExcludeSourceFields() {
        return excludeSourceFields;
    }

    public void setExcludeSourceFields(List<String> excludeSourceFields) {
        this.excludeSourceFields = excludeSourceFields;
    }

    public List<String> getIncludeSourceFields() {
        return includeSourceFields;
    }

    public void setIncludeSourceFields(List<String> includeSourceFields) {
        this.includeSourceFields = includeSourceFields;
    }

    public Boolean getDidYouMean() {
        return didYouMean;
    }

    public void setDidYouMean(Boolean didYouMean) {
        this.didYouMean = didYouMean;
    }

    public String getSearchType() { return searchType; }

    public void setSearchType(String searchType) { this.searchType = searchType; }

}

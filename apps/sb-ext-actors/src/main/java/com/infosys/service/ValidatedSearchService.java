package com.infosys.service;

import com.infosys.searchv6.validations.model.ValidatedSearchData;

public interface ValidatedSearchService {
    /**
     * This method updates request query based on request search type
     *
     * @param validatedSearchData
     * @throws Exception
     */
    void buildSearchRequestQuery(ValidatedSearchData validatedSearchData) throws Exception;

}

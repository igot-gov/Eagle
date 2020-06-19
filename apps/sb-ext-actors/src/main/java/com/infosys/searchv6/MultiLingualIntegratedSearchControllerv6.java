/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.searchv6;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.exception.ApplicationLogicError;
import com.infosys.exception.BadRequestException;
import com.infosys.exception.NoContentException;
import com.infosys.searchv6.validations.model.ValidatedSearchData;
import com.infosys.service.ValidatedSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.sunbird.common.models.util.ProjectLogger;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MultiLingualIntegratedSearchControllerv6 {


    static boolean isAccessControlEnabled;
    
    @Autowired
//    @Qualifier("GeneralMultiLingualIntegratedSearchServicev6")
    private GeneralMultiLingualIntegratedSearchServicev6 generalSearchService;
    
    @Autowired
    private AdminMultiLingualIntegratedSearchServicev6 adminSearchService;

    @Autowired
    private AuthoringToolMultiLingualIntegratedSearchServicev6 authoringToolSearchService;

    @Autowired
    ValidatedSearchService validatedSearchService;

    @Autowired
    ObjectMapper mapper;
    
    @Value("${com.infosys.enable-access-control}")
    private boolean ace;

    @PostConstruct
    private void init() {
        isAccessControlEnabled = ace;
    }

    @PostMapping("/v6/search")
    public ResponseEntity<Map<String,Object>> generalSearchTemplateAPIv6(@Valid @RequestBody ValidatedSearchData request) throws Exception {

        Map<String, Object> resp = new HashMap<>();

        validatedSearchService.buildSearchRequestQuery(request);
        ProjectLogger.log("Search.v6.request.body", mapper.writeValueAsString(request));
        try {
            resp = generalSearchService.performSearch(request);
        } catch (BadRequestException e) {
            ProjectLogger.log("Search.v6", e);
            resp.put("error",e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        } catch (ApplicationLogicError e) {
            ProjectLogger.log("Search.v6", e);
            resp.put("error",e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoContentException e) {
            ProjectLogger.log("Search.v6", e);
            resp.put("error",e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @PostMapping("/v6/search/auth")
    public ResponseEntity<Map<String,Object>> authoringToolSearchTemplateAPI(@Valid @RequestBody ValidatedSearchData request) {

        Map<String,Object> resp = new HashMap<>();
        try {
			resp = authoringToolSearchService.performSearch(request);
        } catch (BadRequestException e) {
            ProjectLogger.log("Search.auth.v6", e);
            resp.put("error",e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        } catch (ApplicationLogicError e) {
            ProjectLogger.log("Search.auth.v6", e);
            resp.put("error",e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoContentException e) {
            ProjectLogger.log("Search.auth.v6", e);
            resp.put("error",e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            ProjectLogger.log("Search.auth.v6", e);
            resp.put("error",e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @PostMapping("/v6/search/admin")
    public ResponseEntity<Map<String,Object>> adminSearchTemplateAPI(@Valid @RequestBody ValidatedSearchData request) {

        Map<String,Object> resp = new HashMap<>();
        try {
			resp = adminSearchService.performSearch(request);

        } catch (BadRequestException e) {
            ProjectLogger.log("Search.admin.v6", e);
            resp.put("error",e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        } catch (ApplicationLogicError e) {
            ProjectLogger.log("Search.admin.v6", e);
            resp.put("error",e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoContentException e) {
            ProjectLogger.log("Search.admin.v6", e);
            resp.put("error",e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            ProjectLogger.log("Search.admin.v6", e);
            resp.put("error",e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}

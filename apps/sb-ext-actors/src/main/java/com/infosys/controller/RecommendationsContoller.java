/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.controller;

import com.infosys.exception.ApplicationLogicError;
import com.infosys.exception.BadRequestException;
import com.infosys.exception.NoContentException;
import com.infosys.search.validations.model.ValidatedSearchData;
import com.infosys.service.RecommendationsService;
import com.infosys.service.TopicService;
import com.infosys.util.ErrorGenerator;
import com.infosys.util.LexConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;

import javax.annotation.PostConstruct;
import javax.ws.rs.HeaderParam;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class RecommendationsContoller {
    private final RestTemplate restTemplate;
    private String neo4jIp;
    private String simpleColabIp;
    private String neighbourColabIp;
    private String trendingIp;
    private List<String> sourceShortName = Arrays.asList("","","","");

    @Autowired
    private Environment environment;

    @Autowired
    private RecommendationsService recommendationsService;

    public RecommendationsContoller() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        builder = builder.basicAuthorization("neo4j", "neo4j1");
        this.restTemplate = builder.build();
    }

    @PostConstruct
    private void initialize() {
        neo4jIp = environment.getProperty(LexConstants.NEO4J_IP);
        simpleColabIp += neo4jIp + "recommendation2/";
        neighbourColabIp += neo4jIp + "recommendation3/";
        trendingIp += neo4jIp + "trending/";
    }

    @GetMapping("/{userId}/recommendations/activity")
    public String activityBasedRecommendations(@PathVariable String userId) {
        try {
            return restTemplate.getForObject(simpleColabIp + userId, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/{userId}/recommendations/nactivity")
    public String activityBasedNeigbourhoodRecommendations(@PathVariable String userId) {
        try {
            return restTemplate.getForObject(neighbourColabIp + userId, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/{userId}/recommendations")
    public ResponseEntity<Response> getRecommendations(@PathVariable UUID userId, @RequestParam(value = "type", defaultValue = "org") String type, @RequestParam(value = "excludeContentType", defaultValue = "") String filters, @RequestHeader(value = "langCode",required = false) List<String> locales, @RequestParam(value="sourceFields", required=false) List<String> sourceFields, @RequestParam(value="isInIntranet",required = false) Boolean isInIntranet) throws Exception {
        Response resp = new Response();

        List<String> filtersArray = Arrays.asList(filters.split(","));
        locales = locales.stream().filter(item -> !item.isEmpty()).collect(Collectors.toList());
        locales.retainAll(ValidatedSearchData.supportedLocales);

        switch (type) {
            case "jl":
                resp = recommendationsService.getRecommendationsForJL(userId, filtersArray, locales, sourceFields, isInIntranet);
                break;
            case "unit":
                resp = recommendationsService.getRecommendationsForUnit(userId, filtersArray, locales, sourceFields, isInIntranet);
                break;
            case "account":
                resp = recommendationsService.getRecommendationsForAccount(userId, filtersArray, locales, sourceFields, isInIntranet);
                break;
            default:
                resp = recommendationsService.getRecommendationsForOrg(userId, filtersArray, locales, sourceFields, isInIntranet);
                break;
        }
        resp.setVer("v1");
        resp.setId("api.latest");
        resp.setTs(ProjectUtil.getFormattedDate());
        return new ResponseEntity<Response>(resp, HttpStatus.OK);
    }

    @GetMapping("/{userId}/recommendations/trending")
    public ResponseEntity<Response> getTrending(@PathVariable UUID userId, @RequestParam(value = "type", defaultValue = "org") String type, @RequestHeader(value = "rootOrg", defaultValue = "") String rootOrg, @RequestParam(value = "org", defaultValue = "") String org, @RequestParam(value = "pageSize", defaultValue = "10") String pageSizeStr, @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumberStr, @RequestParam(value = "includeContentType", defaultValue = "") String filters, @RequestHeader(value = "langCode",required = false,defaultValue = "en")String langCodes,@RequestParam(value="sourceFields", required=false) List<String> sourceFields, @RequestParam(value="isInIntranet",required = false) Boolean isInIntranet) {
        Response resp = new Response();

        if (rootOrg.isEmpty()){
            resp.setVer("v1");
            resp.setId("api.trending");
            resp.setTs(ProjectUtil.getFormattedDate());
            resp.put("error", "rootOrg is Mandatory");
            return new ResponseEntity<Response>(resp,HttpStatus.BAD_REQUEST);
        }
        if (org.isEmpty())
            org = environment.getProperty(LexConstants.ORG);

        int pageSize;
        int pageNumber;
        try {
        	pageSize = Integer.valueOf(pageSizeStr);
        	pageNumber = Integer.valueOf(pageNumberStr);
        }catch(Exception e) {
        	resp.setVer("v1");
            resp.setId("api.trending");
            resp.setTs(ProjectUtil.getFormattedDate());
        	resp.put("error", "pageSize and pageNumber must be an integer");
        	return new ResponseEntity<Response>(resp,HttpStatus.BAD_REQUEST);
        }

        List<String> locales = Arrays.asList(langCodes.split(","));

        locales = locales.stream().filter(item -> !item.isEmpty()).collect(Collectors.toList());
        locales.retainAll(ValidatedSearchData.supportedLocales);

        List<String> filtersArray = new ArrayList<>();
        if (!filters.isEmpty())
            filtersArray = Arrays.asList(filters.split(","));
        try {
            switch (type) {
                case "unit":
                    resp = recommendationsService.getTrendingForUnit(userId, rootOrg, org, pageNumber, pageSize, filtersArray, locales, sourceFields, isInIntranet);
                    break;
                case "account":
                    resp = recommendationsService.getTrendingForAccount(userId, rootOrg, org, pageNumber, pageSize, filtersArray, locales, sourceFields, isInIntranet);
                    break;
                case "jl":
                    resp = recommendationsService.getTrendingForJL(userId, rootOrg, org, pageNumber, pageSize, filtersArray, locales, sourceFields, isInIntranet);
                    break;
                default:
                    List<Map<String, Object>> responseData = recommendationsService.getTrendingOrg(userId, rootOrg, org, pageNumber, pageSize, filtersArray, locales, sourceFields, isInIntranet);
                    resp.put("greyOut",false);
                    resp.put("response", responseData);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            resp.put("error",e.getMessage());
            resp.put("response", new ArrayList<>());
        }
        resp.setVer("v1");
        resp.setId("api.trending");
        resp.setTs(ProjectUtil.getFormattedDate());
        return new ResponseEntity<Response>(resp, HttpStatus.OK);
    }

    @GetMapping("/{userId}/recommendations/latest")
    public ResponseEntity<Response> getLatest(@PathVariable UUID userId, @RequestParam(value = "type", defaultValue = "org") String type, @RequestParam(value="learningMode", required=false) String learningMode, @RequestHeader(value = "rootOrg", defaultValue = "") String rootOrg, @RequestParam(value = "org", defaultValue = "") String org, @RequestParam(value = "pageSize", defaultValue = "10") String pageSizeStr, @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumberStr, @RequestParam(value = "excludeContentType", defaultValue = "") String contenttypeFilters, @RequestParam(value="externalContent", required=false) Boolean externalContent, @RequestHeader(value = "langCode",required = false) List<String> locales, @RequestParam(value="isInIntranet",required = false) Boolean isInIntranet, @RequestParam(value="isStandAlone",required = false) Boolean isStandAlone) throws Exception {
        Response resp = new Response();

        if (rootOrg.isEmpty()){
            resp.setVer("v1");
            resp.setId("api.latest");
            resp.setTs(ProjectUtil.getFormattedDate());
            resp.put("error", "rootOrg is Mandatory");
            return new ResponseEntity<Response>(resp,HttpStatus.BAD_REQUEST);
        }

        if (org.isEmpty())
            org = environment.getProperty(LexConstants.ORG);

        int pageSize;
        int pageNumber;
        try {
        	pageSize = Integer.valueOf(pageSizeStr);
        	pageNumber = Integer.valueOf(pageNumberStr);
        }catch(Exception e) {
        	resp.setVer("v1");
            resp.setId("api.latest");
            resp.setTs(ProjectUtil.getFormattedDate());
        	resp.put("error", "pageSize and pageNumber must be an integer");
        	return new ResponseEntity<Response>(resp,HttpStatus.BAD_REQUEST);
        }
        
        List<String> contentTypefiltersArray = Arrays.asList(contenttypeFilters.split(","));

        if(null == externalContent)
        	externalContent = false;

        locales = locales.stream().filter(item -> !item.isEmpty()).collect(Collectors.toList());
        locales.retainAll(ValidatedSearchData.supportedLocales);

        if (null == isStandAlone)
            isStandAlone = true;

        switch (type) {
            case "role":
                resp = recommendationsService.getLatestForRoles(userId, learningMode, rootOrg, org, pageNumber, pageSize, contentTypefiltersArray,sourceShortName,externalContent, locales, isInIntranet, isStandAlone);
                break;
            default:
                resp = recommendationsService.getLatestOrg(userId, learningMode, rootOrg, org, pageNumber, pageSize, contentTypefiltersArray,sourceShortName,externalContent,locales, isInIntranet, isStandAlone);
                break;
        }
        resp.setVer("v1");
        resp.setId("api.latest");
        resp.setTs(ProjectUtil.getFormattedDate());
        return new ResponseEntity<Response>(resp, HttpStatus.OK);
    }

    @GetMapping("/{userId}/recommendations/interest")
    public ResponseEntity<Response> getInterest(@PathVariable UUID userId, @RequestParam(value="learningMode", required=false,defaultValue = "") String learningModes, @RequestParam(value = "pageSize", defaultValue = "10") String pageSizeStr, @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumberStr, @RequestParam(value = "includeContentType", defaultValue = "",required = false) String includeContentTypes, @RequestParam(value="externalContent", required=false) Boolean externalContentFilter, @RequestHeader(value = "rootOrg",defaultValue = "") String rootOrg, @RequestHeader(value = "langCode",required = false) List<String> locales,@RequestParam(value="sourceFields", required=false) List<String> sourceFields, @RequestParam(value="isInIntranet",required = false) Boolean isInIntranet, @RequestParam(value="isStandAlone",required = false) Boolean isStandAlone) {
        Response resp = new Response();

        int pageSize;
        int pageNumber;
        try {
            pageSize = Integer.valueOf(pageSizeStr);
            pageNumber = Integer.valueOf(pageNumberStr);
        }catch(Exception e) {
            resp.setVer("v1");
            resp.setId("api.interest");
            resp.setTs(ProjectUtil.getFormattedDate());
            resp.put("error", "pageSize and pageNumber must be an integer");
            return new ResponseEntity<Response>(resp,HttpStatus.BAD_REQUEST);
        }

        List<String> includeContentTypesFilter = new ArrayList<>();
        if (!includeContentTypes.isEmpty())
            includeContentTypesFilter = Arrays.asList(includeContentTypes.split(","));

        List<String> learningModesFilter = new ArrayList<>();
        if (!learningModes.isEmpty())
            learningModesFilter = Arrays.asList(learningModes.split(","));

        locales = locales.stream().filter(item -> !item.isEmpty()).collect(Collectors.toList());
        locales.retainAll(ValidatedSearchData.supportedLocales);


        try {
            resp = recommendationsService.getInterests(userId, learningModesFilter, pageNumber, pageSize, includeContentTypesFilter, externalContentFilter, locales, rootOrg, sourceFields, isInIntranet, isStandAlone);
        } catch (BadRequestException e) {
            ProjectLogger.log("Recommendations.getInterest", e);
            return new ResponseEntity<>(ErrorGenerator.generateErrorResponse(ResponseCode.invalidRequestData, "Recommendations.getInterest", "1", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (ApplicationLogicError e) {
            ProjectLogger.log("Recommendations.getInterest", e);
            return new ResponseEntity<>(ErrorGenerator.generateErrorResponse(ResponseCode.internalError, "Recommendations.getInterest", "1", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoContentException e) {
            ProjectLogger.log("Recommendations.getInterest", e);
            return new ResponseEntity<>(ErrorGenerator.generateErrorResponse(ResponseCode.noDataForConsumption, "Recommendations.getInterest", "1", e.getMessage()), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            ProjectLogger.log("Recommendations.getInterest", e);
            return new ResponseEntity<>(ErrorGenerator.generateErrorResponse(ResponseCode.internalError, "Recommendations.getInterest", "1", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        resp.setVer("v1");
        resp.setId("api.interest");
        resp.setTs(ProjectUtil.getFormattedDate());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}

/*
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lexauthoringservices.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class ContentSearchRequestNodeFilters implements Serializable {

    private List<Boolean> isExternal = Collections.emptyList();
    private List<String> contentType = Collections.emptyList();
    private List<String> unit = Collections.emptyList();
    private List<String> learningMode = Collections.emptyList();
    private List<String> resourceType = Collections.emptyList();
    private List<String> sourceShortName = Collections.emptyList();
    private List<String> fileType = Collections.emptyList();
    private List<Long> duration = Collections.emptyList();
    private List<String> complexityLevel = Collections.emptyList();
    private List<String> lastUpdatedOn = Collections.emptyList();
    private List<Boolean> isRejected = Collections.emptyList();
    private List<Boolean> exclusiveContent = Collections.emptyList();
    private List<Boolean> instanceCatalog = Collections.emptyList();
    private List<String> labels = Collections.emptyList();
    private List<String> curatedTags = Collections.emptyList();
    private List<String> identifier = Collections.emptyList();
    private List<Boolean> isInIntranet = Collections.emptyList();
    private List<String> catalogPaths = Collections.emptyList();
    private List<String> region = Collections.emptyList();

    public ContentSearchRequestNodeFilters() {
    }

    public List<Boolean> getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(List<Boolean> isExternal) {
        this.isExternal = isExternal;
    }

    public List<String> getContentType() {
        return contentType;
    }

    public void setContentType(List<String> contentType) {
        this.contentType = contentType;
    }

    public List<String> getUnit() {
        return unit;
    }

    public void setUnit(List<String> unit) {
        this.unit = unit;
    }

    public List<String> getLearningMode() {
        return learningMode;
    }

    public void setLearningMode(List<String> learningMode) {
        this.learningMode = learningMode;
    }

    public List<String> getResourceType() {
        return resourceType;
    }

    public void setResourceType(List<String> resourceType) {
        this.resourceType = resourceType;
    }

    public List<String> getSourceShortName() {
        return sourceShortName;
    }

    public void setSourceShortName(List<String> sourceShortName) {
        this.sourceShortName = sourceShortName;
    }

    public List<String> getFileType() {
        return fileType;
    }

    public void setFileType(List<String> fileType) {
        this.fileType = fileType;
    }

    public List<Long> getDuration() {
        return duration;
    }

    public void setDuration(List<Long> duration) {
        this.duration = duration;
    }

    public List<String> getComplexityLevel() {
        return complexityLevel;
    }

    public void setComplexityLevel(List<String> complexityLevel) {
        this.complexityLevel = complexityLevel;
    }

    public List<String> getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(List<String> lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public List<Boolean> getIsRejected() {
        return isRejected;
    }

    public void setIsRejected(List<Boolean> isRejected) {
        this.isRejected = isRejected;
    }

    public List<Boolean> getExclusiveContent() {
        return exclusiveContent;
    }

    public void setExclusiveContent(List<Boolean> exclusiveContent) {
        this.exclusiveContent = exclusiveContent;
    }

    public List<Boolean> getInstanceCatalog() {
        return instanceCatalog;
    }

    public void setInstanceCatalog(List<Boolean> instanceCatalog) {
        this.instanceCatalog = instanceCatalog;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getCuratedTags() {
        return curatedTags;
    }

    public void setCuratedTags(List<String> curatedTags) {
        this.curatedTags = curatedTags;
    }

    public List<Boolean> getIsInIntranet() {
        return isInIntranet;
    }

    public void setIsInIntranet(List<Boolean> isInIntranet) {
        this.isInIntranet = isInIntranet;
    }

    public List<String> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<String> identifier) {
        this.identifier = identifier;
    }

    public List<String> getCatalogPaths() {
        return catalogPaths;
    }

    public void setCatalogPaths(List<String> catalogPaths) {
        this.catalogPaths = catalogPaths;
    }

    public List<String> getRegion() {
        return region;
    }

    public void setRegion(List<String> region) {
        this.region = region;
    }
}

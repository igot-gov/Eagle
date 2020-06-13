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
import java.util.ArrayList;
import java.util.List;

public class ContentSearchRequestRelation implements Serializable {

    private List<String> sourceFields = new ArrayList<>();
    private Integer levels = 1;
    private ContentSearchRequestRelationFilters filters = new ContentSearchRequestRelationFilters();

    public ContentSearchRequestRelation() {
    }

    public List<String> getSourceFields() {
        return sourceFields;
    }

    public void setSourceFields(List<String> sourceFields) {
        this.sourceFields = sourceFields;
    }

    public Integer getLevels() {
        return levels;
    }

    public void setLevels(Integer levels) {
        this.levels = levels;
    }

    public ContentSearchRequestRelationFilters getFilters() {
        return filters;
    }

    public void setFilters(ContentSearchRequestRelationFilters filters) {
        this.filters = filters;
    }
}

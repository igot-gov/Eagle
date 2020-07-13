/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.model.cassandra;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Table("content_evaluation")
public class ContentEvaluation {

    @Column("role")
    private String role;
    @Column("date")
    private String date;
    @Column("assesment_items")
    private List<AssesmentItem> assesmentItems = new ArrayList<>();
    @PrimaryKey
    private ContentEvaluationPrimaryKey contentEvaluationPrimaryKey;

    public ContentEvaluation(List<AssesmentItem> assesmentItems, ContentEvaluationPrimaryKey contentEvaluationPrimaryKey){
        this.assesmentItems = assesmentItems;
        this.contentEvaluationPrimaryKey = contentEvaluationPrimaryKey;
    }

    public List<AssesmentItem> getAssesmentItems() {
        return assesmentItems;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

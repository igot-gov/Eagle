/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.model.cassandra;

import org.hibernate.annotations.Formula;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.*;

@Table("content_evaluation")
public class ContentEvaluation {

    @Column("header")
    private String header;
    @Column("role")
    private String role;
    @Column("date")
    private String date;
    @PrimaryKey
    private ContentEvaluationPrimaryKey contentEvaluationPrimaryKey;
    @Column("description")
    private String description;
    @Column("items")
    private Map<String, String> items = new HashMap<>();

    @Column("length")
    private int length;

    @Column("width")
    private int width;

    @Formula(" length * width ")
    public long area;

    public ContentEvaluation(ContentEvaluationPrimaryKey contentEvaluationPrimaryKey){
        this.contentEvaluationPrimaryKey = contentEvaluationPrimaryKey;
    }

    public String getHeader() { return header; }

    public void setHeader(String header) { this.header = header; }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }



}

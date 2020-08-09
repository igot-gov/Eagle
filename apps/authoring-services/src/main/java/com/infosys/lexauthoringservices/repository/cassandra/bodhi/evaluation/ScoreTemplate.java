/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.repository.cassandra.bodhi.evaluation;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;


@Table("score_template")
public class ScoreTemplate {

    @PrimaryKeyColumn(name = "root_org",type= PrimaryKeyType.PARTITIONED)
    private String rootOrg;

    @PrimaryKeyColumn(name = "org")
    private String org;

    @PrimaryKeyColumn(name = "template_id")
    private String templateId;

    @Column("version")
    private int version;

    @Column("max_score")
    private double maxScore;

    @Column("weightage")
    private double totalWeightage;

    @Column("min_acceptable_score")
    private double finalMinScore;


    public ScoreTemplate(String rootOrg, String org, String templateId, int version){
        this.rootOrg = rootOrg;
        this.org = org;
        this. templateId = templateId;
        this.version = version;
    }

    public String getRootOrg() {
        return rootOrg;
    }

    public String getOrg() {
        return org;
    }

    public String getTemplateId() {
        return templateId;
    }

    public int getVersion() {
        return version;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public double getTotalWeightage() {
        return totalWeightage;
    }

    public void setTotalWeightage(double totalWeightage) {
        this.totalWeightage = totalWeightage;
    }

    public double getFinalMinScore() {
        return finalMinScore;
    }

    public void setFinalMinScore(double finalMinScore) {
        this.finalMinScore = finalMinScore;
    }
}

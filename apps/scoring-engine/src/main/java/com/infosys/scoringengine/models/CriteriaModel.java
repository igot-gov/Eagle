/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.scoringengine.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class CriteriaModel {

    @NotBlank
    private String criteria;

    private double totalScore;

    private double weightage;

    private double weightedAvg;

    private double maxScore;

    private double minScore;

    private double maxWeightedAvg;

    private double minWeightedAvg;

    @Size(min =1)
    private List<QualifierModel> qualifiers = new ArrayList<>();

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getWeightedAvg() {
        return weightedAvg;
    }

    public void setWeightedAvg(double weightedAvg) {
        this.weightedAvg = weightedAvg;
    }

    public List<QualifierModel> getQualifiers() {
        return qualifiers;
    }

    public void setQualifiers(List<QualifierModel> qualifiers) {
        this.qualifiers = qualifiers;
    }

    public double getWeightage() {
        return weightage;
    }

    public void setWeightage(double weightage) {
        this.weightage = weightage;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public double getMinScore() {
        return minScore;
    }

    public void setMinScore(double minScore) {
        this.minScore = minScore;
    }

    public double getMaxWeightedAvg() {
        return maxWeightedAvg;
    }

    public void setMaxWeightedAvg(double maxWeightedAvg) {
        this.maxWeightedAvg = maxWeightedAvg;
    }

    public double getMinWeightedAvg() {
        return minWeightedAvg;
    }

    public void setMinWeightedAvg(double minWeightedAvg) {
        this.minWeightedAvg = minWeightedAvg;
    }
}

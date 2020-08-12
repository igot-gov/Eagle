/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.scoringengine.schema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

public class Qualifier {

    private String qualifier;
    private Double weightage;
    private Double max_score;
    private Double min_acceptable_score;
    private Map<String, Integer> fixed_score = new HashMap<>();
    //@JsonIgnoreProperties
    private Map<String, Range> range_score = new HashMap<>();

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public Double getWeightage() {
        return weightage;
    }

    public void setWeightage(Double weightage) {
        this.weightage = weightage;
    }

    public Double getMax_score() {
        return max_score;
    }

    public void setMax_score(Double max_score) {
        this.max_score = max_score;
    }

    public Double getMin_acceptable_score() {
        return min_acceptable_score;
    }

    public void setMin_acceptable_score(Double min_acceptable_score) {
        this.min_acceptable_score = min_acceptable_score;
    }

    public Map<String, Integer> getFixed_score() {
        return fixed_score;
    }

    public void setFixed_score(Map<String, Integer> fixed_score) {
        this.fixed_score = fixed_score;
    }

    public Map<String, Range> getRange_score() {
        return range_score;
    }

    public void setRange_score(Map<String, Range> range_score) {
        this.range_score = range_score;
    }
}

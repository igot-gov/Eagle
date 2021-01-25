

package org.sunbird.scoringengine.schema.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Qualifier {

    private String qualifier;
    private String description;
    private Double weightage;
    private Double max_score;
    private Double min_acceptable_score;
    private Map<String, Integer> fixed_score = new HashMap<>();
    //@JsonIgnoreProperties
    private Map<String, Range> range_score = new HashMap<>();

    private Boolean modify_max_score;

    private Map<String, Integer> max_score_modify_value = new HashMap<>();

    private List<HashMap<String, Object>> options = new ArrayList<>();

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

    public Boolean getModify_max_score() {
        return modify_max_score;
    }

    public void setModify_max_score(Boolean modify_max_score) {
        this.modify_max_score = modify_max_score;
    }

    public Map<String, Integer> getMax_score_modify_value() {
        return max_score_modify_value;
    }

    public void setMax_score_modify_value(Map<String, Integer> max_score_modify_value) {
        this.max_score_modify_value = max_score_modify_value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<HashMap<String, Object>> getOptions() {
        return options;
    }

    public void setOptions(List<HashMap<String, Object>> options) {
        this.options = options;
    }
}

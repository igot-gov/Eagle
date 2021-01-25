

package org.sunbird.scoringengine.schema.model;


import java.util.ArrayList;
import java.util.List;

public class Criteria {

    private String criteria;
    private Double weightage;
    private Double max_score;
    private Double min_acceptable_score;
    private String description;
    private List<Qualifier> qualifiers = new ArrayList<>();

    private Boolean min_score_weightage_enable;

    private Double min_score_weightage;

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
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

    public List<Qualifier> getQualifiers() {
        return qualifiers;
    }

    public void setQualifiers(List<Qualifier> qualifiers) {
        this.qualifiers = qualifiers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getMin_score_weightage_enable() {
        return min_score_weightage_enable;
    }

    public void setMin_score_weightage_enable(Boolean min_score_weightage_enable) {
        this.min_score_weightage_enable = min_score_weightage_enable;
    }

    public Double getMin_score_weightage() {
        return min_score_weightage;
    }

    public void setMin_score_weightage(Double min_score_weightage) {
        this.min_score_weightage = min_score_weightage;
    }
}

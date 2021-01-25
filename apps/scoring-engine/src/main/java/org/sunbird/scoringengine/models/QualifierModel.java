

package org.sunbird.scoringengine.models;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QualifierModel {

    @NotBlank
    private String name;
    @NotBlank
    private String evaluated;
    private String description;
    private double scoreValue;
    private String scoreRange;
    private String scoringType;
    private List<HashMap<String, Object>> options = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEvaluated() {
        return evaluated;
    }

    public void setEvaluated(String evaluated) {
        this.evaluated = evaluated;
    }

    public double getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(double scoreValue) {

        this.scoreValue = scoreValue;
    }

    public String getScoreRange() {
        return scoreRange;
    }

    public void setScoreRange(String scoreRange) {
        this.scoreRange = scoreRange;
    }

    public String getScoringType() {
        return scoringType;
    }

    public void setScoringType(String scoringType) {
        this.scoringType = scoringType;
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






package org.sunbird.scoringengine.models;

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

    private double weightedScore;

    private String description;

    @Size(min =1)
    private List<QualifierModel> qualifiers = new ArrayList<>();

    private  boolean isQualifiedMinCriteria = true;

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

    public double getWeightedScore() {
        return weightedScore;
    }

    public void setWeightedScore(double weightedScore) {
        this.weightedScore = weightedScore;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isQualifiedMinCriteria() {
        return isQualifiedMinCriteria;
    }

    public void setQualifiedMinCriteria(boolean qualifiedMinCriteria) {
        isQualifiedMinCriteria = qualifiedMinCriteria;
    }
}

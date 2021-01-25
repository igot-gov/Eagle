

package org.sunbird.scoringengine.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class EvaluatorModel {

    private String identifier;
    private String rootOrg;
    private String org;
    @NotBlank
    private String resourceId;
    @NotBlank
    private String resourceType;
    @NotBlank
    private String userId;

    private String templateId;
    private String templeteName;

    private double compositeScore;
    private double minimunQualifier;

    private double finalMaxScore;
    private double finalMinScore;

    private double finalTotalScore;
    private double finalWeightedAvg;

    private double finalMaxWeightedAvg;
    private double finalMinWeightedAvg;

    private double finalWeightedScore;

    private boolean getLatestRecordEnabled;


    private String scoreGrade;

    private boolean isQualifiedMinCriteria = true;

    @Size(min = 1)
    private List<CriteriaModel> criteriaModels = new ArrayList<>();

    private String timeStamp;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getRootOrg() {
        return rootOrg;
    }

    public void setRootOrg(String rootOrg) {
        this.rootOrg = rootOrg;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public double getCompositeScore() {
        return compositeScore;
    }

    public void setCompositeScore(double compositeScore) {
        this.compositeScore = compositeScore;
    }

    public double getMinimunQualifier() {
        return minimunQualifier;
    }

    public void setMinimunQualifier(double minmunQualifier) {
        this.minimunQualifier = minmunQualifier;
    }

    public List<CriteriaModel> getCriteriaModels() {
        return criteriaModels;
    }

    public void setCriteriaModels(List<CriteriaModel> criteriaModels) {
        this.criteriaModels = criteriaModels;
    }

    public double getFinalMaxScore() {
        return finalMaxScore;
    }

    public void setFinalMaxScore(double finalMaxScore) {
        this.finalMaxScore = finalMaxScore;
    }

    public double getFinalMinScore() {
        return finalMinScore;
    }

    public void setFinalMinScore(double finalMinScore) {
        this.finalMinScore = finalMinScore;
    }

    public double getFinalTotalScore() {
        return finalTotalScore;
    }

    public void setFinalTotalScore(double finalTotalScore) {
        this.finalTotalScore = finalTotalScore;
    }

    public double getFinalWeightedAvg() {
        return finalWeightedAvg;
    }

    public void setFinalWeightedAvg(double finalWeightedAvg) {
        this.finalWeightedAvg = finalWeightedAvg;
    }

    public double getFinalMaxWeightedAvg() {
        return finalMaxWeightedAvg;
    }

    public void setFinalMaxWeightedAvg(double finalMaxWeightedAvg) {
        this.finalMaxWeightedAvg = finalMaxWeightedAvg;
    }

    public double getFinalMinWeightedAvg() {
        return finalMinWeightedAvg;
    }

    public void setFinalMinWeightedAvg(double finalMinWeightedAvg) {
        this.finalMinWeightedAvg = finalMinWeightedAvg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTempleteName() {
        return templeteName;
    }

    public void setTempleteName(String templeteName) {
        this.templeteName = templeteName;
    }

    public double getFinalWeightedScore() {
        return finalWeightedScore;
    }

    public void setFinalWeightedScore(double finalWeightedScore) {
        this.finalWeightedScore = finalWeightedScore;
    }

    public boolean isGetLatestRecordEnabled() {
        return getLatestRecordEnabled;
    }

    public void setGetLatestRecordEnabled(boolean getLatestRecordEnabled) {
        this.getLatestRecordEnabled = getLatestRecordEnabled;
    }

    public String getScoreGrade() {
        return scoreGrade;
    }

    public void setScoreGrade(String scoreGrade) {
        this.scoreGrade = scoreGrade;
    }

    public boolean isQualifiedMinCriteria() {
        return isQualifiedMinCriteria;
    }

    public void setQualifiedMinCriteria(boolean qualifiedMinCriteria) {
        isQualifiedMinCriteria = qualifiedMinCriteria;
    }
}

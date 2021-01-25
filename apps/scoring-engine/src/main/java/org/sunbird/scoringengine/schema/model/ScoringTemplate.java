

package org.sunbird.scoringengine.schema.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScoringTemplate {

    private String rootOrg;
    private String org;
    private String template_id;
    private String templateName;
    private double version;
    private double weightage;
    private double max_score;
    private double min_acceptable_score;
    private List<Criteria> criteria = new ArrayList<>();
    private List<HashMap<String, String>> score_grades = new ArrayList<>();
    private HashMap<String, String> status_on_min_criteria = new HashMap<>();

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

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public double getWeightage() {
        return weightage;
    }

    public void setWeightage(double weightage) {
        this.weightage = weightage;
    }

    public double getMax_score() {
        return max_score;
    }

    public void setMax_score(double max_score) {
        this.max_score = max_score;
    }

    public double getMin_acceptable_score() {
        return min_acceptable_score;
    }

    public void setMin_acceptable_score(double min_acceptable_score) {
        this.min_acceptable_score = min_acceptable_score;
    }

    public List<Criteria> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<Criteria> criteria) {
        this.criteria = criteria;
    }

    public List<HashMap<String, String>> getScore_grades() {
        return score_grades;
    }

    public void setScore_grades(List<HashMap<String, String>> score_grades) {
        this.score_grades = score_grades;
    }

    public HashMap<String, String> getStatus_on_min_criteria() {
        return status_on_min_criteria;
    }

    public void setStatus_on_min_criteria(HashMap<String, String> status_on_min_criteria) {
        this.status_on_min_criteria = status_on_min_criteria;
    }
}

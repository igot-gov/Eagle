

package org.sunbird.scoringengine.repository.cassandra.bodhi;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("evaluation_criteria")
public class EvaluationCriteria {


    @PrimaryKeyColumn(name = "root_org",type= PrimaryKeyType.PARTITIONED)
    private String rootOrg;

    @PrimaryKeyColumn(name = "org")
    private String org;

    @PrimaryKeyColumn(name = "ref_template_id")
    private String templateId;

    @PrimaryKeyColumn(name = "criteria_id")
    private String criteriaId;

    @Column("criteria")
    private String criteria;

    @Column("max_score")
    private double maxScore;

    @Column("weightage")
    private double weightage;

    @Column("min_acceptable_score")
    private double minScore;

    public EvaluationCriteria(String rootOrg, String org, String templateId, String criteriaId){
        this.rootOrg = rootOrg;
        this.org = org;
        this. templateId = templateId;
        this.criteriaId = criteriaId;
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

    public String getCriteriaId() {
        return criteriaId;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public double getWeightage() {
        return weightage;
    }

    public void setWeightage(double weightage) {
        this.weightage = weightage;
    }

    public double getMinScore() {
        return minScore;
    }

    public void setMinScore(double minScore) {
        this.minScore = minScore;
    }
}

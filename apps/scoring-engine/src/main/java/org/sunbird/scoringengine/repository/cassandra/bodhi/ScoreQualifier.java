

package org.sunbird.scoringengine.repository.cassandra.bodhi;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.HashMap;
import java.util.Map;

@Table("score_qualifier")
public class ScoreQualifier {

    @PrimaryKeyColumn(name = "root_org",type= PrimaryKeyType.PARTITIONED)
    private String rootOrg;

    @PrimaryKeyColumn(name = "org")
    private String org;

    @PrimaryKeyColumn(name = "ref_criteria_id")
    private String criteriaId;

    @PrimaryKeyColumn(name = "qualifier")
    private String qualifier;

    @Column("fixed_score")
    private Map<String, Integer> fixedScore = new HashMap<>();

    @Column("range_score")
    private Map<String, String> rangeScore = new HashMap<>();

    public ScoreQualifier(String rootOrg, String org, String criteriaId, String qualifier){
        this.rootOrg = rootOrg;
        this.org = org;
        this. qualifier = qualifier;
        this.criteriaId = criteriaId;
    }

    public String getRootOrg() {
        return rootOrg;
    }

    public String getOrg() {
        return org;
    }

    public String getCriteriaId() {
        return criteriaId;
    }

    public String getQualifier() {
        return qualifier;
    }

    public Map<String, Integer> getFixedScore() {
        return fixedScore;
    }

    public void setFixedScore(Map<String, Integer> fixedScore) {
        this.fixedScore = fixedScore;
    }

    public Map<String, String> getRangeScore() {
        return rangeScore;
    }

    public void setRangeScore(Map<String, String> rangeScore) {
        this.rangeScore = rangeScore;
    }
}

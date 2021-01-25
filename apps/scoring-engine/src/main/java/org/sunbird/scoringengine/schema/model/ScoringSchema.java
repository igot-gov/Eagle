
package org.sunbird.scoringengine.schema.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScoringSchema {

    private String id;
    private List<ScoringTemplate> scoringTemplates = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ScoringTemplate> getScoringTemplates() {
        return scoringTemplates;
    }

    public void setScoringTemplates(List<ScoringTemplate> scoringTemplates) {
        this.scoringTemplates = scoringTemplates;
    }
}

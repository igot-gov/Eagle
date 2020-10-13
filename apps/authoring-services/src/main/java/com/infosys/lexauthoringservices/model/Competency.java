package com.infosys.lexauthoringservices.model;

import java.util.HashMap;

public class Competency {

    private String type;

    private String id;

    private String name;

    private String description;

    private String status;

    private String source;

    private HashMap<String, Object> additionalProperties;

    private String rootOrg;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public HashMap<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(HashMap<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public String getRootOrg() {
        return rootOrg;
    }

    public void setRootOrg(String rootOrg) {
        this.rootOrg = rootOrg;
    }
}

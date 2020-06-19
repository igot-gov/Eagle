package com.infosys.lexauthoringservices.model;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class Catalog {

    @NotNull(message="type cannot be missing or empty")
    private String type;
    @NotNull(message="name cannot be missing or empty")
    private String name;
    private Properties properties = new Properties();
    private List<Catalog> child = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public List<Catalog> getChild() {
        return child;
    }

    public void setChild(List<Catalog> child) {
        this.child = child;
    }
}

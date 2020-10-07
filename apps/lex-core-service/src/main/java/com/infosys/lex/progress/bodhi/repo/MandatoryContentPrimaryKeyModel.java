package com.infosys.lex.progress.bodhi.repo;


import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class MandatoryContentPrimaryKeyModel implements Serializable {

    private static final long serialVersionUID = 1L;
    @PrimaryKeyColumn(name = "root_org", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String rootOrg;

    @PrimaryKeyColumn(name = "org", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String org;

    @PrimaryKeyColumn(name = "content_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String content_id;

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

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    @Override
    public String toString() {
        return "MandatoryContentPrimaryKeyModel [rootOrg=" + rootOrg + ", org=" + org + ", content_id=" + content_id
                + "]";
    }

}

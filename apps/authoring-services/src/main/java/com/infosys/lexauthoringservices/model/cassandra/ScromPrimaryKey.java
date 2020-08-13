/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.model.cassandra;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@PrimaryKeyClass
public class ScromPrimaryKey implements Serializable {

    @PrimaryKeyColumn(name="root_org",type= PrimaryKeyType.PARTITIONED)
    private String rootOrg;

    @PrimaryKeyColumn(name="org")
    private String org;

    @PrimaryKeyColumn(name="contentId")
    private String contentId;

    @PrimaryKeyColumn(name="userId")
    private String userId;

    public ScromPrimaryKey(){
        super();
    }

    public ScromPrimaryKey(String rootOrg, String org, String contentId, String userId){
        super();
        this.rootOrg = rootOrg;
        this.org = org;
        this.contentId = contentId;
        this.userId = userId;
    }

    public String getRootOrg() {
        return rootOrg;
    }

    public String getOrg() {
        return org;
    }

    public String getContentId() {
        return contentId;
    }

    public String getUserId() {
        return userId;
    }
}

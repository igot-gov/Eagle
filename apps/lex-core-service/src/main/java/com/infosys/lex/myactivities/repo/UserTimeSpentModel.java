/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lex.myactivities.repo;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("daily_time_spent")
public class UserTimeSpentModel {

    @PrimaryKey
    private UserTimeSpentPrimaryKey primaryKey;

    @Column("time_spent")
    private Double timeSpent;

    public UserTimeSpentPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(UserTimeSpentPrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Double getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(Double timeSpent) {
        this.timeSpent = timeSpent;
    }
}

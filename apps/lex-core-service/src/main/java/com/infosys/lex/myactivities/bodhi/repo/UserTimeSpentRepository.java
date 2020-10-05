/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lex.myactivities.bodhi.repo;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTimeSpentRepository extends CassandraRepository<UserTimeSpentModel, UserTimeSpentPrimaryKey> {

    @Query("select * from daily_time_spent where root_org=?0 and user_id=?1 allow filtering;")
    public List<UserTimeSpentModel> findByRootOrgAndUserId(String rootOrg, String userId);

    @Query("select avg(time_spent) from daily_time_spent where root_org=?0 and user_id=?1 allow filtering;")
    public Double avgTimeSpentByRootOrgAndUserId(String rootOrg, String userId);

}

/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.repository;

import com.infosys.model.postgres.EagleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EagleUserRepo extends JpaRepository<EagleUser, String> {

    @Query(nativeQuery = true, value = "select wid, root_org, org, first_name, last_name, email from public.eagle_user where email=?1")
    EagleUser fetchUserByEmail(String email);


}

/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.language.postgredb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.language.entity.LanguageMaster;

@Repository
public interface LanguageMasterRepo extends JpaRepository<LanguageMaster, String> {

	@Modifying
	@Query(nativeQuery=true,value="delete from language_master where language=?1")
	public void delete(String lang);

}

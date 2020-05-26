/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.filters.postgredb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.filters.postgredb.entity.TranslatedField;
import com.infosys.lex.filters.postgredb.projection.FieldValueProjection;

@Repository
public interface FiltersFieldRepo extends JpaRepository<TranslatedField,String> {
	
	@Query(nativeQuery = true, value = "select tv.value as value, tv.translated_value as translatedValue, tf.field as field, tf.translated_field as translatedField from wingspan.translated_field tf left outer join wingspan.translated_value tv on tf.id = tv.field_meta where root_org = ?1 and org = ?2 and language = ?3")
	List<FieldValueProjection> getAllFilterFieldAndValues(String rootOrg, String org, String language);
	
	@Query("SELECT tf from TranslatedField tf where tf.rootOrg = ?1 and tf.org = ?2 and tf.language = ?3 and tf.field = ?4")
	TranslatedField findByRootorgAndOrgAndLangAndField(String rootOrg, String org, String language, String field);
	
}

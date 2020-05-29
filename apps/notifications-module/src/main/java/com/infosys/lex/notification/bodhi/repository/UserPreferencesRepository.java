package com.infosys.lex.notification.bodhi.repository;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.notification.model.cassandra.UserPreferencesModel;
import com.infosys.lex.notification.model.cassandra.UserPreferencesPrimaryKey;

@Repository
public interface UserPreferencesRepository
		extends CassandraRepository<UserPreferencesModel, UserPreferencesPrimaryKey> {

	@Query("select * from user_preferences where root_org=?0 and user_id in ?1")
	public List<UserPreferencesModel> getPreferencesByRootOrgAndUserIds(String rootOrg, List<String> userIds);
}
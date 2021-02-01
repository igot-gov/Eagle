package org.sunbird.userroles.bodhi.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import org.sunbird.userroles.entities.UserRoles;

@Repository
public interface UserRolesRepository extends CassandraRepository<UserRoles, String> {

	public UserRoles findByUserRolesKeyRootOrgAndUserRolesKeyUserId(String rootOrg, String userId);

	@Query("select * from user_roles where root_org=?0 and user_id in ?1")
	public List<UserRoles> findById(String rootOrg, Set<String> users);
}

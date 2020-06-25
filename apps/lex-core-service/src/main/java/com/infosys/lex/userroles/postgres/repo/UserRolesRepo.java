package com.infosys.lex.userroles.postgres.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.infosys.lex.userroles.postgres.entity.UserRole;
import com.infosys.lex.userroles.postgres.entity.UserRolePrimaryKey;

public interface UserRolesRepo extends JpaRepository<UserRole, UserRolePrimaryKey> {

//	List<UserRole> findById(String rootOrg, List<String> roles);

//	List<UserRole> findAllByRole(String rootOrg, String role);

	List<UserRole> findByUserRolePrimaryKeyRootOrgAndUserRolePrimaryKeyUserIdIn(String rootOrg, List<String> userId);

	void deleteAllByUserRolePrimaryKey(List<UserRolePrimaryKey> userRecords);

	List<UserRole> findByUserRolePrimaryKeyRootOrgAndUserRolePrimaryKeyUserId(String rootOrg, String string);

	Slice<UserRole> findByUserRolePrimaryKeyRootOrgAndUserRolePrimaryKeyRoleAndUserRolePrimaryKeyUserIdNot(
			String rootOrg, String role, String masterUser, Pageable pageable);

}

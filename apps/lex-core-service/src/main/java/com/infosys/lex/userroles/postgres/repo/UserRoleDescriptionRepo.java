package com.infosys.lex.userroles.postgres.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.userroles.postgres.entity.UserRoleDescription;
import com.infosys.lex.userroles.postgres.entity.UserRoleDescriptionPrimaryKey;

@Repository
public interface UserRoleDescriptionRepo extends JpaRepository<UserRoleDescription, UserRoleDescriptionPrimaryKey> {

	List<UserRoleDescription> findByKeyRoleInAndKeyLanguage(List<String> masterRolesList, String langCode);

}

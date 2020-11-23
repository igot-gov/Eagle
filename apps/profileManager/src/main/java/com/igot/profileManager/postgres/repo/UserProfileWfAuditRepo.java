package com.igot.profileManager.postgres.repo;

import com.igot.profileManager.postgres.entity.UserProfileWfAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProfileWfAuditRepo extends JpaRepository<UserProfileWfAudit, Integer> {

    List<UserProfileWfAudit> findByRootOrgAndUserIdAndWfId(String rootOrg, String userId, String wfId);

    List<UserProfileWfAudit> findByRootOrgAndUserId(String rootOrg, String userId);

}

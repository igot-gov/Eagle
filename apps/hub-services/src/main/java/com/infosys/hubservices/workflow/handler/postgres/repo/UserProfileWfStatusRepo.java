package com.infosys.hubservices.workflow.handler.postgres.repo;


import com.infosys.hubservices.workflow.handler.postgres.entity.UserProfileWfStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileWfStatusRepo extends JpaRepository<UserProfileWfStatus, String> {

    UserProfileWfStatus findByRootOrgAndOrgAndUserIdAndWfId(String rootOrg, String org, String userId, String wfId);

    List<UserProfileWfStatus> findByRootOrgAndOrgAndCurrentStatus(String rootOrg, String org, String status);

}

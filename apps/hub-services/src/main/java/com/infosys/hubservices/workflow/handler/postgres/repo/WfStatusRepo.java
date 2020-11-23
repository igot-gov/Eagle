package com.infosys.hubservices.workflow.handler.postgres.repo;


import com.infosys.hubservices.workflow.handler.postgres.entity.WfStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WfStatusRepo extends JpaRepository<WfStatusEntity, String> {

    WfStatusEntity findByRootOrgAndOrgAndApplicationIdAndWfId(String rootOrg, String org, String userId, String wfId);

    List<WfStatusEntity> findByRootOrgAndOrgAndCurrentStatus(String rootOrg, String org, String status);

}

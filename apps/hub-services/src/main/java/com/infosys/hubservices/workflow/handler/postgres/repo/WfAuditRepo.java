package com.infosys.hubservices.workflow.handler.postgres.repo;

import com.infosys.hubservices.workflow.handler.postgres.entity.WfAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WfAuditRepo extends JpaRepository<WfAuditEntity, Integer> {

    List<WfAuditEntity> findByRootOrgAndApplicationIdAndWfId(String rootOrg, String userId, String wfId);

    List<WfAuditEntity> findByRootOrgAndApplicationId(String rootOrg, String userId);

}

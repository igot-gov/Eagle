package com.infosys.lexauthoringservices.repository.cassandra.bodhi;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import com.infosys.lexauthoringservices.model.cassandra.ContentWorkFlowModel;
import com.infosys.lexauthoringservices.model.cassandra.ContentWorkFlowPrimaryKeyModel;

public interface ContentWorkFlowRepository
		extends CassandraRepository<ContentWorkFlowModel, ContentWorkFlowPrimaryKeyModel> {

	@Query("SELECT * from content_work_flow where root_org= ?0 AND org= ?1 AND content_type= ?2;")
	public ContentWorkFlowModel findByPrimaryKeyContentWorkFlow(String root_org, String org, String content_type);
	
	@Query("Select * from content_work_flow where root_org= ?0 AND org= ?1;")
	public List<ContentWorkFlowModel> findAllByRootOrg(String rootOrg,String org);
}

package com.igot.profileManager.repository.cassandra.bodhi;


import com.igot.profileManager.models.cassandra.ProfileWf;
import com.igot.profileManager.models.cassandra.ProfileWfPrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileWfRepo extends CassandraRepository<ProfileWf, ProfileWfPrimaryKey> {

    @Query("SELECT * FROM profile_work_flow WHERE root_org=?0 AND org=?1 AND service=?2;")
    ProfileWf getWorkFlowForService(String rootOrg, String org, String service);

}

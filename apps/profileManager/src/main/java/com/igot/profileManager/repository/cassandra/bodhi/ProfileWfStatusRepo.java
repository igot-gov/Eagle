package com.igot.profileManager.repository.cassandra.bodhi;


import com.igot.profileManager.models.cassandra.ProfileWf;
import com.igot.profileManager.models.cassandra.ProfileWfStatus;
import com.igot.profileManager.models.cassandra.ProfileWfStatusPrimarykey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileWfStatusRepo extends CassandraRepository<ProfileWfStatus, ProfileWfStatusPrimarykey> {

    @Query("SELECT * FROM profile_work_flow_status WHERE root_org=?0 AND org=?1 AND current_status=?2 and user_id=?3;")
    ProfileWfStatus getUserProfileStatus(String rootOrg, String org, String currentStatus, String userId);

}

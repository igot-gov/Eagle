package com.igot.profileManager.repository.cassandra.bodhi;


import com.igot.profileManager.models.cassandra.ProfileWf;
import com.igot.profileManager.models.cassandra.ProfileWfStatus;
import com.igot.profileManager.models.cassandra.ProfileWfStatusPrimarykey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileWfStatusRepo extends CassandraRepository<ProfileWfStatus, ProfileWfStatusPrimarykey> {

    @Query("SELECT * FROM profile_work_flow_status WHERE root_org=?0 AND org=?1 AND user_id=?2;")
    ProfileWfStatus getUserProfile(String rootOrg, String org, String userId);

    @Query("SELECT * FROM profile_work_flow_status WHERE root_org=?0 AND org=?1 AND current_status=?2 ALLOW FILTERING;")
    List<ProfileWfStatus> getUserProfiles(String rootOrg, String org, String status);

}

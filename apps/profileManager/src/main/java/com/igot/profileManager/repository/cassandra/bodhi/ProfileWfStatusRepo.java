package com.igot.profileManager.repository.cassandra.bodhi;


import com.igot.profileManager.models.cassandra.ProfileWfStatus;
import com.igot.profileManager.models.cassandra.ProfileWfStatusPrimarykey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileWfStatusRepo extends CassandraRepository<ProfileWfStatus, ProfileWfStatusPrimarykey> {

}

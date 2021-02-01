package org.sunbird.common.repo;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMVRepository extends CassandraRepository<UserMVModel, String> {

}
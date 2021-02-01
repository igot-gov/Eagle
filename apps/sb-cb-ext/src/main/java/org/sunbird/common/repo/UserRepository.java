package org.sunbird.common.repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CassandraRepository<UserModel, String> {

	/**
	 * fetches one email id from the users table
	 * @return
	 */
	@Query("Select id,email from user limit 1")
	public Map<String,Object> findOneId();
	
	/**
	 * fetch emails from given uuids from users table
	 */
	
	@Query("Select id,email from user where id in ?0")
	public List<Map<String,Object>> findEmaildsForids(List<String> ids);
}
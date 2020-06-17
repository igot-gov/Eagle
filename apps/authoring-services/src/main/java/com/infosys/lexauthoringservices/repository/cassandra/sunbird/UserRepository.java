package com.infosys.lexauthoringservices.repository.cassandra.sunbird;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import com.infosys.lexauthoringservices.model.cassandra.User;

public interface UserRepository extends CassandraRepository<User, String> {

	@Query("select * from user where email=?0")
	public User findByEmail(String email);
}

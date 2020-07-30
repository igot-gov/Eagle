package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import com.infosys.lex.ext.Models.Cassandra.UserContentProgress;
import com.infosys.lex.ext.Models.Cassandra.UserContentProgressPrimaryKey;

public interface UserContentProgressRepository
		extends CassandraRepository<UserContentProgress, UserContentProgressPrimaryKey> {

	@Query("select user_id,content_type,content_id,progress from user_content_progress where userEmail=?0 and content_type in ?1")
	List<UserContentProgress> findyByUserAndContentIds(String userEmail, List<String> contentTypes,
			List<String> contentIds);

}

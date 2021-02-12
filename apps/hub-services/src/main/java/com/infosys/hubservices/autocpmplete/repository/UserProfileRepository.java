package com.infosys.hubservices.autocpmplete.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.infosys.hubservices.autocpmplete.dto.UserProfile;

public interface UserProfileRepository extends CrudRepository<UserProfile, Integer>{
	
	@Query(value = "SELECT up.\"userId\" FROM \"V_UserProfile\" up WHERE up.\"personalDetails_osid\" = ?1", nativeQuery = true)
	Object findByPersonalDetailsosidNative(String osid);
}

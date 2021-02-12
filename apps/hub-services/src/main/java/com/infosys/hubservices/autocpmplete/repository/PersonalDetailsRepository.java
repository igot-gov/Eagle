package com.infosys.hubservices.autocpmplete.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.infosys.hubservices.autocpmplete.dto.PersonalDetails;

public interface PersonalDetailsRepository extends CrudRepository<PersonalDetails, Integer>{
	
	@Query(value = "SELECT pd.osid,pd.firstname,pd.surname,pd.\"primaryEmail\",pd.username FROM \"V_personalDetails\" pd WHERE pd.username LIKE ?1%", nativeQuery = true)
	List<Object[]> findByUsernameStartsWithNative(String username);
}

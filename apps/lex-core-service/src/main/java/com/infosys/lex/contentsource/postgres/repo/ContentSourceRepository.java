/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.contentsource.postgres.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.contentsource.postgres.entity.ContentSource;
import com.infosys.lex.contentsource.postgres.entity.ContentSourcePrimaryKey;
import com.infosys.lex.contentsource.postgres.projection.ContentSourceProj;
import com.infosys.lex.contentsource.postgres.projection.ContentSourceShortNameProj;

@Repository
public interface ContentSourceRepository extends JpaRepository<ContentSource, ContentSourcePrimaryKey> {

	public List<ContentSourceProj> findAllByKeyRootOrg(String rootOrg);
	
	public List<ContentSourceShortNameProj> findByKeyRootOrgAndSourceNameIn( String rootOrg ,List<String> sourceNames);
	
	public ContentSourceProj findByKeyRootOrgAndKeySourceShortName(String rootOrg,String  sourceShortName);
	
	public List<ContentSourceProj> findAllByKeyRootOrgAndRegistrationProvided(String rootOrg,Boolean registrationProvided);
	
	public ContentSourceProj findByKeyRootOrgAndKeySourceShortNameAndRegistrationProvided( String rootOrg ,String sourceShortName,Boolean registrationProvided);
	

}
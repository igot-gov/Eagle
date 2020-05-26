/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.rating.bodhi.repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserContentRatingRepository extends CassandraRepository<UserContentRatingModel,UserContentRatingPrimaryKeyModel>{

	
	@Query("Select avg(rating) as \"averageRating\",count(rating) as \"ratingCount\" from user_content_rating where root_org = ?0 and content_id = ?1")
	public Map<String,Object> getAvgRatingAndRatingCountForContentId(String rootOrg,String contentId );
	
	@Query("Select content_id, avg(rating) as \"averageRating\",count(rating) as \"ratingCount\" from user_content_rating where root_org = ?0 and content_id in ?1 group by content_id")
	public List<Map<String,Object>> getAvgRatingAndRatingCountForContentIds(String rootOrg,List<String> contentId );
	
	
}

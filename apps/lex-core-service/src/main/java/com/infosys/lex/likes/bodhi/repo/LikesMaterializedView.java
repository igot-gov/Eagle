/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.likes.bodhi.repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.likes.entities.LikesMaterialize;
import com.infosys.lex.likes.entities.LikesMaterializeKey;

@Repository
public interface LikesMaterializedView extends CassandraRepository<LikesMaterialize, LikesMaterializeKey> {

	@Query("select content_id,count(*) from bodhi.user_likes_by_content where root_org=?0 and content_id in ?1 group by root_org,content_id")
	public List<Map<String, Object>> getTotalLikes(String rootOrg, List<String> contentId);

	@Query("select content_id,count(*) from bodhi.user_likes_by_content where root_org=?0 and content_id = ?1 group by root_org,content_id")
	public Map<String, Object> getTotalLikesOfContent(String rootOrg, String contentId);

}

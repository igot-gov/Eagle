/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.playlist.bodhi.repo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.playlist.entities.PlaylistRecent;
import com.infosys.lex.playlist.entities.PlaylistRecentKey;

@Repository
public interface PlaylistRecentRepo
		extends CassandraRepository<PlaylistRecent, PlaylistRecentKey>, PlaylistRecentCustomRepository {

	@Query("delete from playlist_recent where root_org=?0 and user_id=?1 and playlist_id=?2")
	void deleteRecentContents(String rootOrg, String userId, UUID playlistId);

	List<Map<String, Object>> findByPlaylistRecentkeyRootOrgAndPlaylistRecentkeyUserId(String rootOrg, String userId);

	@Query("delete from playlist_recent where root_org=?0 and user_id =?1 and playlist_id=?2 and resource_id in ?3")
	void deleteAllByResourceId(String rootOrg, String userId, UUID fromString, List<String> toBeDeleted);
}

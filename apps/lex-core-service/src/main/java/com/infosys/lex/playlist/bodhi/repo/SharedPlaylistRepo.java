/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.playlist.bodhi.repo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.playlist.entities.PlaylistShared;
import com.infosys.lex.playlist.entities.PlaylistSharedKey;

@Repository
public interface SharedPlaylistRepo extends CassandraRepository<PlaylistShared, PlaylistSharedKey> {

	@Query("select * from playlist_shared where root_org =?0 and playlist_id=?1 and shared_by=?2 allow filtering")
	Optional<PlaylistShared> findByRootOrgAndPlaylistIdAndSharedBy(String rootOrg, UUID playlistId, String sharedBy);

	List<Map<String, Object>> findByPlaylistSharedKeyRootOrgAndPlaylistSharedKeySharedWith(String rootOrg,
			String userId);

}

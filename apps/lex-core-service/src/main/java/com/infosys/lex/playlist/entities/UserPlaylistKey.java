/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.playlist.entities;

import java.util.UUID;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class UserPlaylistKey {

	@PrimaryKeyColumn(name = "root_org", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String rootOrg;

	@PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String userId;

	@PrimaryKeyColumn(name = "playlist_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
	private UUID playlistId;

	public String getRootOrg() {
		return rootOrg;
	}

	public UserPlaylistKey() {
		super();
	}

	public UserPlaylistKey(String rootOrg, String userId, UUID playlistId) {
		super();
		this.rootOrg = rootOrg;
		this.userId = userId;
		this.playlistId = playlistId;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public UUID getPlaylistId() {
		return playlistId;
	}

	public void setPlaylistId(UUID playlistId) {
		this.playlistId = playlistId;
	}

	@Override
	public String toString() {
		return "UserPlaylistKey [rootOrg=" + rootOrg + ", userId=" + userId + ", playlistId=" + playlistId + "]";
	}

}

/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.playlist.entities;

import java.util.UUID;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class PlaylistRecentKey {

	public PlaylistRecentKey() {
		super();
	}

	public PlaylistRecentKey(String rootOrg, String userId, UUID playlistId, String resourceId) {
		super();
		this.rootOrg = rootOrg;
		this.userId = userId;
		this.playlistId = playlistId;
		this.resourceId = resourceId;
	}

	@PrimaryKeyColumn(value = "root_org", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String rootOrg;

	@PrimaryKeyColumn(value = "user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String userId;

	@PrimaryKeyColumn(value = "playlist_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
	private UUID playlistId;

	@PrimaryKeyColumn(value = "resource_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
	private String resourceId;

	public String getRootOrg() {
		return rootOrg;
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

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	@Override
	public String toString() {
		return "PlaylistRecentKey [rootOrg=" + rootOrg + ", userId=" + userId + ", playlistId=" + playlistId
				+ ", resourceId=" + resourceId + ", getRootOrg()=" + getRootOrg() + ", getUserId()=" + getUserId()
				+ ", getPlaylistId()=" + getPlaylistId() + ", getResourceId()=" + getResourceId() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}

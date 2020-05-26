/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.playlist.entities;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_playlist")
public class UserPlaylist {

	@PrimaryKey
	private UserPlaylistKey userPlayListKey;

	@Column("created_on")
	private Date createdOn;

	@Column("isshared")
	private int isShared;

	@Column("last_updated_on")
	private Date lastUpdatedOn;

	@Column("playlist_title")
	private String playlistTitle;

	@Column("resource_ids")
	private List<String> resourceIds;

	@Column("shared_by")
	private String sharedBy;

	@Column("source_playlist_id")
	private UUID sourcePlaylistId;

	private String visibility;

	public UserPlaylistKey getUserPlayListKey() {
		return userPlayListKey;
	}

	public void setUserPlayListKey(UserPlaylistKey userPlayListKey) {
		this.userPlayListKey = userPlayListKey;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public int getIsShared() {
		return isShared;
	}

	public void setIsShared(int isShared) {
		this.isShared = isShared;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public UUID getSourcePlaylistId() {
		return sourcePlaylistId;
	}

	public void setSourcePlaylistId(UUID sourcePlaylistId) {
		this.sourcePlaylistId = sourcePlaylistId;
	}

	public String getPlaylistTitle() {
		return playlistTitle;
	}

	public void setPlaylistTitle(String playlistTitle) {
		this.playlistTitle = playlistTitle;
	}

	public String getSharedBy() {
		return sharedBy;
	}

	public void setSharedBy(String sharedBy) {
		this.sharedBy = sharedBy;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public UserPlaylist() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserPlaylist(UserPlaylistKey userPlayListKey, Date createdOn, int isShared, Date lastUpdatedOn,
			String playlistTitle, List<String> resourceIds, String sharedBy, UUID sourcePlaylistId, String visibility) {
		super();
		this.userPlayListKey = userPlayListKey;
		this.createdOn = createdOn;
		this.isShared = isShared;
		this.lastUpdatedOn = lastUpdatedOn;
		this.playlistTitle = playlistTitle;
		this.resourceIds = resourceIds;
		this.sharedBy = sharedBy;
		this.sourcePlaylistId = sourcePlaylistId;
		this.visibility = visibility;
	}

	@Override
	public String toString() {
		return "UserPlaylist [userPlayListKey=" + userPlayListKey + ", createdOn=" + createdOn + ", isShared="
				+ isShared + ", lastUpdatedOn=" + lastUpdatedOn + ", playlistTitle=" + playlistTitle + ", resource_ids="
				+ resourceIds + ", sharedBy=" + sharedBy + ", sourcePlaylistId=" + sourcePlaylistId + ", visibility="
				+ visibility + "]";
	}

	public List<String> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(List<String> resourceIds) {
		this.resourceIds = resourceIds;
	}

}

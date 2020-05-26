/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.playlist.entities;

import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("playlist_shared")
public class PlaylistShared {

	@PrimaryKey
	private PlaylistSharedKey playlistSharedKey;

	@Column("last_updated_on")
	private Date lastUpdatedOn;

	@Column("playlist_title")
	private String playlistTitle;

	@Column("resource_ids")
	private List<String> resourceIds;

	@Column("shared_by")
	private String sharedBy;

	@Column("shared_on")
	private Date sharedOn;

	private String visibility;

	public PlaylistShared() {
		super();
	}

	public PlaylistShared(PlaylistSharedKey playlistSharedKey, Date lastUpdatedOn, String playlistTitle,
			List<String> resourceIds, String sharedBy, Date sharedOn, String visibility) {
		super();
		this.playlistSharedKey = playlistSharedKey;
		this.lastUpdatedOn = lastUpdatedOn;
		this.playlistTitle = playlistTitle;
		this.resourceIds = resourceIds;
		this.sharedBy = sharedBy;
		this.sharedOn = sharedOn;
		this.visibility = visibility;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getPlaylistTitle() {
		return playlistTitle;
	}

	public void setPlaylistTitle(String playlistTitle) {
		this.playlistTitle = playlistTitle;
	}

	public List<String> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(List<String> resourceIds) {
		this.resourceIds = resourceIds;
	}

	public Date getSharedOn() {
		return sharedOn;
	}

	public void setSharedOn(Date sharedOn) {
		this.sharedOn = sharedOn;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public PlaylistSharedKey getPlaylistSharedKey() {
		return playlistSharedKey;
	}

	public void setPlaylistSharedKey(PlaylistSharedKey playlistSharedKey) {
		this.playlistSharedKey = playlistSharedKey;
	}

	public String getSharedBy() {
		return sharedBy;
	}

	public void setSharedBy(String sharedBy) {
		this.sharedBy = sharedBy;
	}

	@Override
	public String toString() {
		return "PlaylistShared [playlistSharedKey=" + playlistSharedKey + ", lastUpdatedOn=" + lastUpdatedOn
				+ ", playlistTitle=" + playlistTitle + ", resourceIds=" + resourceIds + ", sharedBy=" + sharedBy
				+ ", sharedOn=" + sharedOn + ", visibility=" + visibility + "]";
	}

}

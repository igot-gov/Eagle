/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.playlist.entities;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("playlist_recent")
public class PlaylistRecent {

	public PlaylistRecent() {
		super();
	}

	public PlaylistRecent(PlaylistRecentKey playlistRecentkey, Timestamp lastUpdatedOn) {
		super();
		this.playlistRecentkey = playlistRecentkey;
		this.lastUpdatedOn = lastUpdatedOn;
	}

	@PrimaryKey
	private PlaylistRecentKey playlistRecentkey;

	@Column("last_updated_on")
	private Timestamp lastUpdatedOn;

	public PlaylistRecentKey getPlaylistRecentkey() {
		return playlistRecentkey;
	}

	public void setPlaylistRecentkey(PlaylistRecentKey playlistRecentkey) {
		this.playlistRecentkey = playlistRecentkey;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Timestamp lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}
}

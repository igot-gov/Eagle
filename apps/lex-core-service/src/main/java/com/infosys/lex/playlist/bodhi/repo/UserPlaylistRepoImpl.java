/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.playlist.bodhi.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraBatchOperations;
import org.springframework.data.cassandra.core.CassandraOperations;

import com.infosys.lex.playlist.entities.PlaylistRecent;
import com.infosys.lex.playlist.entities.PlaylistShared;
import com.infosys.lex.playlist.entities.UserPlaylist;

/**
 * @author yogesh.bansal
 *
 */
public class UserPlaylistRepoImpl implements UserPlaylistCustomRepository {

	@Autowired
	CassandraOperations cassOps;

	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
substitute url based on requirement
	 * java.util.List)
	 */
	@Override
	public void insertUserAndRecentPlaylist(UserPlaylist playlist, List<PlaylistRecent> recentPlaylistContents) {

		CassandraBatchOperations batchOps = cassOps.batchOps();
		batchOps.insert(playlist);
		batchOps.insert(recentPlaylistContents);
		batchOps.execute();
	}


	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
substitute url based on requirement
substitute url based on requirement
	 */
	@Override
	public void acceptPlaylist(UserPlaylist playlist, PlaylistShared sharedPlaylist,
			List<PlaylistRecent> recentPlaylistContents) {

		CassandraBatchOperations batchOperations = cassOps.batchOps();

		batchOperations.insert(playlist);
		batchOperations.insert(recentPlaylistContents);
		batchOperations.delete(sharedPlaylist);
		batchOperations.execute();
	}

	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
substitute url based on requirement
	 * java.util.List)
	 */
	public void deleteContents(UserPlaylist userPlaylist, List<PlaylistRecent> recentPlaylist) {
		CassandraBatchOperations batchOperations = cassOps.batchOps();

		if (userPlaylist.getResourceIds().isEmpty()) {
			batchOperations.delete(userPlaylist);
		} else {
			batchOperations.insert(userPlaylist);
		}

		batchOperations.delete(recentPlaylist);
		batchOperations.execute();
	}

	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
substitute url based on requirement
	 * java.util.List)
	 */
	public void deletePlaylist(UserPlaylist userPlaylist, List<PlaylistRecent> recentPlaylist) {

		CassandraBatchOperations batchOperations = cassOps.batchOps();

		batchOperations.delete(userPlaylist);
		batchOperations.delete(recentPlaylist);
		batchOperations.execute();
	}

	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
	 * sharePlayList(java.util.List,
substitute url based on requirement
	 */
	@Override
	public void sharePlayList(List<PlaylistShared> userPlaylist, UserPlaylist playlistShared) {
		
		CassandraBatchOperations batchOperations = cassOps.batchOps();
		batchOperations.insert(userPlaylist);
		batchOperations.insert(playlistShared);
		batchOperations.execute();
	}


	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
substitute url based on requirement
	 * java.util.List, java.util.List)
	 */
	@Override
	public void updatePlaylist(UserPlaylist userPlaylist, List<PlaylistRecent> toBeInserted,
			List<PlaylistRecent> toBeDeleted) {

		CassandraBatchOperations batchOperations = cassOps.batchOps();

		if (userPlaylist.getResourceIds().isEmpty()) {
			batchOperations.delete(userPlaylist);
		} else {
			batchOperations.insert(userPlaylist);
		}

		// check if to deleted is empty or not
		if (toBeDeleted != null && !toBeDeleted.isEmpty()) {
			batchOperations.delete(toBeDeleted);
		}

		batchOperations.insert(toBeInserted);
		batchOperations.execute();
	}

}

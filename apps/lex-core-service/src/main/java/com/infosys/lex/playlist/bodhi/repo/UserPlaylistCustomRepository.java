/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.playlist.bodhi.repo;

import java.util.List;

import com.infosys.lex.playlist.entities.PlaylistRecent;
import com.infosys.lex.playlist.entities.PlaylistShared;
import com.infosys.lex.playlist.entities.UserPlaylist;

public interface UserPlaylistCustomRepository {

	/**
	 * this function inserts in user playlist and recent playlist
	 * 
	 * @param playlist
	 * @param recentPlaylistContents
	 */
	public void insertUserAndRecentPlaylist(UserPlaylist playlist, List<PlaylistRecent> recentPlaylistContents);

	/**
	 * This function accepts a shared playlist creates a playlist and deletes from
	 * shared playlist
	 * 
	 * @param playlist
	 * @param sharedPlaylist
	 */
	public void acceptPlaylist(UserPlaylist playlist, PlaylistShared sharedPlaylist,
			List<PlaylistRecent> recentPlaylistContents);

	/**
	 * this function updates playlist of a user with new object playlist and deletes
	 * contents in recentPlaylist
	 * 
	 * @param userPlaylist
	 * @param recentPlaylist
	 */
	public void deleteContents(UserPlaylist userPlaylist, List<PlaylistRecent> recentPlaylist);

	/**
	 * this function deletes playlist of a user and delete contents in
	 * recentPlaylist
	 * 
	 * @param userPlaylist
	 * @param recentPlaylist
	 */
	public void deletePlaylist(UserPlaylist userPlaylist, List<PlaylistRecent> recentPlaylist);

	/**
	 * This function shares a playlist with user and creates a share entry
	 * corresponding to every user and updates isShared of userPlaylist to 1
	 * 
	 * @param userPlaylist   Users to be added
	 * @param playlistShared
	 */
	public void sharePlayList(List<PlaylistShared> userPlaylist, UserPlaylist playlistShared);
	
	/**
	 * This function updates the user playlist with new userpPlaylist and inserts
	 * and deletes record in playlist recent
	 * 
	 * @param userPlaylist
	 * @param toBeInserted
	 * @param toBeDeleted
	 */
	public void updatePlaylist(UserPlaylist userPlaylist, List<PlaylistRecent> toBeInserted,
			List<PlaylistRecent> toBeDeleted);

}

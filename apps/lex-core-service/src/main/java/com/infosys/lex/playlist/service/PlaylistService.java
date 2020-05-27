/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.playlist.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.infosys.lex.playlist.dto.PlaylistRequest;

public interface PlaylistService {

	/**
	 * This function creates a playlist userplaylist table and recent_playlist table
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistBody
	 * @throws Exception
	 */
	public void createPlayList(String rootOrg, String userId, PlaylistRequest playlistBody) throws Exception;

	/**
	 * This function shares a playlist with multiple users
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @param recipientMap
	 * @return It returns a map of invalid users and content_access_denied_users
	 * @throws Exception
	 */
	public Map<String, Object> sharePlaylist(String rootOrg, String userId, String playlistId,
			Map<String, Object> recipientMap) throws Exception;

	/**
	 * This function deletes the playlist
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @throws Exception
	 */
	public void deletePlaylist(String rootOrg, String userId, String playlistId) throws Exception;

	/**
	 * This function delete a content from playlist
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @param lexId
	 * @throws Exception
	 */
	public void deleteContent(String rootOrg, String userId, String playlistId, String lexId) throws Exception;

	/**
	 * This function adds the content in the playlist
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @param contents
	 * @throws Exception
	 */
	public void addContents(String rootOrg, String userId, String playlistId, List<String> contents) throws Exception;

	/**
	 * This function adds a content in playlist
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @param lexId
	 * @throws Exception
	 */
	public void addContent(String rootOrg, String userId, String playlistId, String lexId) throws Exception;

	/**
	 * This function fetches all the playlists of a user
	 * 
	 * @param rootOrg
	 * @param userId
	 * @return It returns a list of maps all the playlists of users in descending
	 *         order of last_updated_on.
	 * @throws Exception
	 */
	public List<Map<String, Object>> getUserPlaylists(String rootOrg, String userId) throws Exception;

	/**
	 * This function fetches all the playlists shared by a user
	 * 
	 * @param rootOrg
	 * @param userId
	 * @return It return a list of maps of all the playlists shared by a user.
	 * @throws Exception
	 */
	public List<Map<String, Object>> getUserSharedPlaylist(String rootOrg, String userId) throws Exception;

	/**
	 * This function fetches the playlist of a user
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param count   count of playlists
	 * @return
	 * @throws Exception
	 */

	public Map<String, Object> getPlaylistContent(String rootOrg, String userId, Integer size, String page)
			throws Exception;

	/**
	 * This function fetches detailed playlist content
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getDetailedPlaylistContent(String rootOrg, String userId, List<String> sourceFields,
			String isInIntranet, String isStandAlone, List<String> contentType, Integer size, String page)
			throws Exception;

	/**
	 * This funtion fetches the playlist of a user
	 * 
	 * @param rootOrg
	 * @param userId
	 * @return content with meta data
	 * @throws Exception
	 */
	public List<Map<String, Object>> getDetailedUserPlaylists(String rootOrg, String userId, List<String> sourceFields)
			throws Exception;

	/**
	 * This function updates the playlist with new content and title
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @param playlistBody
	 * @throws Exception
	 */
	public void updatePlaylist(String rootOrg, String userId, String playlistId, Map<String, Object> playlistBody)
			throws Exception;

	/**
	 * This function accepts or rejects a playlist shared by a user.
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @param status
	 * @throws Exception
	 */
	public void acceptRejectPlaylist(String rootOrg, String userId, String playlistId, String status) throws Exception;

	/**
	 * This function fetches detailed playlist shared by a user
	 * 
	 * @param rootOrg
	 * @param userId
	 * @return Map of detailed description of meta data shared by a user
	 * @throws Exception
	 */
	public List<Map<String, Object>> getDetailedSharedPlaylist(String rootOrg, String userId, List<String> sourceFields)
			throws Exception;

	/**
	 * This function fetches the detailed list of resource user and shared by user
	 * has
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @return detailed map of common content and content only user has and the
	 *         content only shared by user has
	 * @throws Exception
	 */
	public Map<String, Object> getDetailedPlaylistSyncInfo(String rootOrg, String userId, String playlistId,
			List<String> sourceFields) throws Exception;

	/**
	 * This function fetches the list of resource user and shared by user has
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @return Map of common content and content only user has and the content only
	 *         shared by user has
	 * @throws Exception
	 */
	public Map<String, Object> getPlaylistSyncInfo(String rootOrg, String userId, String playlistId) throws Exception;

	/**
	 * This function fetches all the data of a resource within a playlist
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getUserPlaylist(String rootOrg, String userId, String playlistId,
			List<String> sourceFields) throws Exception;

	/**
	 * deletes multiple contents from userplaylist and recent table
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @param content
	 * @throws Exception
	 */
	public void deleteMultiple(String rootOrg, String userId, String playlistId, Map<String, Object> content)
			throws Exception;

}

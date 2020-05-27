/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.playlist.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.playlist.dto.PlaylistRequest;
import com.infosys.lex.playlist.service.PlaylistService;

@RestController
@CrossOrigin("*")
public class PlaylistController {

	@Autowired
	PlaylistService playListService;

	/**
	 * \ This function creates a playlist user playlist table and recent_playlist
	 * table
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistBody
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v1/users/{user_id}/playlists")
	public ResponseEntity<?> createPlaylist(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable("user_id") String userId, @Valid @RequestBody PlaylistRequest playlistBody) throws Exception {

		playListService.createPlayList(rootOrg, userId, playlistBody);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * This function updates the playlist with new content and title
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @param playlistBody
	 * @throws Exception
	 */
	@PatchMapping("/v1/users/{user_id}/playlists/{playlist_id}")
	public ResponseEntity<?> updatePlaylist(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable("user_id") String userId, @PathVariable("playlist_id") String playlistId,
			@RequestBody Map<String, Object> playlistBody) throws Exception {

		playListService.updatePlaylist(rootOrg, userId, playlistId, playlistBody);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/**
	 * This function adds the content in the playlist
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @param contents
	 * @throws Exception
	 */
	@PostMapping("/v1/users/{user_id}/playlists/{playlist_id}/contents")
	public ResponseEntity<?> addContents(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable(value = "user_id") String userId, @PathVariable(value = "playlist_id") String playlistId,
			@RequestBody Map<String, List<String>> request) throws Exception {

		List<String> contentId = request.get("content_ids");
		playListService.addContents(rootOrg, userId, playlistId, contentId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/**
	 * This function adds a content in playlist
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @param lexId
	 * @throws Exception
	 */
	@PostMapping("/v1/users/{user_id}/playlists/{playlist_id}/contents/{lex_id}")
	public ResponseEntity<?> addContent(@RequestHeader(value = "rootOrg") String rootOrg,
										@PathVariable(value = "user_id") String userId, @PathVariable(value = "playlist_id") String playlistId,
										@PathVariable(value = "lex_id") String lexId) throws Exception  {

		playListService.addContent(rootOrg, userId, playlistId, lexId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

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
	@PostMapping("/v1/users/{user_id}/playlists/{playlist_id}/share")
	public ResponseEntity<Map<String, Object>> sharePlaylist(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable(value = "user_id") String userId, @PathVariable(value = "playlist_id") String playlistId,
			@RequestBody Map<String, Object> shareMap) throws Exception {

		return new ResponseEntity<Map<String, Object>>(
				playListService.sharePlaylist(rootOrg, userId, playlistId, shareMap), HttpStatus.OK);
	}

	/**
	 * This function accepts or rejects a playlist shared by a user.
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @param status
	 * @throws Exception
	 */
	@PostMapping("/v1/users/{user_id}/shared-playlists/{playlist_id}/{status}")
	public ResponseEntity<?> acceptRejectPlaylist(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable(value = "user_id") String userId, @PathVariable(value = "playlist_id") String playlistId,
			@PathVariable(value = "status") String status) throws Exception {

		playListService.acceptRejectPlaylist(rootOrg, userId, playlistId, status);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/**
	 * This function deletes the playlist
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @throws Exception
	 */

	@DeleteMapping("/v1/users/{user_id}/playlists/{playlist_id}")
	public ResponseEntity<?> deletePlaylist(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable(value = "user_id") String userId, @PathVariable(value = "playlist_id") String playlistId)
			throws Exception {

		playListService.deletePlaylist(rootOrg, userId, playlistId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/**
	 * This function delete a content from playlist
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @param lexId

	 */
	@DeleteMapping("/v1/users/{user_id}/playlists/{playlist_id}/contents/{lex_id}")
	public ResponseEntity<?> deleteContent(@RequestHeader(value = "rootOrg") String rootOrg,
										   @PathVariable(value = "user_id") String userId, @PathVariable(value = "playlist_id") String playlistId,
										   @PathVariable(value = "lex_id") String lexId)  throws Exception{

		playListService.deleteContent(rootOrg, userId, playlistId, lexId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/**
	 * This funtion fetches the playlist of a user
	 * 
	 * @param rootOrg
	 * @param userId
	 * @return content with meta data
	 * @throws Exception
	 */
	@GetMapping("/v1/users/{user_id}/playlists")
	public ResponseEntity<List<Map<String, Object>>> getUserPlaylists(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable(value = "user_id") String userId,
			@RequestParam(value = "details-required", defaultValue = "true") Boolean details,
			@RequestParam(value = "sourceFields", required = false) List<String> sourceFields) throws Exception {

		List<Map<String, Object>> listOfData = new ArrayList<>();
		if (details) {
			listOfData = playListService.getDetailedUserPlaylists(rootOrg, userId, sourceFields);
		} else {
			listOfData = playListService.getUserPlaylists(rootOrg, userId);
		}
		return new ResponseEntity<List<Map<String, Object>>>(listOfData, HttpStatus.OK);
	}

	/**
	 * This function fetches detailed playlist shared by a user
	 * 
	 * @param rootOrg
	 * @param userId
	 * @return Map of detailed description of meta data shared by a user
	 * @throws Exception
	 */

	@GetMapping("/v1/users/{user_id}/shared-playlist")
	public ResponseEntity<List<Map<String, Object>>> getUserSharedPlaylist(
			@RequestHeader(value = "rootOrg") String rootOrg, @PathVariable(value = "user_id") String userId,
			@RequestParam(value = "details-required", defaultValue = "true") Boolean details,
			@RequestParam(value = "sourceFields", required = false) List<String> sourceFields) throws Exception {

		List<Map<String, Object>> sharedPlaylist = new ArrayList<>();
		if (details) {
			sharedPlaylist = playListService.getDetailedSharedPlaylist(rootOrg, userId, sourceFields);
		} else {
			sharedPlaylist = playListService.getUserSharedPlaylist(rootOrg, userId);
		}

		return new ResponseEntity<List<Map<String, Object>>>(sharedPlaylist, HttpStatus.OK);
	}

	/**
	 * This function fetches detailed playlist content
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/users/{user_id}/playlist-contents")
	public ResponseEntity<Map<String, Object>> getPlaylistContent(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable(value = "user_id") String userId,
			@RequestParam(value = "details-required", defaultValue = "true") Boolean details,
			@RequestParam(defaultValue = "0", required = false, name = "page") String page,
			@RequestParam(defaultValue = "20", required = false, name = "size") Integer size,
			@RequestParam(defaultValue = "all", required = false, name = "isInIntranet") String isInIntranet,
			@RequestParam(defaultValue = "all", required = false, name = "isStandAlone") String isStandAlone,
			@RequestParam(required = false, name = "contentType") List<String> contentType,
			@RequestParam(value = "sourceFields", required = false) List<String> sourceFields) throws Exception {

		Map<String, Object> playlistContent = new HashMap<>();
		if (details) {
			playlistContent = playListService.getDetailedPlaylistContent(rootOrg, userId, sourceFields, isInIntranet,
					isStandAlone, contentType, size, page);
		} else {
			playlistContent = playListService.getPlaylistContent(rootOrg, userId, size, page);
		}
		return new ResponseEntity<Map<String, Object>>(playlistContent, HttpStatus.OK);
	}

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
	@GetMapping("/v1/users/{user_id}/playlists/{playlist_id}/sync-info")
	public ResponseEntity<Map<String, Object>> fetchPlaylistSyncInfo(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable(value = "user_id") String userId, @PathVariable(value = "playlist_id") String playlistId,
			@RequestParam(value = "details-required", defaultValue = "true") Boolean details,
			@RequestParam(value = "sourceFields", required = false) List<String> sourceFields) throws Exception {

		Map<String, Object> playlistInfo = new HashMap<String, Object>();
		if (details) {
			playlistInfo = playListService.getDetailedPlaylistSyncInfo(rootOrg, userId, playlistId, sourceFields);
		} else {
			playlistInfo = playListService.getPlaylistSyncInfo(rootOrg, userId, playlistId);
		}
		return new ResponseEntity<Map<String, Object>>(playlistInfo, HttpStatus.OK);
	}

	/**
	 * This function fetches all the data of a resource within a playlist
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/users/{user_id}/playlists/{playlist_id}")
	public ResponseEntity<Map<String, Object>> getPlaylistById(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable(value = "user_id") String userId, @PathVariable(value = "playlist_id") String playlistId,
			@RequestParam(value = "sourceFields", required = false) List<String> sourceFields) throws Exception {

		return new ResponseEntity<Map<String, Object>>(
				playListService.getUserPlaylist(rootOrg, userId, playlistId, sourceFields), HttpStatus.OK);
	}

	/**
	 * deletes multiple contents from userplaylist and recent table
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param playlistId
	 * @param content
	 * @throws Exception
	 */

	@DeleteMapping("/v1/users/{user_id}/playlists/{playlist_id}/contents")
	public ResponseEntity<?> deleteMultiple(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable(value = "user_id") String userId, @PathVariable(value = "playlist_id") String playlistId,
			@RequestBody Map<String, Object> content) throws Exception {

		playListService.deleteMultiple(rootOrg, userId, playlistId, content);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);

	}
}

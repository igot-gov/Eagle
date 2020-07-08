package com.infosys.lex.playlist.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.infosys.lex.core.logger.LexLogger;
import com.infosys.lex.hierarchy.service.HierarchyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.playlist.bodhi.repo.PlaylistRecentRepo;
import com.infosys.lex.playlist.bodhi.repo.SharedPlaylistRepo;
import com.infosys.lex.playlist.bodhi.repo.UserPlaylistRepo;
import com.infosys.lex.playlist.dto.PlaylistRequest;
import com.infosys.lex.playlist.entities.PlaylistRecent;
import com.infosys.lex.playlist.entities.PlaylistRecentKey;
import com.infosys.lex.playlist.entities.PlaylistShared;
import com.infosys.lex.playlist.entities.PlaylistSharedKey;
import com.infosys.lex.playlist.entities.UserPlaylist;
import com.infosys.lex.playlist.entities.UserPlaylistKey;

/**
 * @author yogesh.bansal
 *
 */
@Service
public class PlaylistServiceImpl implements PlaylistService {

	@Autowired
	UserUtilityService userUtilService;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ContentService contentService;

	@Autowired
	UserPlaylistRepo userPlayListRepo;

	@Autowired
	PlaylistRecentRepo playListRecentRepo;

	@Autowired
	PlaylistRecentRepo playlistRecentRepo;

	@Autowired
	LexServerProperties props;

	@Autowired
	SharedPlaylistRepo sharedPlaylistRepo;

	@Autowired
	Environment env;

	private String notifSvcPort;

	private String notifSvcIp;

	private LexLogger logger = new LexLogger(PlaylistServiceImpl.class.getName());
	private ObjectMapper mapper = new ObjectMapper();


	@PostConstruct
	void init() {
		notifSvcPort = env.getProperty("notification.service.port");
		notifSvcIp = env.getProperty("notification.service.ip");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#createPlayList(java.lang.
	 * String, java.lang.String, com.infosys.lex.playlist.entities.PlaylistRequest)
	 */
	@Override
	public void createPlayList(String rootOrg, String userId, PlaylistRequest playlistBody) throws Exception {

		// Validating User
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		// Remove duplicates from resource to be inserted
		List<String> resourceId = new ArrayList<>();
		resourceId = playlistBody.getContentIds();
		resourceId = resourceId.stream().distinct().collect(Collectors.toList());

		// access and status check for contents
		this.checkForAccessAndRetiredStatus(Arrays.asList(userId), resourceId, new HashMap<String, Object>(), false,
				rootOrg, null, null);

		// Generate Random UUID on time basis
		UUID playlistId = UUIDs.timeBased();
		UserPlaylistKey userPlaylistKey = new UserPlaylistKey(rootOrg, userId, playlistId);

		// insert into user playlist table
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		UserPlaylist userPlaylist = new UserPlaylist(userPlaylistKey, timestamp, 0, timestamp,
				playlistBody.getPlayListTitle(), resourceId, null, null, "private");

		// insert into required tables
		userPlayListRepo.insertUserAndRecentPlaylist(userPlaylist,
				this.getRecentObjects(rootOrg, userId, playlistId, resourceId));
	}

	// Insert into RecentPlaylist Table
	public List<PlaylistRecent> getRecentObjects(String rootOrg, String userId, UUID playlistId,
			List<String> resourceIds) {

		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		List<PlaylistRecent> insertPlaylist = new ArrayList<>();
		for (String contentId : resourceIds) {
			PlaylistRecentKey insertKey = new PlaylistRecentKey(rootOrg, userId, playlistId, contentId);
			PlaylistRecent insertRecent = new PlaylistRecent(insertKey, timestamp);
			insertPlaylist.add(insertRecent);
		}
		return insertPlaylist;
	}

	/**
	 * 
	 * @param uID            &nbsp;&nbsp;-&nbsp;&nbsp;UserId for user
	 * @param contentList    &nbsp;&nbsp;-&nbsp;&nbsp;list of resource Ids against
	 *                       which the access/status needs to be checked
	 * @param statusData     &nbsp;&nbsp;-&nbsp;&nbsp;data to be returned to the
	 *                       calling method if required
	 * @param isDataRequired &nbsp;&nbsp;-&nbsp;&nbsp;If true data will be populated
	 *                       in the given map, else exception will be thrown if
	 *                       certain conditions fail
	 * @param rootOrg        &nbsp;&nbsp;-&nbsp;&nbsp;root org
	 * @param status         &nbsp;&nbsp;-&nbsp;&nbsp;If any filter is required on
	 *                       status in meta while fetching
	 * @param source         &nbsp;&nbsp;-&nbsp;&nbsp;Array to limit the fields
	 *                       returned in meta
	 * @throws IOException 
	 * @throws ParseException 
	 * @
	 */
	@SuppressWarnings("unchecked")
	private void checkForAccessAndRetiredStatus(List<String> uID, List<String> contentList,
			Map<String, Object> statusData, boolean isDataRequired, String rootOrg, String status, String[] source) throws IOException, ParseException{

		// Get Meta of the content
		List<Map<String, Object>> metaData = contentService.getMetaByIDListandSource(contentList, source, status);
		if (metaData == null || metaData.isEmpty()) {
			logger.info("invalid.playlistData: metaData not present");
			//throw new InvalidDataInputException("invalid.playlistData");
		}
		if (!isDataRequired) {
			for (Map<String, Object> data : metaData) {
				if (data.get("contentType") != null) {
					if ("Channel".equalsIgnoreCase(data.get("contentType").toString())
							|| "Knowledge Board".equalsIgnoreCase(data.get("contentType").toString())
							|| "Learning Journeys".equalsIgnoreCase(data.get("contentType").toString())) {
						throw new InvalidDataInputException("invalid.content");
					}
				}
			}
		}
		if (metaData != null && !metaData.isEmpty()) {
			final String sbExtHost = props.getSbextServiceHost();
			final String sbExtPort = props.getSbextPort();
			List<Map<String, Object>> allContentResponse = new ArrayList<>();
			Map<String, Object> requestBody = new HashMap<>();
			for (String user : uID) {
				requestBody.put(user, contentList);
			}
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("request", requestBody);
			// Request API to check the access of user for content
			Map<String, Object> accessResponseData = restTemplate.postForObject(
					"http://" + sbExtHost + ":" + sbExtPort + "/accesscontrol/users/contents?rootOrg=" + rootOrg,
					requestMap, Map.class);
			Map<String, Object> result = (Map<String, Object>) accessResponseData.get("result");
			Map<String, Object> userAccessReponse = (Map<String, Object>) result.get("response");

			for (String user : userAccessReponse.keySet()) {

				Map<String, Object> data = new HashMap<>();
				data.put(user, userAccessReponse.get(user));
				allContentResponse.add(data);
			}

			Map<String, Object> contentMeta = new HashMap<>();

			for (Map<String, Object> data : metaData) {
				contentMeta.put(data.get("identifier").toString(), data);
			}

			for (Map<String, Object> eachUserData : allContentResponse) {
				String userId = (String) eachUserData.keySet().toArray()[0];
				Map<String, Object> accessData = (Map<String, Object>) eachUserData.get(userId);
				Map<String, Object> contentData = new HashMap<>();
				for (String content : contentList) {

					Map<String, Object> meta = new HashMap<>();
					boolean hasAccess = false;

					if (accessData.containsKey(content)) {
						hasAccess = (boolean) ((Map<String, Object>) accessData.get(content)).get("hasAccess");
					}

					if (contentMeta.containsKey(content)) {
						meta = (Map<String, Object>) contentMeta.get(content);
						this.extractRatingData(rootOrg, meta);
						if (!isDataRequired && !hasAccess) {
							throw new InvalidDataInputException("content.accessrestricted");
						} else if (!isDataRequired && meta.get("status").toString().equalsIgnoreCase("deleted")) {
							throw new InvalidDataInputException("content.deleted");
						} else if (!isDataRequired && meta.get("status").toString().equalsIgnoreCase("expired")) {
							throw new InvalidDataInputException("content.expired");
						} else if (meta.get("status").toString().equalsIgnoreCase("live")
								|| meta.get("status").toString().equalsIgnoreCase("marked for deletion")) {

							if (meta.containsKey("expiryDate") && meta.get("expiryDate") != null
									&& !meta.get("expiryDate").toString().isEmpty()) {
								SimpleDateFormat formatterDateTime = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");
								Date expiryDate = formatterDateTime.parse(meta.get("expiryDate").toString());
								Date currentDate = new Date();

								if (!isDataRequired && expiryDate.before(currentDate)) {
									throw new InvalidDataInputException("content.expired");
								} else if (isDataRequired && expiryDate.before(currentDate)) {
									meta.put("status", "Expired");
								}
							}
						}
						meta.put("hasAccess", hasAccess);
					} else {
						if (!isDataRequired) {
							throw new InvalidDataInputException("invalid.LexId");
						}
						meta.put("identifier", content);
					}
					if (!meta.containsKey("hasAccess")) {
						meta.put("hasAccess", hasAccess);
					}
					contentData.put(content, meta);
				}
				statusData.put(userId, contentData);
			}
		}
	}

	/*
	 * (non-Javadoc) *
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#sharePlaylist(java.lang.
	 * String, java.lang.String, java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> sharePlaylist(String rootOrg, String userId, String playlistId,
			Map<String, Object> recipientMap) throws Exception {

		Map<String, Object> returnObject = new HashMap<String, Object>();
		if (recipientMap.get("users") == null || ((List<String>) recipientMap.get("users")).isEmpty()) {
			throw new InvalidDataInputException("invalid.userList");
		}

		// Validate user
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		// validate playlist
		List<String> contentList = new ArrayList<>();
		UserPlaylistKey userPlaylistKey = new UserPlaylistKey(rootOrg, userId, UUID.fromString(playlistId));
		Optional<UserPlaylist> playlist = userPlayListRepo.findById(userPlaylistKey);

		if (!playlist.isPresent()) {
			throw new InvalidDataInputException("invalid.playlistId");
		}

		// check for content access and status
		contentList = playlist.get().getResourceIds();
		this.checkForAccessAndRetiredStatus(Arrays.asList(userId), contentList, new HashMap<String, Object>(), false,
				rootOrg, null, null);

		// Validate users to be shared with
		List<String> usersList = (List<String>) recipientMap.get("users");

		// User can not share with himself
		if (usersList.contains(userId)) {
			usersList.remove(userId);
		}

		Map<String, Object> users = this.processUsersToBeSharedWith(rootOrg, usersList, contentList);
		returnObject.put("invalid_users", users.get("invalid_users"));
		returnObject.put("content_access_denied_users", users.get("content_access_denied_users"));

		// insert records into shared playlist table
		String message = null;
		if (recipientMap.get("message") != null) {
			message = recipientMap.get("message").toString();
		}
		this.insertInToSharedPlaylistTable(rootOrg, userId, playlistId, contentList, playlist, users, message);

		return returnObject;
	}

	@SuppressWarnings("unchecked")
	private void insertInToSharedPlaylistTable(String rootOrg, String userId, String playlistId,
			List<String> contentList, Optional<UserPlaylist> playlist, Map<String, Object> users, String message) {

		List<String> validUser = new ArrayList<>();
		validUser = (List<String>) users.get("valid_users");

		// Get value of playlist title and visibility
		UserPlaylist myPlaylist = playlist.get();
		String playlistTitle = myPlaylist.getPlaylistTitle();
		String visibility = myPlaylist.getVisibility();

		List<PlaylistShared> listSharedPlaylist = new ArrayList<>();
		for (String user : validUser) {
			// Check if playlistid is of new playlist or of user who shared playlist
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			PlaylistSharedKey sharedPlaylistKeyInserted = new PlaylistSharedKey(rootOrg, user,
					UUID.fromString(playlistId));
			PlaylistShared sharedPlaylistInserted = new PlaylistShared(sharedPlaylistKeyInserted, timestamp,
					playlistTitle, contentList, userId, timestamp, visibility);
			listSharedPlaylist.add(sharedPlaylistInserted);
		}

		UserPlaylist userPlaylistUpdate = new UserPlaylist();
		userPlaylistUpdate = playlist.get();
		userPlaylistUpdate.setIsShared(1);
		userPlayListRepo.sharePlayList(listSharedPlaylist, userPlaylistUpdate);

		// finally put notification event in kafka
		this.putNotificationEventInKafka(rootOrg, userId, validUser, playlistTitle, message);

	}

	/**
	 * this functions puts notification in kafka
	 * 
	 * @param rootOrg
	 * @param sharedBy
	 * @param sharedWith
	 * @param playListTitle
	 * @param message
	 */
	private void putNotificationEventInKafka(String rootOrg, String sharedBy, List<String> sharedWith,
			String playListTitle, String message) {

		if (message == null || message.isEmpty())
			message = "";

		Map<String, Object> requestBody = new HashMap<>();

		requestBody.put("event-id", "share_playlist");

		Map<String, Object> tagValuePair = new HashMap<>();
		tagValuePair.put("#contentTitle", playListTitle);
		tagValuePair.put("#message", message);
		requestBody.put("tag-value-pair", tagValuePair);

		Map<String, List<String>> recipients = new HashMap<>();
		recipients.put("sharedWith", sharedWith);
		recipients.put("sharedBy", Arrays.asList(sharedBy));
		requestBody.put("recipients", recipients);

		String url = "http://" + this.notifSvcIp + ":" + this.notifSvcPort + "/v1/notification/event";
		HttpHeaders headers = new HttpHeaders();
		headers.set("rootOrg", rootOrg);
		try {
			restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(requestBody, headers), Void.class);
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * checks for valid and invalid users and new users
	 * 
	 * @param rootOrg
	 * @param usersList
	 * @param contentList
	 * @return
	 * @
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> processUsersToBeSharedWith(String rootOrg, List<String> usersList,
			List<String> contentList)  {

		Map<String, Object> returnObject = new HashMap<>();

		// Validate if list of users belong to rootorg
		Map<String, Object> users = new HashMap<String, Object>();

		// validate all the users
		users = userUtilService.validateAndFetchNewUsers(rootOrg, usersList);

		List<String> invalidUsers = new ArrayList<>();
		List<String> validUsers = new ArrayList<>();
		invalidUsers = (List<String>) users.get("invalid_users");
		validUsers = (List<String>) users.get("valid_users");

		// access check for users
		List<String> unAuthorizedUsers = new ArrayList<>();
		Map<String, Object> statusData = new HashMap<>();

		this.checkForAccessStatus(validUsers, contentList, statusData, rootOrg);

		for (String user : validUsers) {
			if (statusData.containsKey(user)) {
				if (!(boolean) statusData.get(user)) {
					unAuthorizedUsers.add(user);
				}
			}
		}
		// remove unauthorized users from valid users list
		validUsers.removeAll(unAuthorizedUsers);

		// add new users who have not yet logged in since their access check has been
		// bypassed
		validUsers.addAll((List<String>) users.get("new_users"));

		// Get user Data of unauthorized user
		List<Object> unAuthorizedUserNamesInsert = new ArrayList<>();
		if (!unAuthorizedUsers.isEmpty()) {
			Map<String, Object> unAuthorizedUserNames = this.getMultipleUserData(rootOrg, unAuthorizedUsers);
			for (String unAuthorizedName : unAuthorizedUsers) {
				if (unAuthorizedUserNames.get(unAuthorizedName) == null) {
					unAuthorizedUserNamesInsert.add(new HashMap<>());
				} else {
					unAuthorizedUserNamesInsert.add(unAuthorizedUserNames.get(unAuthorizedName));
				}
			}
		}
		returnObject.put("content_access_denied_users", unAuthorizedUserNamesInsert);
		returnObject.put("valid_users", validUsers);
		returnObject.put("invalid_users", invalidUsers);
		return returnObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#getUserPlaylists(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getUserPlaylists(String rootOrg, String userId) throws Exception {

		// Validate user
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		// validate playlist
		List<Map<String, Object>> userPlaylist = new ArrayList<>();
		userPlaylist = userPlayListRepo.findByUserPlayListKeyRootOrgAndUserPlayListKeyUserId(rootOrg, userId);
		if (userPlaylist == null || userPlaylist.isEmpty()) {
			return userPlaylist;
		}

		// Sort Collection in last updated order
		Comparator<Map<String, Object>> dateCompare = new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				if (m1.get("last_updated_on") != null && m2.get("last_updated_on") != null) {
					return ((Date) m2.get("last_updated_on")).compareTo((Date) m1.get("last_updated_on"));
				} else {
					return -1;
				}
			}
		};
		Collections.sort(userPlaylist, dateCompare);

		// add user_id to allUserids and replace them with the name of user
		List<String> allUserIds = new ArrayList<>();
		for (Map<String, Object> allUsers : userPlaylist) {
			if (allUsers.containsKey("user_id") && allUsers.get("user_id") != null) {
				allUserIds.add(allUsers.get("user_id").toString());
			}
			if (allUsers.containsKey("shared_by") && allUsers.get("shared_by") != null) {
				allUserIds.add(allUsers.get("shared_by").toString());
			}
		}

		// get the names of the all the users
		this.addUserDataWhereRequired(rootOrg, userPlaylist, allUserIds, new HashMap<String, Object>());

		return userPlaylist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#getDetailedUserPlaylists(
	 * java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getDetailedUserPlaylists(String rootOrg, String userId, List<String> metaFields) throws Exception{

		// Validate user
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		// validate playlist
		List<Map<String, Object>> userPlaylists = new ArrayList<>();
		userPlaylists = userPlayListRepo.findByUserPlayListKeyRootOrgAndUserPlayListKeyUserId(rootOrg, userId);

		if (userPlaylists == null || userPlaylists.isEmpty()) {
			return userPlaylists;
		}

		// Sort Collection in last updated order
		Comparator<Map<String, Object>> dateCompare = new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				if (m1.get("last_updated_on") != null && m2.get("last_updated_on") != null) {
					return ((Date) m2.get("last_updated_on")).compareTo((Date) m1.get("last_updated_on"));
				} else {
					return -1;
				}
			}
		};
		Collections.sort(userPlaylists, dateCompare);

		// Fetch all the resourceids
		List<String> resourceIds = new ArrayList<>();
		List<String> userIds = new ArrayList<>();
		for (Map<String, Object> entry : userPlaylists) {
			resourceIds.addAll((List<String>) entry.get("resource_ids"));

			if (entry.containsKey("user_id") && entry.get("user_id") != null) {
				userIds.add(entry.get("user_id").toString());
			}
			if (entry.containsKey("shared_by") && entry.get("shared_by") != null) {
				userIds.add(entry.get("shared_by").toString());
			}

		}
		// Remove duplicates from resourceids
		resourceIds = resourceIds.stream().distinct().collect(Collectors.toList());

		// check for the source fields and assign it to the required fields
		String[] requiredFields = new String[] { "appIcon", "artifactUrl", "children", "complexityLevel", "contentType",
				"creatorContacts", "description", "downloadUrl", "duration", "identifier", "isExternal",
				"lastUpdatedOn", "learningMode", "learningObjective", "me_totalSessionsCount", "mimeType", "name",
				"resourceCategory", "resourceType", "sourceName", "status", "hasAccess", "averageRating",
				"totalRating" };

		if (metaFields != null && !metaFields.isEmpty()) {

			List<String> fieldsRequiredForProcessing = Arrays.asList(requiredFields);
			fieldsRequiredForProcessing.forEach(field -> {
				if (!metaFields.contains(field))
					metaFields.add(field);
			});

			requiredFields = metaFields.stream().toArray(String[]::new);
		}

		Map<String, Object> statusData = new HashMap<String, Object>();
		this.checkForAccessAndRetiredStatus(Arrays.asList(userId), resourceIds, statusData, true, rootOrg, null,
				requiredFields);

		// Update content meta
		Map<String, Object> contentMeta = new HashMap<>();
		if (!statusData.isEmpty()) {
			contentMeta = (Map<String, Object>) statusData.get(userId);
//			for (String resourceId : resourceIds) {
//				Map<String, Object> contentMap = new HashMap<String, Object>();
//				if (contentMeta.containsKey(resourceId)) {
//					contentMap = (Map<String, Object>) contentMeta.get(resourceId);
////					this.extractRatingData(rootOrg, contentMap);
//				}
//			}
		}

		// add name of user instead of User id
		this.addUserDataWhereRequired(rootOrg, userPlaylists, userIds, contentMeta);
		Collections.sort(userPlaylists, dateCompare);

		return userPlaylists;
	}

	/**
	 * @param rootOrg
	 * @param userPlaylists
	 * @param userIds
	 * @param contentMeta
	 * @
	 */
	@SuppressWarnings("unchecked")
	private void addUserDataWhereRequired(String rootOrg, List<Map<String, Object>> userPlaylists, List<String> userIds,
			Map<String, Object> contentMeta)  {

		// returns the names of all the users in userids
		Map<String, Object> userData = this.getMultipleUserData(rootOrg, userIds);
		for (Map<String, Object> entry : userPlaylists) {

			List<Map<String, Object>> metaData = new ArrayList<>();
			if ((List<String>) entry.get("resource_ids") != null
					&& !((List<String>) entry.get("resource_ids")).isEmpty()) {
				List<String> contentIds = (List<String>) entry.get("resource_ids");
				// insert content_meta in place of resource id
				for (String contentId : contentIds) {
					if (contentMeta.containsKey(contentId)) {
						metaData.add((Map<String, Object>) contentMeta.get(contentId));
					}
				}

				entry.put("content_meta", metaData);
			}

			// insert user name instead of user_id
			if (entry.containsKey("user_id") && entry.get("user_id") != null) {
				entry.put("user", userData.getOrDefault(entry.get("user_id").toString(), new HashMap<>()));
				entry.remove("user_id");
			}

			// insert details of user instead of shared_by
			if (entry.containsKey("shared_by") && entry.get("shared_by") != null) {
				entry.put("shared_by", userData.getOrDefault(entry.get("shared_by").toString(), new HashMap<>()));
			}

			if (entry.containsKey("shared_with") && entry.get("shared_with") != null) {
				entry.put("shared_with", userData.getOrDefault(entry.get("shared_with").toString(), new HashMap<>()));
			}
		}

	}

	// this function will get rating data of a root org and update the contentMap
	/**
	 * @param rootOrg
	 * @param contentMap
	 */
	@SuppressWarnings("unchecked")
	private void extractRatingData(String rootOrg, Map<String, Object> contentMap) {

		if (contentMap.containsKey("averageRating") && contentMap.get("averageRating") != null) {
			Float averageRating = 0f;
			Map<String, Object> mapOfRating = (Map<String, Object>) contentMap.get("averageRating");
			if (!mapOfRating.isEmpty() && mapOfRating.containsKey(rootOrg)) {
				averageRating = Float.parseFloat(mapOfRating.get(rootOrg).toString());
			}
			contentMap.put("averageRating", averageRating);
		}
		if (contentMap.containsKey("totalRating") && contentMap.get("totalRating") != null) {
			int totalRating = 0;
			Map<String, Object> mapOfRating = (Map<String, Object>) contentMap.get("totalRating");
			if (!mapOfRating.isEmpty() && mapOfRating.containsKey(rootOrg)) {
				totalRating = Integer.parseInt(mapOfRating.get(rootOrg).toString());
			}
			contentMap.put("totalRating", totalRating);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#getUserSharedPlaylist(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getUserSharedPlaylist(String rootOrg, String userId) throws Exception {

		// Validate user
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		List<Map<String, Object>> sharedPlaylist = new ArrayList<>();
		sharedPlaylist = sharedPlaylistRepo.findByPlaylistSharedKeyRootOrgAndPlaylistSharedKeySharedWith(rootOrg,
				userId);

		if (sharedPlaylist.isEmpty()) {
			return sharedPlaylist;
		}

		// shared playlist table extract shared_with and shared_by
		List<String> allUserIds = new ArrayList<>();
		for (Map<String, Object> allUsers : sharedPlaylist) {

			if (allUsers.containsKey("shared_with") && allUsers.get("shared_with") != null) {
				allUserIds.add(allUsers.get("shared_with").toString());
			}
			if (allUsers.containsKey("shared_by") && allUsers.get("shared_by") != null) {
				allUserIds.add(allUsers.get("shared_by").toString());
			}
		}

		// get the names of the all the users
		this.addUserDataWhereRequired(rootOrg, sharedPlaylist, allUserIds, new HashMap<String, Object>());

		return sharedPlaylist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#deletePlaylist(java.lang.
	 * String, java.lang.String, java.lang.String)
	 */
	@Override
	public void deletePlaylist(String rootOrg, String userId, String playlistId) throws Exception {

		// Validating User
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		// validate playlist
		UserPlaylistKey playlistKey = new UserPlaylistKey(rootOrg, userId, UUID.fromString(playlistId));

		Optional<UserPlaylist> optionalUserPlaylist = userPlayListRepo.findById(playlistKey);

		if (!optionalUserPlaylist.isPresent()) {
			throw new InvalidDataInputException("invalid.playlist");
		}

		UserPlaylist userPlaylist = new UserPlaylist();
		userPlaylist = optionalUserPlaylist.get();

		List<String> toBeDeleted = new ArrayList<>();
		toBeDeleted = userPlaylist.getResourceIds();

		List<PlaylistRecent> recordsRecent = new ArrayList<>();
		for (String deleted : toBeDeleted) {
			PlaylistRecentKey playlistRecentKey = new PlaylistRecentKey(rootOrg, userId, UUID.fromString(playlistId),
					deleted);

			PlaylistRecent playlistRecent = new PlaylistRecent();
			playlistRecent.setPlaylistRecentkey(playlistRecentKey);
			recordsRecent.add(playlistRecent);
		}

		// update
		userPlayListRepo.deletePlaylist(userPlaylist, recordsRecent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#deleteContent(java.lang.
	 * String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteContent(String rootOrg, String userId, String playlistId, String lexId) throws Exception {

		// Validate user
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		// validate playlist
		UserPlaylistKey playlistKey = new UserPlaylistKey(rootOrg, userId, UUID.fromString(playlistId));
		Optional<UserPlaylist> playlist = userPlayListRepo.findById(playlistKey);
		if (!playlist.isPresent()) {
			throw new InvalidDataInputException("invalid.playlist");
		}

		// resource should be present in playlist
		List<String> resourceId = playlist.get().getResourceIds();
		if (!resourceId.contains(lexId)) {
			throw new InvalidDataInputException("resource.notFound");
		}

		// lexid removed from user_playlist
		resourceId.remove(lexId);

		UserPlaylist userPlaylist = playlist.get();
		userPlaylist.setResourceIds(resourceId);
		Date date = new Date();
		Timestamp lastUpdated = new Timestamp(date.getTime());
		userPlaylist.setLastUpdatedOn(lastUpdated);

		// remove from recent user table
		PlaylistRecentKey recentPlaylistKey = new PlaylistRecentKey(rootOrg, userId, UUID.fromString(playlistId),
				lexId);
		PlaylistRecent recentPlaylist = new PlaylistRecent();
		recentPlaylist.setPlaylistRecentkey(recentPlaylistKey);
		List<PlaylistRecent> listToBeDeleted = new ArrayList<>();
		listToBeDeleted.add(recentPlaylist);

		// deletes the list listToBedeleted and updates user playlist with new object
		// userPlaylist
		userPlayListRepo.deleteContents(userPlaylist, listToBeDeleted);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.lex.playlist.service.PlaylistService#addContents(java.lang.
	 * String, java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public void addContents(String rootOrg, String userId, String playlistId, List<String> contents) throws Exception {

		if (contents == null || contents.isEmpty()) {
			throw new InvalidDataInputException("content.NullOrEmpty");
		}

		// validate user
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		// validate playlist
		UserPlaylistKey playlistKey = new UserPlaylistKey(rootOrg, userId, UUID.fromString(playlistId));
		Optional<UserPlaylist> userPlaylist = userPlayListRepo.findById(playlistKey);
		if (!userPlaylist.isPresent()) {
			throw new InvalidDataInputException("invalid.playlist");
		}

		// Check if lex id is already in playlist
		UserPlaylist playlistToInsert = userPlaylist.get();
		List<String> contentIds = playlistToInsert.getResourceIds();
		for (String id : contents) {
			if (contentIds.contains(id)) {
				throw new InvalidDataInputException("content.alreadyExists");
			}
		}

		// check for distinct contents which is to be added
		contents = contents.stream().distinct().collect(Collectors.toList());
		// access check
		this.checkForAccessAndRetiredStatus(Arrays.asList(userId), contents, new HashMap<String, Object>(), false,
				rootOrg, null, null);

		contentIds.addAll(contents);
		// update playlist
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		playlistToInsert.setLastUpdatedOn(timestamp);

		// save in user playlist as well as recent playlsit using bacth operation
		userPlayListRepo.insertUserAndRecentPlaylist(playlistToInsert,
				this.getRecentObjects(rootOrg, userId, UUID.fromString(playlistId), contents));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#addContent(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addContent(String rootOrg, String userId, String playlistId, String lexId) throws Exception  {

		// validate user
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		// validate playlist
		UserPlaylistKey playlistKey = new UserPlaylistKey(rootOrg, userId, UUID.fromString(playlistId));
		Optional<UserPlaylist> dbPlaylist = userPlayListRepo.findById(playlistKey);
		if (!dbPlaylist.isPresent()) {
			throw new InvalidDataInputException("invalid.playlist");
		}

		// check if the given content is already present
		UserPlaylist userPlaylist = dbPlaylist.get();
		List<String> playlistContent = userPlaylist.getResourceIds();
		if (playlistContent.contains(lexId)) {
			throw new InvalidDataInputException("content.alreadyPresent");
		}

		// Check for access and status

		this.checkForAccessAndRetiredStatus(Arrays.asList(userId), Arrays.asList(lexId), new HashMap<String, Object>(),
				false, rootOrg, null, null);

		playlistContent.add(lexId);
		// update user playlist
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		userPlaylist.setResourceIds(playlistContent);
		userPlaylist.setLastUpdatedOn(timestamp);

		// insert in userplaylist as well as recent playlist using batch operation
		userPlayListRepo.insertUserAndRecentPlaylist(userPlaylist,
				this.getRecentObjects(rootOrg, userId, UUID.fromString(playlistId), Arrays.asList(lexId)));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#updatePlaylist(java.lang.
	 * String, java.lang.String, java.lang.String,
	 * com.infosys.lex.playlist.dto.PlaylistRequest)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updatePlaylist(String rootOrg, String userId, String playlistId, Map<String, Object> playlistBody) throws Exception {

		// check if request data is null or empty
		if (playlistBody.containsKey("playlist_title")) {
			if (playlistBody.get("playlist_title") == null || playlistBody.get("playlist_title").toString().isEmpty())
				throw new InvalidDataInputException("invalid.playlistTitle");
		}

		if (playlistBody.containsKey("content_ids") && playlistBody.get("content_ids") == null) {
			throw new InvalidDataInputException("invalid.contentIds");
		}

		// validate user
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		// validate playlist
		UserPlaylistKey playlisyKey = new UserPlaylistKey(rootOrg, userId, UUID.fromString(playlistId));
		Optional<UserPlaylist> userDbRecord = userPlayListRepo.findById(playlisyKey);
		if (!userDbRecord.isPresent()) {
			throw new InvalidDataInputException("invalid.playlistId");
		}

		UserPlaylist updatedPlaylist = userDbRecord.get();
		Date newDate = new Date();
		Timestamp updateDate = new Timestamp(newDate.getTime());
		// update the title fields in playlist
		if (playlistBody.get("playlist_title") != null) {
			updatedPlaylist.setPlaylistTitle(playlistBody.get("playlist_title").toString());
		}

		// check for status and access for contents
		List<String> contents = updatedPlaylist.getResourceIds();

		if (playlistBody.containsKey("content_ids")) {
			contents = (List<String>) playlistBody.get("content_ids");
		}

		List<PlaylistRecent> recordsToBeInserted = new ArrayList<>();
		List<PlaylistRecent> recordsToBeDeleted = new ArrayList<>();
		if (contents != null) {
			contents = contents.stream().distinct().collect(Collectors.toList());

			List<String> contentAlreadyPresent = new ArrayList<>();
			contentAlreadyPresent = updatedPlaylist.getResourceIds();

			Map<String, Object> contentDetails = new HashMap<String, Object>();
			// find out which content to be added and which to be deleted
			contentDetails = this.getToBeInsertedAndToBeDeletedResources(contentAlreadyPresent, contents);

			List<String> toBeInserted = new ArrayList<>();
			List<String> toBeDeleted = new ArrayList<>();
			toBeInserted = (List<String>) contentDetails.get("toBeInserted");
			toBeDeleted = ((List<String>) contentDetails.get("toBeDeleted"));

			// insert in recent playlist table
			for (String delete : toBeDeleted) {
				PlaylistRecentKey playlistRecentKey = new PlaylistRecentKey(rootOrg, userId,
						UUID.fromString(playlistId), delete);
				PlaylistRecent playlistRecent = new PlaylistRecent();
				playlistRecent.setPlaylistRecentkey(playlistRecentKey);
				recordsToBeDeleted.add(playlistRecent);
			}

			for (String insert : toBeInserted) {
				PlaylistRecentKey playlistRecentKey = new PlaylistRecentKey(rootOrg, userId,
						UUID.fromString(playlistId), insert);
				PlaylistRecent playlistRecent = new PlaylistRecent(playlistRecentKey, updateDate);
				playlistRecent.setPlaylistRecentkey(playlistRecentKey);
				recordsToBeInserted.add(playlistRecent);
			}
			if (!toBeInserted.isEmpty()) {
				this.checkForAccessAndRetiredStatus(Arrays.asList(userId), toBeInserted, new HashMap<String, Object>(),
						false, rootOrg, null, null);
			}

			// set the new resources to playlist
			updatedPlaylist.setResourceIds(contents);
		}

		updatedPlaylist.setLastUpdatedOn(updateDate);
		// save data in user playlist table
		userPlayListRepo.updatePlaylist(updatedPlaylist, recordsToBeInserted, recordsToBeDeleted);
	}

	private Map<String, Object> getToBeInsertedAndToBeDeletedResources(List<String> oldContents,
			List<String> newContents) {

		List<String> commonContent = new ArrayList<>();
		List<String> onlyNewContent = new ArrayList<>();
		List<String> onlyOldContent = new ArrayList<>();

		for (String content : oldContents) {
			if (newContents.contains(content)) {
				commonContent.add(content);
			} else {
				onlyOldContent.add(content);
			}
		}

		for (String content : newContents) {
			if (!oldContents.contains(content)) {
				onlyNewContent.add(content);
			}
		}

		Map<String, Object> returnObject = new HashMap<String, Object>();
		returnObject.put("alreadyPresent", commonContent);
		returnObject.put("toBeDeleted", onlyOldContent);
		returnObject.put("toBeInserted", onlyNewContent);
		return returnObject;

	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getMultipleUserData(String rootOrg, List<String> uuids)  {

		Map<String, Object> result = userUtilService.getUsersDataFromUserIds(rootOrg, uuids,
				new ArrayList<>(Arrays.asList(PIDConstants.FIRST_NAME, PIDConstants.LAST_NAME, PIDConstants.EMAIL)));

		for (String userId : result.keySet()) {
			Map<String, Object> userData = (Map<String, Object>) result.get(userId);

			userData.put("name", this.getUserDisplayName(userData));
			userData.put("userId", userId);
			userData.remove(PIDConstants.FIRST_NAME);
			userData.remove(PIDConstants.LAST_NAME);
			userData.remove(PIDConstants.UUID);

			result.put(userId, userData);
		}

		return result;
	}

	// Name of the user
	private String getUserDisplayName(Map<String, Object> userData) {

		String name = "";
		if (userData.get(PIDConstants.FIRST_NAME) != null
				&& !(userData.get(PIDConstants.FIRST_NAME)).toString().isEmpty()
				&& userData.get(PIDConstants.LAST_NAME) != null
				&& !(userData.get(PIDConstants.LAST_NAME)).toString().isEmpty()) {

			name = userData.get(PIDConstants.FIRST_NAME) + " " + userData.get(PIDConstants.LAST_NAME);

		} else if (userData.get(PIDConstants.FIRST_NAME) != null
				&& !(userData.get(PIDConstants.FIRST_NAME)).toString().isEmpty()
				&& (userData.get(PIDConstants.LAST_NAME) == null
						|| (userData.get(PIDConstants.LAST_NAME)).toString().isEmpty())) {

			name = (userData.get(PIDConstants.FIRST_NAME)).toString();

		} else if ((userData.get(PIDConstants.FIRST_NAME) == null
				|| (userData.get(PIDConstants.FIRST_NAME)).toString().isEmpty())
				&& userData.get(PIDConstants.LAST_NAME) != null
				&& !(userData.get(PIDConstants.LAST_NAME)).toString().isEmpty()) {
			name = (userData.get(PIDConstants.LAST_NAME)).toString();
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#acceptRejectPlaylist(java.
	 * lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void acceptRejectPlaylist(String rootOrg, String userId, String playlistId, String status) throws Exception{

		if (!Arrays.asList("accept", "reject").contains(status)) {
			throw new InvalidDataInputException("invalid.status");
		}

		// Validate User
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		// validate playlist
		PlaylistSharedKey sharedPlaylistKey = new PlaylistSharedKey(rootOrg, userId, UUID.fromString(playlistId));
		Optional<PlaylistShared> sharedPlaylistOptional = sharedPlaylistRepo.findById(sharedPlaylistKey);
		if (!sharedPlaylistOptional.isPresent()) {
			throw new InvalidDataInputException("invalid.playlistId");
		}

		PlaylistShared sharedPlaylist = new PlaylistShared();
		sharedPlaylist = sharedPlaylistOptional.get();
		List<String> contentList = new ArrayList<>();
		contentList = sharedPlaylist.getResourceIds();

		// validate if user has access to content and its status is live
		this.checkForAccessAndRetiredStatus(Arrays.asList(userId), contentList, new HashMap<String, Object>(), false,
				rootOrg, null, null);

		// finally accept or reject
		this.takeAction(rootOrg, userId, playlistId, status, sharedPlaylistKey, sharedPlaylist, contentList);
	}

	private void takeAction(String rootOrg, String userId, String playlistId, String status,
			PlaylistSharedKey sharedPlaylistKey, PlaylistShared sharedPlaylist, List<String> contentList) {

		if (status.equalsIgnoreCase("accept")) {

			String playlistTitle = sharedPlaylist.getPlaylistTitle();
			String sharedBy = sharedPlaylist.getSharedBy();
			String visibility = sharedPlaylist.getVisibility();
			UUID sourcePlaylistId = UUID.fromString(playlistId);

			UUID playlistIdNew = UUIDs.timeBased();
			UserPlaylistKey userPlaylistKey = new UserPlaylistKey(rootOrg, userId, playlistIdNew);
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			UserPlaylist eachUser = new UserPlaylist(userPlaylistKey, timestamp, 0, timestamp, playlistTitle,
					contentList, sharedBy, sourcePlaylistId, visibility);

			userPlayListRepo.acceptPlaylist(eachUser, sharedPlaylist,
					this.getRecentObjects(rootOrg, userId, playlistIdNew, contentList));

		} else {
			// fetch playlist from shared playlist
			sharedPlaylistRepo.deleteById(sharedPlaylistKey);
		}
	}

	@SuppressWarnings("unchecked")
	private void checkForAccessStatus(List<String> uID, List<String> contentList, Map<String, Object> statusData,
			String rootOrg)  {

		final String sbExtHost = props.getSbextServiceHost();
		final String sbExtPort = props.getSbextPort();

		List<Map<String, Object>> allContentResponse = new ArrayList<>();
		Map<String, Object> requestBody = new HashMap<>();
		for (String user : uID) {
			requestBody.put(user, contentList);
		}
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("request", requestBody);
		// check access of user making an API call
		Map<String, Object> accessResponseData = restTemplate.postForObject(
				"http://" + sbExtHost + ":" + sbExtPort + "/accesscontrol/users/contents?rootOrg=" + rootOrg,
				requestMap, Map.class);
		Map<String, Object> result = (Map<String, Object>) accessResponseData.get("result");
		Map<String, Object> userAccessReponse = (Map<String, Object>) result.get("response");

		for (String user : userAccessReponse.keySet()) {
			Map<String, Object> data = new HashMap<>();
			data.put(user, userAccessReponse.get(user));
			allContentResponse.add(data);
		}

		for (Map<String, Object> eachUserData : allContentResponse) {
			String userId = (String) eachUserData.keySet().toArray()[0];
			Map<String, Object> accessData = (Map<String, Object>) eachUserData.get(userId);

			boolean hasAccess = true;
			for (String content : contentList) {
				if (accessData.containsKey(content)) {
					hasAccess = (boolean) ((Map<String, Object>) accessData.get(content)).get("hasAccess");
				} else {
					hasAccess = false;
				}
				if (!hasAccess) {
					break;
				}
			}
			statusData.put(userId, hasAccess);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#getDetailedSharedPlaylist(
	 * java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getDetailedSharedPlaylist(String rootOrg, String userId, List<String> metaFields) throws Exception {

		// validate user
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		List<Map<String, Object>> sharedPlaylist = new ArrayList<>();
		sharedPlaylist = sharedPlaylistRepo.findByPlaylistSharedKeyRootOrgAndPlaylistSharedKeySharedWith(rootOrg,
				userId);

		if (sharedPlaylist.isEmpty()) {
			return sharedPlaylist;
		}

		List<String> resourceIds = new ArrayList<>();
		List<String> userIds = new ArrayList<>();

		// Collect all the users ids and sharedBy in different lists
		for (Map<String, Object> entry : sharedPlaylist) {
			resourceIds.addAll((List<String>) entry.get("resource_ids"));

			if (entry.containsKey("shared_with") && entry.get("shared_with") != null) {
				userIds.add(entry.get("shared_with").toString());
			}
			if (entry.containsKey("shared_by") && entry.get("shared_by") != null) {
				userIds.add(entry.get("shared_by").toString());
			}
		}

		// Remove duplicates from resourceids
		resourceIds = resourceIds.stream().distinct().collect(Collectors.toList());

		// check for the source fields and assign it to the required fields
		String[] requiredFields = new String[] { "appIcon", "artifactUrl", "children", "complexityLevel", "contentType",
				"creatorContacts", "description", "downloadUrl", "duration", "identifier", "isExternal",
				"lastUpdatedOn", "learningMode", "learningObjective", "me_totalSessionsCount", "mimeType", "name",
				"resourceCategory", "resourceType", "sourceName", "status", "hasAccess", "averageRating",
				"totalRating" };

		if (metaFields != null && !metaFields.isEmpty()) {

			List<String> fieldsRequiredForProcessing = Arrays.asList(requiredFields);
			fieldsRequiredForProcessing.forEach(field -> {
				if (!metaFields.contains(field))
					metaFields.add(field);
			});

			requiredFields = metaFields.stream().toArray(String[]::new);
		}
		Map<String, Object> statusData = new HashMap<String, Object>();
		this.checkForAccessAndRetiredStatus(Arrays.asList(userId), resourceIds, statusData, true, rootOrg, null,
				requiredFields);

		Map<String, Object> contentMeta = new HashMap<>();
		if (!statusData.isEmpty()) {
			contentMeta = (Map<String, Object>) statusData.get(userId);
//			for (String resourceId : resourceIds) {
//				Map<String, Object> contentMap = new HashMap<String, Object>();
//				if (contentMeta.containsKey(resourceId)) {
//					contentMap = (Map<String, Object>) contentMeta.get(resourceId);
////					this.extractRatingData(rootOrg, contentMap);
//				}
//			}
		}

//		this.addUserDataWhereRequired(rootOrg, sharedPlaylist, userIds, statusData);
		this.addUserDataWhereRequired(rootOrg, sharedPlaylist, userIds, contentMeta);
		return sharedPlaylist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#getPlaylistContent(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getPlaylistContent(String rootOrg, String userId, Integer size, String page) throws Exception {

		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		int contentCount = 0;

		// list of all the recent contents to be sent
		List<String> recentContents = new ArrayList<>();

		while (contentCount < Integer.valueOf(size) && !page.equals("-1")) {
			// fetch data from db
			Map<String, Object> data = playListRecentRepo.fetchRecentPlaylistContents(rootOrg, userId, page, size);
			List<String> resourceIds = (List<String>) data.get("resourceIds");
			page = data.get("page").toString();
			if (resourceIds.isEmpty()) {
				break;
			}

			recentContents.addAll(resourceIds);
			contentCount = recentContents.size();
		}

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("recentContents", recentContents);
		returnMap.put("nextPage", page);
		returnMap.put("greyOut", env.getProperty("recentPlaylist.greyOut"));
		return returnMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#getDetailedPlaylistContent(
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getDetailedPlaylistContent(String rootOrg, String userId, List<String> sourceFields,
			String isInIntranet, String isStandAlone, List<String> contentTypes, Integer size, String page) throws Exception{

		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		// return a filter map which contains all the filters that are required
		Map<String, Object> filterMap = this.prepareFilterMap(isInIntranet, isStandAlone, contentTypes);

		// meta list
		String[] requiredFields = new String[] { "appIcon", "artifactUrl", "children", "complexityLevel", "contentType",
				"creatorContacts", "description", "downloadUrl", "duration", "identifier", "isExternal",
				"lastUpdatedOn", "learningMode", "learningObjective", "me_totalSessionsCount", "mimeType", "name",
				"resourceCategory", "resourceType", "sourceName", "status", "hasAccess", "averageRating", "totalRating",
				"isInIntranet", "isStandAlone" };

		// check if source fields are required
		if (sourceFields != null && !sourceFields.isEmpty()) {
			List<String> fieldsRequiredForProcessing = Arrays.asList(requiredFields);
			fieldsRequiredForProcessing.forEach(field -> {
				if (!sourceFields.contains(field))
					sourceFields.add(field);
			});
			requiredFields = sourceFields.stream().toArray(String[]::new);
		}
		int contentCount = 0;

		// list of all the recent contents to be sent
		List<Map<String, Object>> recentContents = new ArrayList<>();

		while (contentCount < Integer.valueOf(size) && !page.equals("-1")) {
			// fetch data from db
			Map<String, Object> data = playListRecentRepo.fetchRecentPlaylistContents(rootOrg, userId, page, size);
			List<String> resourceIds = (List<String>) data.get("resourceIds");
			page = data.get("page").toString();
			if (resourceIds.isEmpty()) {
				break;
			}

			Map<String, Object> statusData = new HashMap<>();
			this.checkForAccessAndRetiredStatus(Arrays.asList(userId), resourceIds, statusData, true, rootOrg, null,
					requiredFields);

			recentContents
					.addAll(this.filterContents((Map<String, Object>) statusData.get(userId), filterMap, resourceIds));
			contentCount = recentContents.size();
		}

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("recentContents", recentContents);
		returnMap.put("nextPage", page);
		return returnMap;
	}

	private Map<String, Object> prepareFilterMap(String isInIntranet, String isStandAlone, List<String> contentTypes) {

		Map<String, Object> filters = new HashMap<>();
		if (!"all".equalsIgnoreCase(isInIntranet)) {
			filters.put("isInIntranet", Boolean.parseBoolean(isInIntranet));
		}
		if (!"all".equalsIgnoreCase(isStandAlone)) {
			filters.put("isStandAlone", Boolean.parseBoolean(isStandAlone));
		}

		if (contentTypes != null && !contentTypes.isEmpty()) {
			filters.put("contentType", contentTypes);
		}
		filters.put("status", Arrays.asList("Live", "Marked For Deletion"));

		return filters;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> filterContents(Map<String, Object> meta, Map<String, Object> filterMap,
			List<String> resouceIds) {

		List<Map<String, Object>> contentData = new ArrayList<>();
		Map<String, Object> filteredMeta = new HashMap<>();
		for (String content : meta.keySet()) {
			if (meta.containsKey(content)) {
				boolean toKeep = true;
				Map<String, Object> data = (Map<String, Object>) meta.get(content);
				for (String filter : filterMap.keySet()) {
					if (data.containsKey(filter)) {
						if (filter.equalsIgnoreCase("status") || filter.equalsIgnoreCase("contentType")) {
							if (!((List<String>) filterMap.get(filter)).contains(data.get(filter))) {
								toKeep = false;
								break;
							}
						} else if (filter.equalsIgnoreCase("isStandAlone") || filter.equalsIgnoreCase("isInIntranet")) {
							Boolean filterValue = Boolean.parseBoolean(filterMap.get(filter).toString());
							if (!filterValue.equals(Boolean.parseBoolean(data.get(filter).toString()))) {
								toKeep = false;
								break;
							}
						}
					}
				}
				if (toKeep)
					filteredMeta.put(content, data);
			}
		}

		for (String content : resouceIds) {
			if (filteredMeta.containsKey(content)) {
				contentData.add((Map<String, Object>) filteredMeta.get(content));
			}
		}
		return contentData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#getDetailedPlaylistSyncInfo(
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getDetailedPlaylistSyncInfo(String rootOrg, String userId, String playlistId,
			List<String> metaFields) throws Exception  {

		Map<String, Object> returnObject = new HashMap<String, Object>();
		returnObject = this.getPlaylistSyncInfo(rootOrg, userId, playlistId);

		List<String> resourceIds = new ArrayList<String>();
		List<String> commonContent = new ArrayList<>();
		List<String> onlyUserPlaylistContent = new ArrayList<>();
		List<String> onlySharedByPlaylistContent = new ArrayList<>();

		commonContent = (List<String>) returnObject.get("common_content");
		onlyUserPlaylistContent = (List<String>) returnObject.get("only_user_playlist_content");
		onlySharedByPlaylistContent = (List<String>) returnObject.get("only_sharedby_playlist_content");
		resourceIds.addAll(commonContent);
		resourceIds.addAll(onlyUserPlaylistContent);
		resourceIds.addAll(onlySharedByPlaylistContent);

		// check for the source fields and assign it to the required fields
		String[] requiredFields = new String[] { "appIcon", "artifactUrl", "children", "complexityLevel", "contentType",
				"creatorContacts", "description", "downloadUrl", "duration", "identifier", "isExternal",
				"lastUpdatedOn", "learningMode", "learningObjective", "me_totalSessionsCount", "mimeType", "name",
				"resourceCategory", "resourceType", "sourceName", "status", "hasAccess", "averageRating",
				"totalRating" };

		if (metaFields != null && !metaFields.isEmpty()) {

			List<String> fieldsRequiredForProcessing = Arrays.asList(requiredFields);
			fieldsRequiredForProcessing.forEach(field -> {
				if (!metaFields.contains(field))
					metaFields.add(field);
			});

			requiredFields = metaFields.stream().toArray(String[]::new);
		}
		// access check of users
		Map<String, Object> statusData = new HashMap<String, Object>();
		this.checkForAccessAndRetiredStatus(Arrays.asList(userId), resourceIds, statusData, true, rootOrg, null,
				requiredFields);

//		Map<String, Object> contentMeta = new HashMap<>();
//		if (!statusData.isEmpty()) {
//			contentMeta = (Map<String, Object>) statusData.get(userId);
//			for (String resourceId : resourceIds) {
//				Map<String, Object> contentMap = new HashMap<String, Object>();
//				if (contentMeta.containsKey(resourceId)) {
//					contentMap = (Map<String, Object>) contentMeta.get(resourceId);
////					this.extractRatingData(rootOrg, contentMap);
//				}
//			}
//		}

		// Full data of all the resources
		Map<String, Object> allContentMeta = new HashMap<String, Object>();
		allContentMeta = (Map<String, Object>) statusData.get(userId);

		List<Object> commonContentMeta = new ArrayList<>();
		List<Object> onlyUserPlaylistMeta = new ArrayList<>();
		List<Object> onlySharedWithPlaylistMeta = new ArrayList<>();
		// add meta data to corresponding list
		for (String commonContentData : commonContent) {
			commonContentMeta.add(allContentMeta.get(commonContentData));
		}
		for (String onlyUserMeta : onlyUserPlaylistContent) {
			onlyUserPlaylistMeta.add(allContentMeta.get(onlyUserMeta));
		}
		for (String onlySharedWith : onlySharedByPlaylistContent) {
			onlySharedWithPlaylistMeta.add(allContentMeta.get(onlySharedWith));
		}
		returnObject.put("common_content", commonContentMeta);
		returnObject.put("only_user_playlist_content", onlyUserPlaylistMeta);
		returnObject.put("only_sharedby_playlist_content", onlySharedWithPlaylistMeta);

		return returnObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#getPlaylistSyncInfo(java.
	 * lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> getPlaylistSyncInfo(String rootOrg, String userId, String playlistId) throws Exception{

		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		Map<String, Object> returnObject = new HashMap<String, Object>();

		// fetch data of user playlist
		UserPlaylistKey userPlaylistKey = new UserPlaylistKey(rootOrg, userId, UUID.fromString(playlistId));
		Optional<UserPlaylist> optionalUserPlaylist = userPlayListRepo.findById(userPlaylistKey);

		if (!optionalUserPlaylist.isPresent()) {
			throw new InvalidDataInputException("invalid.playlistId");
		}
		UserPlaylist userPlaylist = new UserPlaylist();
		userPlaylist = optionalUserPlaylist.get();
		List<String> userResources = new ArrayList<>();
		userResources = userPlaylist.getResourceIds();
		String sharedBy = userPlaylist.getSharedBy();
		UUID sourcePlaylistId = userPlaylist.getSourcePlaylistId();

		// fetch data of sharedBy userplaylist
		UserPlaylistKey userPlaylistKeySharedBy = new UserPlaylistKey(rootOrg, sharedBy, sourcePlaylistId);
		Optional<UserPlaylist> optionalUserPlaylistSharedBy = userPlayListRepo.findById(userPlaylistKeySharedBy);

		if (!optionalUserPlaylistSharedBy.isPresent()) {
			throw new InvalidDataInputException("sourcePlaylist.notFound");
		}

		UserPlaylist userPlaylistSharedBy = new UserPlaylist();
		List<String> userPlaylistSharedByResourceIds = new ArrayList<String>();

		userPlaylistSharedBy = optionalUserPlaylistSharedBy.get();
		userPlaylistSharedByResourceIds = userPlaylistSharedBy.getResourceIds();

		// for Access Check for resource ids
		List<String> allResourceIdsForAccessCheck = new ArrayList<String>();
		allResourceIdsForAccessCheck.addAll(userPlaylistSharedByResourceIds);
		allResourceIdsForAccessCheck.addAll(userResources);

		this.checkForAccessAndRetiredStatus(Arrays.asList(userId), allResourceIdsForAccessCheck,
				new HashMap<String, Object>(), false, rootOrg, null, null);

		List<String> commonResource = new ArrayList<String>();
		List<String> onlyUserPlaylistContent = new ArrayList<String>();
		List<String> onlySharedByPlaylistContent = new ArrayList<String>();

		// Filter resource ids which are common and only user have resource
		for (String userResource : userResources) {
			if (userPlaylistSharedByResourceIds.contains(userResource)) {
				commonResource.add(userResource);
			} else {
				onlyUserPlaylistContent.add(userResource);
			}
		}

		for (String userPlaylistSharedByResource : userPlaylistSharedByResourceIds) {
			if (!userResources.contains(userPlaylistSharedByResource)) {
				onlySharedByPlaylistContent.add(userPlaylistSharedByResource);
			}
		}

		// Create Map
		returnObject.put("common_content", commonResource);
		returnObject.put("only_user_playlist_content", onlyUserPlaylistContent);
		returnObject.put("only_sharedby_playlist_content", onlySharedByPlaylistContent);

		return returnObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#getUserPlaylist(java.lang.
	 * String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getUserPlaylist(String rootOrg, String userId, String playlistId,
			List<String> metaFields) throws Exception  {

		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		UserPlaylistKey userPlaylistKey = new UserPlaylistKey(rootOrg, userId, UUID.fromString(playlistId));
		Optional<UserPlaylist> optionalUserPlaylist = userPlayListRepo.findById(userPlaylistKey);
		if (!optionalUserPlaylist.isPresent()) {
			throw new InvalidDataInputException("invalid.playlist");
		}

		UserPlaylist userPlaylist = new UserPlaylist();
		userPlaylist = optionalUserPlaylist.get();
		Map<String, Object> userPlaylistMap = new ObjectMapper().convertValue(userPlaylist, Map.class);
		userPlaylistMap.remove("userPlayListKey");

		List<String> resourceIds = new ArrayList<>();
		resourceIds = userPlaylist.getResourceIds();

		// check for the source fields and assign it to the required fields
		String[] requiredFields = new String[] { "appIcon", "artifactUrl", "children", "complexityLevel", "contentType",
				"creatorContacts", "description", "downloadUrl", "duration", "identifier", "isExternal",
				"lastUpdatedOn", "learningMode", "learningObjective", "me_totalSessionsCount", "mimeType", "name",
				"resourceCategory", "resourceType", "sourceName", "status", "hasAccess", "averageRating",
				"totalRating" };

		if (metaFields != null && !metaFields.isEmpty()) {

			List<String> fieldsRequiredForProcessing = Arrays.asList(requiredFields);
			fieldsRequiredForProcessing.forEach(field -> {
				if (!metaFields.contains(field))
					metaFields.add(field);
			});

			requiredFields = metaFields.stream().toArray(String[]::new);
		}

		Map<String, Object> statusData = new HashMap<String, Object>();
		this.checkForAccessAndRetiredStatus(Arrays.asList(userId), resourceIds, statusData, true, rootOrg, null,
				requiredFields);

		Map<String, Object> contentMap = new HashMap<String, Object>();
		contentMap = (Map<String, Object>) statusData.get(userId);

		List<Map<String, Object>> resourceIdMap = new ArrayList<>();
		if(contentMap !=null){
			for (String resource : resourceIds) {
				resourceIdMap.add((Map<String, Object>) contentMap.get(resource));
			}
		}

		Map<String, Object> listUserIdAndUserName = new HashMap<String, Object>();
		listUserIdAndUserName = this.getMultipleUserData(rootOrg, Arrays.asList(userId));
		userPlaylistMap.put("user", listUserIdAndUserName.get(userId));
		userPlaylistMap.put("resource_ids", resourceIdMap);

		logger.info("userPlaylistMap :"+mapper.writeValueAsString(userPlaylistMap));
		return userPlaylistMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.playlist.service.PlaylistService#deleteMultiple(java.lang.
	 * String, java.lang.String, java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deleteMultiple(String rootOrg, String userId, String playlistId, Map<String, Object> content) throws Exception{

		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		List<String> contents = new ArrayList<>();
		contents = (List<String>) content.get("content");

		if (contents.isEmpty()) {
			throw new InvalidDataInputException("invalid.contents");
		}

		UserPlaylistKey userPlaylistKey = new UserPlaylistKey(rootOrg, userId, UUID.fromString(playlistId));
		Optional<UserPlaylist> optionalUserPlaylist = userPlayListRepo.findById(userPlaylistKey);

		if (!optionalUserPlaylist.isPresent()) {
			throw new InvalidDataInputException("invalid.playlistId");
		}

		UserPlaylist userPlaylist = new UserPlaylist();
		userPlaylist = optionalUserPlaylist.get();
		List<String> userResources = new ArrayList<>();
		userResources = userPlaylist.getResourceIds();

		contents = contents.stream().distinct().collect(Collectors.toList());
		// check if all the contents are present
		for (String resource : contents) {
			if (!userResources.contains(resource)) {
				throw new InvalidDataInputException("invalid.resourceId");
			}
		}

		userResources.removeAll(contents);
		List<PlaylistRecent> toBeDeleted = new ArrayList<>();
		for (String resource : contents) {
			PlaylistRecentKey playlistRecentKey = new PlaylistRecentKey(rootOrg, userId, UUID.fromString(playlistId),
					resource);
			PlaylistRecent playlist = new PlaylistRecent();
			playlist.setPlaylistRecentkey(playlistRecentKey);
			toBeDeleted.add(playlist);
		}
		userPlayListRepo.deleteContents(userPlaylist, toBeDeleted);
	}
}
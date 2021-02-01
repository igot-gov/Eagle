package org.sunbird.common.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.sunbird.common.bodhi.repo.AppConfig;
import org.sunbird.common.bodhi.repo.AppConfigPrimaryKey;
import org.sunbird.common.bodhi.repo.AppConfigRepository;
import org.sunbird.common.constants.JsonKey;
import org.sunbird.common.model.SunbirdApiResp;
import org.sunbird.common.model.SunbirdApiRespContent;
import org.sunbird.common.repo.UserMVModel;
import org.sunbird.common.repo.UserMVRepository;
import org.sunbird.common.repo.UserModel;
import org.sunbird.common.repo.UserRepository;
import org.sunbird.common.util.Constants;
import org.sunbird.common.util.CbExtServerProperties;
import org.sunbird.core.exception.ApplicationLogicError;
import org.sunbird.core.exception.BadRequestException;
import org.sunbird.core.exception.InvalidDataInputException;
import org.sunbird.core.exception.ResourceNotFoundException;
import org.sunbird.userroles.bodhi.repo.UserRolesRepository;
import org.sunbird.userroles.entities.UserRoles;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author akhilesh.kumar05
 *
 */
@Service
public class UserUtilityServiceImpl implements UserUtilityService {

//	private static final long serialVersionUID = -1322913177895512178L;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	UserMVRepository userMVRepo;

	@Autowired
	UserRepository userRepo;

	//@Autowired
	//AppConfigRepository applicationPropertiesRepo;

	@Autowired
	CbExtServerProperties props;

	//@Autowired
	//UserTermsAndConditionsRepository tncRepo;

	@Autowired
	AppConfigRepository appConfigRepo;

	//@Autowired
	//LexServerProperties serverConfig;

	@Autowired
	UserRolesRepository userRoleRepo;

	/*
	 * this method checks whether the list of users with whom goals/playlist is
	 * being shared is a list of valid users. ALWAYS PASS VALUES WITH COMPLETE
	 * DOMAIN eg. genericid@domain.com
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sunbird.core.service.UserUtilityService#verifyUsers(java.util.ArrayList)
	 */

//	@Override
//	public Map<String, Object> verifyUsers(String rootOrg, ArrayList<String> emails) throws Exception {
//		Map<String, Object> ret = new HashMap<>();
//		List<String> valid = new ArrayList<>();
//
//		// check users in cassandra
//		Iterable<UserMVModel> users = userMVRepo.findAllById(emails);
//
//		for (UserMVModel user : users) {
//			valid.add(user.getEmail());
//			emails.remove(user.getEmail());
//		}
//
//		// if the application is configured for graph api then this part checks for
//		// valid users from the active directory
//		if (applicationPropertiesRepo.findById(new AppConfigPrimaryKey(rootOrg, "email_validate_options")).get()
//				.getValue().contains("graph") && emails.size() > 0) {
//			String sbExtHost = props.getSbextServiceHost();
//			String sbExtPort = props.getSbextPort();
//			Map<String, String> usersForGraph = new HashMap<>();
//			for (String email : emails) {
//				if (email.contains("@"))
//					usersForGraph.put(email.substring(0, email.indexOf("@")).toLowerCase(), email);
//				else
//					usersForGraph.put(email.toLowerCase(), email);
//			}
//			HttpHeaders httpHeaders = new HttpHeaders();
//			httpHeaders.set("Content-Type", "application/json");
//			try {
//				String resp = "";
//				Map<String, Object> json = new HashMap<>();
//				Map<String, Object> emailMap = new HashMap<String, Object>();
//				emailMap.put("emails", usersForGraph.keySet());
//				json.put("request", emailMap);
////				json.setRequest(emailMap);
//				resp = restTemplate.postForObject("http://" + sbExtHost + ":" + sbExtPort + "/v1/users/Validate", json,
//						String.class);
//				List<Map<String, Object>> data = new ArrayList<>();
//				if (!(resp.isEmpty() || resp.equals(""))) {
//					ObjectMapper mapper = new ObjectMapper();
//					data = mapper.readValue(resp, new TypeReference<List<Map<String, Object>>>() {
//					});
//				}
//				for (Map<String, Object> userData : data) {
//					String tempId = userData.get("mail").toString()
//							.substring(0, userData.get("mail").toString().indexOf('@')).toLowerCase();
//					valid.add(usersForGraph.get(tempId));
//					emails.remove(usersForGraph.get(tempId));
//				}
//			} catch (Exception e) {
//
//			}
//
//		}
//		ret.put("invalid_users", emails);
//		ret.put("valid_users", valid);
//		return ret;
//	}
//
//	@Override
//	public Map<String, Object> verifyUserUUIDs(String rootOrg, List<String> uuids) throws Exception {
//
////		Map<String, Object> results = this.validateUsersAndRootOrg(rootOrg, uuids);
//		Set<String> validUUIDs = new HashSet<>();
//		Set<String> inValidUUIDs = new HashSet<>();
//
////		inValidUUIDs.addAll((List<String>) results.get("invalid_users"));
////		uuids = (List<String>) results.get("valid_users");
//		if (!uuids.isEmpty()) {
//			List<UserModel> data = userRepo.findAllById(uuids);
//			for (UserModel user : data) {
//				validUUIDs.add(user.getId());
//				uuids.remove(user.getId());
//			}
//		}
//		inValidUUIDs.addAll(uuids);
//		Map<String, Object> ret = new HashMap<>();
//		ret.put("invalid_users", new ArrayList<>(inValidUUIDs));
//		ret.put("valid_users", new ArrayList<>(validUUIDs));
//		return ret;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.sunbird.core.service.UserUtilityService#validateAndFetchUser(java.lang.
//	 * String, java.lang.String)
//	 */
//	@Override
//	public String validateAndFetchUser(String userIdType, String id) throws Exception {
//
//		String userUUID = "";
//
//		// if id is an email id then fetch uuid from the cassandra
//		if (userIdType.toLowerCase().equals("email")) {
//			UserMVModel temp = userMVRepo.findById(id).orElse(null);
//			if (temp == null)
//				throw new InvalidDataInputException("invalid.email", null);
//			userUUID = temp.getId();
//		}
//
//		// if the id is a uuid then check for existence
//		else {
//			userUUID = id;
//			if (userUUID == null || userUUID.isEmpty())
//				throw new InvalidDataInputException("missing.userid", null);
//			UserModel data = userRepo.findById(userUUID).orElse(null);
//			if (data == null)
//				throw new InvalidDataInputException("invalid.userid", null);
//		}
//		return userUUID;
//	}
//
//
//	/*
//	 * This returns emailid for the given uuid
//	 * 
//	 */
//	@Override
//	public String getEmailIdForUUID(String uuid) throws Exception {
//
//		if (uuid == null || uuid.isEmpty())
//			throw new InvalidDataInputException("missing.userid", null);
//		UserModel data = userRepo.findById(uuid).orElse(null);
//		if (data == null)
//			throw new InvalidDataInputException("invalid.userid", null);
//		return data.getEmail();
//	}
//
//	/*
//	 * This method return emailids and the corresponding uuid in the Useremail
//	 * object for valid uuids
//	 * 
//	 */
//
//	@Override
//	public List<Map<String, Object>> getEmailIdsForUUIDs(List<String> uuidList) throws Exception {
//
//		List<Map<String, Object>> emailIdList = userRepo.findEmaildsForids(uuidList);
//		return emailIdList;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sunbird.core.service.UserUtilityService#getUsersFromActiveDirectory(java.
	 * util.ArrayList)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getUsersFromActiveDirectory(List<String> emails){
		List<Map<String, Object>> ret = new ArrayList<>();
		String sbExtHost = props.getSbextServiceHost();
		String sbExtPort = props.getSbextPort();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", "application/json");
		try {
			String resp = "";
			Map<String, Object> json = new HashMap<>();
			Map<String, Object> emailMap = new HashMap<String, Object>();
			emailMap.put("emails", emails);
			json.put("request", emailMap);
//			json.setRequest(emailMap);
			resp = restTemplate.postForObject("http://" + sbExtHost + ":" + sbExtPort + "/v1/Users/Many", json,
					String.class);
			Map<String, Object> responseMap = new HashMap<>();
			if (!(resp.isEmpty() || resp.equals(""))) {
				ObjectMapper mapper = new ObjectMapper();
				responseMap = mapper.readValue(resp, new TypeReference<Map<String, Object>>() {
				});
			}
			ret = (List<Map<String, Object>>) ((Map<String, Object>) responseMap.get("result")).get("response");
		} catch (Exception e) {
		}

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sunbird.common.service.UserUtilityService#getUUIDsFromEmails(java.
	 * util.List)
	 */
	@Override
	public Map<String, Object> getUUIDsFromEmails(List<String> emails) {

		Map<String, Object> uuidMap = new HashMap<>();
		// check users in cassandra
		Iterable<UserMVModel> users = userMVRepo.findAllById(emails);
		for (UserMVModel user : users) {
			uuidMap.put(user.getEmail(), user.getId());
		}
		return uuidMap;
	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.sunbird.common.service.UserUtilityService#getUserDataFromUUID(java.
//	 * util.List)
//	 */
//	@Override
//	public Map<String, Object> getUserDataFromUUID(List<String> uuids) throws Exception {
//
//		Map<String, Object> uuidMap = new HashMap<>();
//		// check users in cassandra
//		Iterable<UserModel> users = userRepo.findAllById(uuids);
//		for (UserModel user : users) {
//			Map<String, Object> data = new HashMap<>();
//			data.put("userId", user.getId());
//			data.put("name", user.getFirstName() + " " + user.getLastName());
//			data.put("email", user.getEmail());
//
//			uuidMap.put(user.getId(), data);
//		}
//		return uuidMap;
//	}
//
//	@Override
//	public boolean validateUserAndRootOrg(String rootOrg, String userId) {
//
//		List<UserTermsAndConditions> data = tncRepo.findUserByRootOrg(rootOrg, userId);
//		if (tncRepo.findUserByRootOrg(rootOrg, userId) != null && !data.isEmpty())
//			return true;
//		return false;
//	}
//
//	@Override
//	public Map<String, Object> validateUsersAndRootOrg(String rootOrg, List<String> userId) {
//
//		List<UserTermsAndConditions> data = tncRepo.findUsersByRootOrg(rootOrg, userId);
//		List<String> validUsers = new ArrayList<>();
//		if (data != null && !data.isEmpty()) {
//			for (UserTermsAndConditions tnc : data) {
//				validUsers.add(tnc.getKey().getUserId());
//				userId.remove(tnc.getKey().getUserId());
//			}
//		}
//		Map<String, Object> ret = new HashMap<>();
//		ret.put("valid_users", validUsers);
//		ret.put("invalid_users", userId);
//		return ret;
//	}

//
//	@Override
	@Override
	public String getUserDataSource(String rootOrg) {
		String dataSource = null;
		AppConfig config = appConfigRepo.findById(new AppConfigPrimaryKey(rootOrg, "user_data_source")).orElse(null);
		if (config != null && (config.getValue() != null && !config.getValue().isEmpty())) {
			dataSource = config.getValue();
		}
		return dataSource;
//		return "PID";
	}

//
	/**
	 * generates map of uuid and email for given uuid list
	 * 
	 * @param uuidList
	 * @return
	 * @throws Exception
	 */
//	@Override
//	public Map<String, Object> getEmailUUIDMapForUUIDs(List<String> uuidList) throws Exception {
//
//		Map<String, Object> uuidEmailMap = new HashMap<String, Object>();
//		// check users in cassandra
//		Iterable<UserModel> users = userRepo.findAllById(uuidList);
//		for (UserModel user : users) {
//			uuidEmailMap.put(user.getId(), user.getEmail());
//		}
//
//		return uuidEmailMap;
//	}

	// ===================================================================> New User
	// Utility With PID Support
	// <==============================================================

	@SuppressWarnings("unchecked")
	@Override
	public boolean validateUser(String rootOrg, String userId,String authorization,
			String xAuthenticatedUserToken) throws ApplicationLogicError {

		String dataSource = getUserDataSource(rootOrg);
		if ("su1".equalsIgnoreCase(dataSource)) {

			UserModel user = userRepo.findById(userId).orElse(null);
			if (user == null) {
				return false;
			} else {
				return user.getId().equals(userId);
			}

		} else {
			/*Map<String, Object> pidRequestMap = new HashMap<String, Object>();
			pidRequestMap.put("source_fields", Arrays.asList("wid", "root_org"));
			Map<String, String> conditions = new HashMap<String, String>();
			conditions.put("root_org", rootOrg);
			conditions.put("wid", userId);
			pidRequestMap.put("conditions", conditions);

			try {
				// commented code due to pid is not required
				//List<Map<String, Object>> pidResponse = restTemplate.postForObject(
					//	"http://" + serverConfig.getPidIp() + ":" + serverConfig.getPidPort().toString() + "/user",
						//pidRequestMap, List.class);
				List<Map<String, Object>> pidResponse = null;
				
				if (pidResponse.isEmpty() || pidResponse == null) {
					return false;
				} else {
					return (pidResponse.get(0).get("wid").equals(userId));
				}

			} catch (Exception e) {
				throw new ApplicationLogicError("PID ERROR: ", e);
			}*/
			
			Map<String, Object> requestMap = new HashMap<String, Object>();
			
			Map<String, Object> request = new HashMap<String, Object>();

			Map<String, String> filters = new HashMap<String, String>();
			//filters.put(Constants.ROOT_ORG, rootOrg);
			filters.put(Constants.USER_ID, userId);
			request.put(Constants.FILTERS, filters);

			requestMap.put("request", request);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", authorization);
			headers.add("X-Authenticated-User-Token", xAuthenticatedUserToken);
			headers.setContentType(MediaType.APPLICATION_JSON);
			try {
				String reqBodyData = new ObjectMapper().writeValueAsString(requestMap);

				HttpEntity<String> requestEnty = new HttpEntity<>(reqBodyData, headers);

				String serverUrl = props.getLearnerUrl() + "api/user/v1/search";

				SunbirdApiResp pidResponse = restTemplate.postForObject(serverUrl, requestEnty, SunbirdApiResp.class);

				if (pidResponse != null && "OK".equalsIgnoreCase(pidResponse.getResponseCode())
						&& pidResponse.getResult().getResponse().getCount() >= 1) {
					//SunbirdApiRespContent content = pidResponse.getResult().getResponse().getContent().get(0);
					return true;
				}else
					return false;

			} catch (Exception e) {
				throw new ApplicationLogicError("PID ERROR: ", e);
			}

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> validateUsers(String rootOrg, List<String> userIds) {

		Map<String, Object> result = new HashMap<>();
		List<String> validUuids = new ArrayList<>();
		String dataSource = getUserDataSource(rootOrg);

		if ("su".equalsIgnoreCase(dataSource)) {

			Iterable<UserModel> users = userRepo.findAllById(userIds);

			for (UserModel user : users) {
				validUuids.add(user.getId());
				userIds.remove(user.getId());
			}
			result.put("valid_users", validUuids);
			result.put("invalid_users", userIds);

		} else {
			Map<String, Object> pidRequestMap = new HashMap<String, Object>();
			pidRequestMap.put("source_fields", Arrays.asList("wid"));
			pidRequestMap.put("values", userIds);

			Map<String, String> conditions = new HashMap<String, String>();
			conditions.put("root_org", rootOrg);

			pidRequestMap.put("conditions", conditions);

			try {
				// commented due to pid is not required
				/*List<Map<String, Object>> pidResponse = restTemplate.postForObject("http://" + serverConfig.getPidIp()
						+ ":" + serverConfig.getPidPort().toString() + "/user/multi-fetch/wid", pidRequestMap,
						List.class);*/
				List<Map<String, Object>> pidResponse = null;

				for (Map<String, Object> map : pidResponse) {
					if (map.get("wid") != null) {
						validUuids.add(map.get("wid").toString());
						userIds.remove(map.get("wid"));
					}
				}

				result.put("valid_users", validUuids);
				result.put("invalid_users", userIds);
			} catch (Exception e) {
				throw new ApplicationLogicError("PID ERROR: ", e);

			}

		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getUsersDataFromUserIds(String rootOrg, List<String> userIds, List<String> source, String authorization,
			String xAuthenticatedUserToken) {

		Map<String, Object> result = new HashMap<>();
		String dataSource = getUserDataSource(rootOrg);
		if ("su1".equalsIgnoreCase(dataSource)) {

			Iterable<UserModel> users = userRepo.findAllById(userIds);

			for (UserModel user : users) {
				Map<String, Object> userData = new HashMap<String, Object>();
				userData.put(Constants.UUID, user.getId());
				userData.put(Constants.FIRST_NAME, user.getFirstName());
				userData.put(Constants.LAST_NAME, user.getLastName());
				userData.put(Constants.EMAIL, user.getEmail());
				result.put(user.getId(), userData);
			}

		} else {

			/*List<String> sources = new ArrayList<>(source);

			if (!sources.contains("wid")) {
				sources.add("wid");
			}
			Map<String, Object> pidRequestMap = new HashMap<String, Object>();
			pidRequestMap.put("source_fields", sources);
			pidRequestMap.put("values", userIds);
			Map<String, String> conditions = new HashMap<String, String>();
			conditions.put("root_org", rootOrg);
			pidRequestMap.put("conditions", conditions);

			try

			{
				// commented due to pid is not required
				
				List<Map<String, Object>> pidResponse = restTemplate.postForObject("http://" + serverConfig.getPidIp()
						+ ":" + serverConfig.getPidPort().toString() + "/user/multi-fetch/wid", pidRequestMap,
						List.class);
				
				List<Map<String, Object>> pidResponse = null;

				for (Map<String, Object> record : pidResponse) {
					if (record.get("wid") != null && userIds.contains(record.get("wid"))) {
						result.put(record.get("wid").toString(), record);
					}
				}

			} catch (Exception e) {
				throw new ApplicationLogicError("PID ERROR: ", e);
			}*/
			
			Map<String, Object> requestMap = new HashMap<String, Object>();
			Map<String, Object> request = new HashMap<String, Object>();

			Map<String, String> filters = new HashMap<String, String>();
			//filters.put(Constants.ROOT_ORG, rootOrg);
			filters.put(Constants.USER_ID, userIds.get(0).toString());
			request.put(Constants.FILTERS, filters);

			requestMap.put("request", request);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", authorization);
			headers.add("X-Authenticated-User-Token", xAuthenticatedUserToken);
			headers.setContentType(MediaType.APPLICATION_JSON);
			try {
				String reqBodyData = new ObjectMapper().writeValueAsString(requestMap);

				HttpEntity<String> requestEnty = new HttpEntity<>(reqBodyData, headers);

				String serverUrl = props.getLearnerUrl() + "api/user/v1/search";

				SunbirdApiResp pidResponse = restTemplate.postForObject(serverUrl, requestEnty, SunbirdApiResp.class);

				if (pidResponse != null && "OK".equalsIgnoreCase(pidResponse.getResponseCode())
						&& pidResponse.getResult().getResponse().getCount() >= 1) {
					SunbirdApiRespContent content = pidResponse.getResult().getResponse().getContent().get(0);
					result.put(userIds.get(0).toString(), content);
					return result;
				}

			} catch (Exception e) {
				throw new ApplicationLogicError("PID ERROR: ", e);
			}

		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getUserDataFromUserId(String rootOrg, String userId, List<String> source) {

		Map<String, Object> result = new HashMap<>();
		String dataSource = getUserDataSource(rootOrg);
		if ("su".equalsIgnoreCase(dataSource)) {

			UserModel user = userRepo.findById(userId).orElse(null);

			if (user != null) {
				Map<String, Object> userData = new HashMap<String, Object>();
				userData.put(Constants.UUID, user.getId());
				userData.put(Constants.FIRST_NAME, user.getFirstName());
				userData.put(Constants.LAST_NAME, user.getLastName());
				userData.put(Constants.EMAIL, user.getEmail());
				result.put(user.getId(), userData);
			}

		} else {

			List<String> sources = new ArrayList<>(source);

			if (!sources.contains("wid")) {
				sources.add("wid");
			}

			Map<String, Object> pidRequestMap = new HashMap<>();
			pidRequestMap.put("source_fields", sources);
			Map<String, String> conditions = new HashMap<String, String>();
			conditions.put("root_org", rootOrg);
			conditions.put("wid", userId);
			pidRequestMap.put("conditions", conditions);

			try {
				// commented due to pid is not required
				/*List<Map<String, Object>> pidResponse = restTemplate.postForObject(
						"http://" + serverConfig.getPidIp() + ":" + serverConfig.getPidPort().toString() + "/user",
						pidRequestMap, List.class);*/
				
				List<Map<String, Object>> pidResponse = null;

				if (!pidResponse.isEmpty()) {
					Map<String, Object> record = pidResponse.get(0);
					if (record.get("wid") != null && record.get("wid").equals(userId)) {
						result.put(record.get("wid").toString(), record);

					}
				}

			} catch (Exception e) {
				throw new ApplicationLogicError("PID ERROR: ", e);
			}

		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getUserEmailsFromUserIds(String rootOrg, List<String> userIds) {

		Map<String, Object> result = new HashMap<>();
		String dataSource = getUserDataSource(rootOrg);
		if ("su".equalsIgnoreCase(dataSource)) {

			Iterable<UserModel> users = userRepo.findAllById(userIds);

			for (UserModel user : users) {
				result.put(user.getId(), user.getEmail());
			}

		} else {
			Map<String, Object> pidRequestMap = new HashMap<String, Object>();

			pidRequestMap.put("source_fields", Arrays.asList("wid", "root_org", "email"));
			pidRequestMap.put("values", userIds);
			Map<String, String> conditions = new HashMap<>();
			conditions.put("root_org", rootOrg);
			pidRequestMap.put("conditions", conditions);
			try {

				// commented due to pid is not required
				/*List<Map<String, Object>> pidResponse = restTemplate.postForObject("http://" + serverConfig.getPidIp()
						+ ":" + serverConfig.getPidPort().toString() + "/user/multi-fetch/wid", pidRequestMap,
						List.class);*/
				
				List<Map<String, Object>> pidResponse = null;

				for (Map<String, Object> record : pidResponse) {
					if (record.get("wid") != null && userIds.contains(record.get("wid"))) {
						result.put(record.get("wid").toString(), record.get("email"));
					}
				}
			} catch (Exception e) {
				throw new ApplicationLogicError("PID ERROR: ", e);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getUserEmailFromUserId(String rootOrg, String userId) {

		String dataSource = getUserDataSource(rootOrg);
		if ("su".equalsIgnoreCase(dataSource)) {

			UserModel user = userRepo.findById(userId).orElse(null);

			if (user != null) {
				return user.getEmail();
			}

		} else {

			Map<String, Object> pidRequestMap = new HashMap<String, Object>();
			pidRequestMap.put("source_fields", Arrays.asList("wid", "root_org", "email"));
			Map<String, Object> conditions = new HashMap<>();
			conditions.put("root_org", rootOrg);
			conditions.put("wid", userId);
			pidRequestMap.put("conditions", conditions);

			try {
				// commented due to pid is not required
				/*List<Map<String, Object>> pidResponse = restTemplate.postForObject(
						"http://" + serverConfig.getPidIp() + ":" + serverConfig.getPidPort().toString() + "/user",
						pidRequestMap, List.class);*/
				
				List<Map<String, Object>> pidResponse = null;
				
				if (pidResponse.isEmpty()) {
					return null;
				} else {
					Map<String, Object> record = pidResponse.get(0);
					if (record.get("wid").equals(userId)) {
						return record.get("email").toString();
					} else {
						return null;
					}
				}

			} catch (Exception e) {
				throw new ApplicationLogicError("PID ERROR: ", e);
			}

		}
		return null;
	}

	/**
	 * Validates if the user is eligible to preview Assessment/exercise in authoring
	 * tool
	 * 
	 * @param rootOrg
	 * @param org
	 * @param userId
	 * @param contentMeta
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean validatePreviewUser(String rootOrg, String org, String userId, Map<String, Object> contentMeta) {

		// verify UserId
		if (!this.validateUser(rootOrg, userId,null,null)) {
			throw new BadRequestException("Invalid User : " + userId);
		}

		// check if admin user
		UserRoles userRole = userRoleRepo.findByUserRolesKeyRootOrgAndUserRolesKeyUserId(rootOrg, userId);
		if (userRole != null) {
			Set<String> roles = userRole.getRoles();
			if (roles.contains("admin"))
				return true;
		}

		// check if reviewer publisher author return if true

		// adds author to preview user list
		if (contentMeta.containsKey("creatorContacts") && contentMeta.get("creatorContacts") != null) {
			Object creatorContacts = contentMeta.get("creatorContacts");
			if (creatorContacts instanceof List) {
				List<?> creatorContactMapList = (List<?>) creatorContacts;
				for (Object creatorDetailsMapObj : creatorContactMapList) {
					if (creatorDetailsMapObj instanceof Map) {
						Map<String, Object> creatorDetailsMap = (Map<String, Object>) creatorDetailsMapObj;
						// checks if user is author
						if (creatorDetailsMap.containsKey("id") && creatorDetailsMap.get("id") != null
								&& creatorDetailsMap.get("id").toString().equals(userId))
							return true;
					} else {
						throw new InvalidDataInputException("Creator Contacts is not list of maps");
					}
				}
			} else {
				throw new InvalidDataInputException("creator contacts is not a list");
			}

		}

		// adds publishers to preview list
		if (contentMeta.containsKey("publisherDetails") && contentMeta.get("publisherDetails") != null) {
			Object creatorContacts = contentMeta.get("publisherDetails");
			if (creatorContacts instanceof List) {
				List<?> creatorContactMapList = (List<?>) creatorContacts;
				for (Object creatorDetailsMapObj : creatorContactMapList) {
					if (creatorDetailsMapObj instanceof Map) {
						Map<String, Object> creatorDetailsMap = (Map<String, Object>) creatorDetailsMapObj;
						if (creatorDetailsMap.containsKey("id") && creatorDetailsMap.get("id") != null
								&& creatorDetailsMap.get("id").toString().equals(userId))
							return true;
					} else {
						throw new InvalidDataInputException("Publisher Details is not list of maps");
					}
				}
			} else {
				throw new InvalidDataInputException("Publisher Details is not a list");
			}

		}

		// adds reviewer(track contacts) to preview Lists
		if (contentMeta.containsKey("trackContacts") && contentMeta.get("trackContacts") != null) {
			Object creatorContacts = contentMeta.get("trackContacts");
			if (creatorContacts instanceof List) {
				List<?> creatorContactMapList = (List<?>) creatorContacts;
				for (Object creatorDetailsMapObj : creatorContactMapList) {
					if (creatorDetailsMapObj instanceof Map) {
						Map<String, Object> creatorDetailsMap = (Map<String, Object>) creatorDetailsMapObj;
						if (creatorDetailsMap.containsKey("id") && creatorDetailsMap.get("id") != null
								&& creatorDetailsMap.get("id").toString().equals(userId))
							return true;
					} else {
						throw new InvalidDataInputException("Track Contacts is not list of maps");
					}
				}
			} else {
				throw new InvalidDataInputException("Track Contacts is not a list");
			}

		}

		return false;
	}

	/**
	 * This method fetches the content meta for the resource. If meta is present for
	 * reviewed/published state then that meta is returned else Live meta is
	 * returned.
	 * 
	 * @param contentId
	 * @param rootOrg
	 * @param org
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@Override
	public Map<String, Object> getContentMeta(String contentId, String rootOrg, String org, String[] fields)
			throws JsonParseException, JsonMappingException, IOException {
		String getcontentIdMetaUrl = "http://" + props.getAuthServiceHost() + ":" + props.getAuthServicePort()
				+ "/action/content/hierarchy/fields/" + contentId;

		HttpHeaders headers = new HttpHeaders();
		headers.add(JsonKey.ROOT_ORG, rootOrg);
		headers.add(JsonKey.ORG, org);

		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("fieldsPassed", true);
		reqMap.put("fields", fields);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(getcontentIdMetaUrl, HttpMethod.POST,
					new HttpEntity<>(reqMap, headers), String.class);

			Map<String, Object> responseMaps = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<Map<String, Object>>() {
					});

			return responseMaps;
		} catch (HttpStatusCodeException ex) {
			throw new ApplicationLogicError("Error fetching content meta from authoring service", ex);
		}
	}

	/**
	 * This method fetches the answer key for exercise
	 * 
	 * @param contentMeta
	 * @return
	 */
	@Override
	public String getAnswerKeyForExerciseAuthoringPreview(Map<String, Object> contentMeta) {
		if (contentMeta.containsKey("artifactUrl") && contentMeta.get("artifactUrl") != null) {
			String artifactUrl = contentMeta.get("artifactUrl").toString();
			try {
				String strCompare = "content-store/";
				int index = artifactUrl.indexOf(strCompare);
				if (index == -1)
					throw new ApplicationLogicError("Invalid assessment key url");
				int startIndexOfLocation = index + strCompare.length();
				String location = artifactUrl.substring(startIndexOfLocation);
				String urlEncodedLocation = location.replaceAll("/", "%2F");
				String contentHost = props.getContentServiceHost();
				String contentPort = props.getBodhiContentPort();

				String fetchUrl = "http://" + contentHost + ":" + contentPort + "/contentv3/download/"
						+ urlEncodedLocation;
				ResponseEntity<String> response = restTemplate.getForEntity(fetchUrl, String.class);

				return response.getBody();

			} catch (HttpStatusCodeException ex) {
				throw new ApplicationLogicError("Error in fetching solution Json!!", ex);
			}
		} else {
			throw new ResourceNotFoundException("Invalid artifact Url");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> validateAndFetchNewUsers(String rootOrg, List<String> userIds) {
		Map<String, Object> result = new HashMap<>();
		List<String> validUuids = new ArrayList<>();
		List<String> newUsers = new ArrayList<>();
		String dataSource = getUserDataSource(rootOrg);

		if ("su".equalsIgnoreCase(dataSource)) {

			Iterable<UserModel> users = userRepo.findAllById(userIds);

			for (UserModel user : users) {
				validUuids.add(user.getId());
				userIds.remove(user.getId());
			}
			result.put("valid_users", validUuids);
			result.put("invalid_users", userIds);

		} else {
			Map<String, Object> pidRequestMap = new HashMap<String, Object>();
			pidRequestMap.put("source_fields", Arrays.asList("wid", "kid"));
			pidRequestMap.put("values", userIds);

			Map<String, String> conditions = new HashMap<String, String>();
			conditions.put("root_org", rootOrg);

			pidRequestMap.put("conditions", conditions);

			try {
				
				// commented due to pid is not required

				/*List<Map<String, Object>> pidResponse = restTemplate.postForObject("http://" + serverConfig.getPidIp()
						+ ":" + serverConfig.getPidPort().toString() + "/user/multi-fetch/wid", pidRequestMap,
						List.class);*/
				
				List<Map<String, Object>> pidResponse = null;

				for (Map<String, Object> map : pidResponse) {
					if (map.get("wid") != null && map.get("kid") != null && !map.get("kid").toString().isEmpty()) {
						validUuids.add(map.get("wid").toString());
						userIds.remove(map.get("wid"));
					} else if (map.get("wid") != null && map.get("kid") == null
							|| map.get("kid").toString().isEmpty()) {
						newUsers.add(map.get("wid").toString());
						userIds.remove(map.get("wid"));
					}
				}

				result.put("valid_users", validUuids);
				result.put("invalid_users", userIds);
				result.put("new_users", newUsers);
			} catch (Exception e) {
				return result;
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> validateAndFetchNewUsersSet(String rootOrg, List<String> userIds) {
		Map<String, Object> result = new HashMap<>();
		Set<String> validUuids = new HashSet<>();
		Set<String> newUsers = new HashSet<>();
		Set<String> invalidUsers = new HashSet<>();
		String dataSource = getUserDataSource(rootOrg);

		if ("su".equalsIgnoreCase(dataSource)) {

			Iterable<UserModel> users = userRepo.findAllById(userIds);

			for (UserModel user : users) {
				validUuids.add(user.getId());
			}
			for (String userId : userIds) {
				if (!validUuids.contains(userId))
					invalidUsers.add(userId);
			}
			result.put("valid_users", validUuids);
			result.put("invalid_users", userIds);

		} else {
			Map<String, Object> pidRequestMap = new HashMap<String, Object>();
			pidRequestMap.put("source_fields", Arrays.asList("wid", "kid"));
			pidRequestMap.put("values", userIds);

			Map<String, String> conditions = new HashMap<String, String>();
			conditions.put("root_org", rootOrg);

			pidRequestMap.put("conditions", conditions);

			try {

				// commented sue to pid is not required
				
				/*List<Map<String, Object>> pidResponse = restTemplate.postForObject("http://" + serverConfig.getPidIp()
						+ ":" + serverConfig.getPidPort().toString() + "/user/multi-fetch/wid", pidRequestMap,
						List.class);*/
				
				List<Map<String, Object>> pidResponse = null;

				for (Map<String, Object> map : pidResponse) {
					if (map.get("wid") != null && map.get("kid") != null && !map.get("kid").toString().isEmpty()) {
						validUuids.add(map.get("wid").toString());
					} else if (map.get("wid") != null && map.get("kid") == null
							|| map.get("kid").toString().isEmpty()) {
						newUsers.add(map.get("wid").toString());
					}
				}

				for (String userId : userIds) {
					if (!validUuids.contains(userId) && !newUsers.contains(userId))
						invalidUsers.add(userId);
				}

				result.put("valid_users", validUuids);
				result.put("invalid_users", invalidUsers);
				result.put("new_users", newUsers);
			} catch (Exception e) {
				throw new ApplicationLogicError("PID ERROR :" + e);
			}
		}
		return result;
	}

	/**
	 * This method fetches the userdetail from PID based on the property values and
	 * based on the conditions provided.
	 * 
	 * @param rootOrg
	 * @param userPropertyName
	 * @param propertyValues
	 * @param sources
	 * @param conditions
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> fetchUsersDataByUserProperty(String rootOrg, String userPropertyName,
			List<String> propertyValues, List<String> sources, Map<String, Object> conditions) {
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> pidRequestMap = new HashMap<String, Object>();
		if (sources == null || sources.isEmpty())
			sources = Arrays.asList("wid");
		pidRequestMap.put("source_fields", sources);
		pidRequestMap.put("values", propertyValues);

		if (conditions == null)
			conditions = new HashMap<>();

		conditions.put("root_org", rootOrg);

		pidRequestMap.put("conditions", conditions);

		try {
			
			//  commented due to pid is not required

			/*List<Map<String, Object>> pidResponse = restTemplate.postForObject("http://" + serverConfig.getPidIp() + ":"
					+ serverConfig.getPidPort().toString() + "/user/multi-fetch/" + userPropertyName, pidRequestMap,
					List.class);*/
			
			List<Map<String, Object>> pidResponse = null;

			result.put("usersDetail", pidResponse);
		} catch (Exception e) {
			throw new ApplicationLogicError("PID ERROR :" + e);
		}
		return result;
	}

	/**
	 * This method fetches the list of users from PID based on the conditions
	 * provided. Return Map with key "userDetail" which will be null if 
	 * list is empty and the userdetailmap if not
	 * 
	 * @param rootOrg
	 * @param conditions
	 * @param sources
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> fetchUserDataByUserProperty(String rootOrg, Map<String, Object> conditions,
			List<String> sources) {
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> pidRequestMap = new HashMap<String, Object>();
		if (sources == null || sources.isEmpty())
			sources = Arrays.asList("wid");
		pidRequestMap.put("source_fields", sources);

		if (conditions == null)
			conditions = new HashMap<>();

		conditions.put("root_org", rootOrg);

		pidRequestMap.put("conditions", conditions);

		try {

			// commented due to pid is not required
			/*List<Map<String, Object>> pidResponse = restTemplate.postForObject(
					"http://" + serverConfig.getPidIp() + ":" + serverConfig.getPidPort().toString() + "/user/",
					pidRequestMap, List.class);*/
			
			List<Map<String, Object>> pidResponse = null;

			if(pidResponse.isEmpty())
				result.put("userDetail", null);
			else
				result.put("userDetail", pidResponse.get(0));
		} catch (Exception e) {
			throw new ApplicationLogicError("PID ERROR :" + e);
		}

		return result;
	}

}
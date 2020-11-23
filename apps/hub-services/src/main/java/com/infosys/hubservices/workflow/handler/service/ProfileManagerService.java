package com.infosys.hubservices.workflow.handler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.hubservices.exception.ApplicationException;
import com.infosys.hubservices.workflow.handler.constants.Constants;
import com.infosys.hubservices.workflow.handler.models.*;
import com.infosys.hubservices.workflow.handler.models.cassandra.ProfileWf;
import com.infosys.hubservices.workflow.handler.postgres.entity.UserProfileWfAudit;
import com.infosys.hubservices.workflow.handler.postgres.entity.UserProfileWfStatus;
import com.infosys.hubservices.workflow.handler.postgres.repo.UserProfileWfAuditRepo;
import com.infosys.hubservices.workflow.handler.postgres.repo.UserProfileWfStatusRepo;
import com.infosys.hubservices.workflow.handler.repository.cassandra.bodhi.ProfileWfRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Service
public class ProfileManagerService {

	@Autowired
	private ProfileWfRepo profileWfRepo;

	@Autowired
	private UserProfileWfStatusRepo userProfileWfStatusRepo;

	@Autowired
	private UserProfileWfAuditRepo userProfileWfAuditRepo;

	@Autowired
	private ObjectMapper mapper;

	/**
	 * Change the status of user profile
	 * 
	 * @param rootOrg
	 * @param org
	 * @param wfRequest
	 * @return Status change response
	 */
	public Response statusChange(String rootOrg, String org, ProfileWfRequest wfRequest) {
		String changeState = null;
		String wfId = wfRequest.getWfId();
		try {
			validateWfRequest(wfRequest);
			UserProfileWfStatus userStatus = userProfileWfStatusRepo.findByRootOrgAndOrgAndUserIdAndWfId(rootOrg, org,
					wfRequest.getUserId(), wfRequest.getWfId());
			ProfileWf workFlow = profileWfRepo.getWorkFlowForService(rootOrg, org, Constants.WF_SERVICE_NAME);
			WorkFlowModel workFlowModel = mapper.readValue(workFlow.getConfiguration(), WorkFlowModel.class);
			WfStatus wfStatus = getWfStatus(wfRequest.getState(), workFlowModel);
			validateUserAndWfStatus(wfRequest, wfStatus, userStatus);
			WfAction wfAction = getWfAction(wfRequest.getAction(), wfStatus);
			
			// TODO get the actor roles and call the validateRoles method to check that
			// actor has proper role to take the workflow action
			
			String nextState = wfAction.getNextState();
			if (ObjectUtils.isEmpty(userStatus)) {
				userStatus = new UserProfileWfStatus();
				wfId = UUID.randomUUID().toString();
				userStatus.setWfId(wfId);
				userStatus.setUserId(wfRequest.getUserId());
				userStatus.setRootOrg(rootOrg);
				userStatus.setOrg(org);
				userStatus.setCreatedOn(new Date());
			}
			changeState = nextState;
			userStatus.setLastUpdatedOn(new Date());
			userStatus.setCurrentStatus(nextState);
			userStatus.setActorUUID(wfRequest.getActorUserId());
			// TODO get user profile and update the status in that
			userProfileWfStatusRepo.save(userStatus);
			// TODO maintain the audit info of user profile workflow steps.
			WfStatus wfStatusCheckForNextState = getWfStatus(changeState, workFlowModel);
			createUserProfileWfAudit(wfRequest, rootOrg, changeState, !wfStatusCheckForNextState.getIsLastState(), wfId);
		} catch (IOException e) {
			throw new ApplicationException(Constants.WORKFLOW_PARSING_ERROR_MESSAGE);
		}
		Response response = new Response();
		response.put(Constants.MESSAGE, Constants.STATUS_CHANGE_MESSAGE + changeState);
		response.put(Constants.STATUS, HttpStatus.OK);
		return response;
	}
	
	/**
	 * Validate profile against workflow state
	 * 
	 * @param wfRequest
	 * @param wfStatus
	 * @param userStatus
	 */
	private void validateUserAndWfStatus(ProfileWfRequest wfRequest, WfStatus wfStatus,
			UserProfileWfStatus userStatus) {

		if (StringUtils.isEmpty(wfRequest.getWfId()) && !wfStatus.getStartState()) {
			throw new ApplicationException(Constants.WORKFLOW_ID_ERROR_MESSAGE);
		}
		if (!ObjectUtils.isEmpty(userStatus)) {
			if (!wfRequest.getState().equalsIgnoreCase(userStatus.getCurrentStatus())) {
				throw new ApplicationException("User profile is in " + userStatus.getCurrentStatus()
						+ " State but trying to be move in " + wfRequest.getState() + " state!");
			}
		}

	}

	/**
	 *
	 * @param nextActions
	 * @return String of next actions
	 */
	private String getNextApplicableActions(List<WfAction> nextActions) {
		String applicableAction = null;
		List<HashMap<String, Object>> nextActionArray = new ArrayList<>();
		try {
			if (CollectionUtils.isEmpty(nextActions)) {
				applicableAction = mapper.writeValueAsString(nextActions);
			} else {
				HashMap<String, Object> actionMap = null;
				for (WfAction action : nextActions) {
					actionMap = new HashMap<>();
					actionMap.put(Constants.ACTION_CONSTANT, action.getAction());
					actionMap.put(Constants.ROLES_CONSTANT, action.getRoles());
					nextActionArray.add(actionMap);
				}
				applicableAction = mapper.writeValueAsString(nextActionArray);
			}
		} catch (IOException e) {
			throw new ApplicationException(Constants.JSON_PARSING_ERROR + e.toString());
		}
		return applicableAction;
	}

	/**
	 * Validate roles of actor with action role
	 *
	 * @param actorRoles
	 * @param actionRoles
	 */
	private void validateRoles(List<String> actorRoles, List<String> actionRoles) {
		if ((CollectionUtils.isEmpty(actionRoles))
				|| (CollectionUtils.isEmpty(actorRoles) && CollectionUtils.isEmpty(actionRoles)))
			return;
		if (CollectionUtils.isEmpty(actorRoles)) {
			throw new ApplicationException(Constants.WORKFLOW_ROLE_ERROR);
		}
		boolean roleFound = actorRoles.stream().anyMatch(role -> actionRoles.contains(role));
		if (!roleFound) {
			throw new ApplicationException(Constants.WORKFLOW_ROLE_CHECK_ERROR);
		}
	}

	/**
	 * Get Workflow Action based on given action
	 *
	 * @param action
	 * @param wfStatus
	 * @return Work flow Action
	 */
	private WfAction getWfAction(String action, WfStatus wfStatus) {
		WfAction wfAction = null;
		if (ObjectUtils.isEmpty(wfStatus.getActions())) {
			throw new ApplicationException(Constants.WORKFLOW_ACTION_ERROR);
		}
		for (WfAction filterAction : wfStatus.getActions()) {
			if (action.equals(filterAction.getAction())) {
				wfAction = filterAction;
			}
		}
		if (ObjectUtils.isEmpty(wfAction)) {
			throw new ApplicationException(Constants.WORKFLOW_ACTION_ERROR);
		}
		return wfAction;
	}

	/**
	 * Get the workflow State based on given state
	 * 
	 * @param state
	 * @param workFlowModel
	 * @return Workflow State
	 */
	private WfStatus getWfStatus(String state, WorkFlowModel workFlowModel) {
		WfStatus wfStatus = null;
		for (WfStatus status : workFlowModel.getWfstates()) {
			if (status.getState().equals(state)) {
				wfStatus = status;
			}
		}
		if (ObjectUtils.isEmpty(wfStatus)) {
			throw new ApplicationException(Constants.WORKFLOW_STATE_CHECK_ERROR);
		}
		return wfStatus;
	}

	/**
	 * Validate the workflow request
	 *
	 * @param wfRequest
	 */
	private void validateWfRequest(ProfileWfRequest wfRequest) {

		if (StringUtils.isEmpty(wfRequest.getState())) {
			throw new ApplicationException(Constants.STATE_VALIDATION_ERROR);
		}

		if (StringUtils.isEmpty(wfRequest.getActorUserId())) {
			throw new ApplicationException(Constants.ACTOR_UUID_VALIDATION_ERROR);
		}

		if (StringUtils.isEmpty(wfRequest.getUserId())) {
			throw new ApplicationException(Constants.USER_UUID_VALIDATION_ERROR);
		}

		if (StringUtils.isEmpty(wfRequest.getAction())) {
			throw new ApplicationException(Constants.ACTION_VALIDATION_ERROR);
		}

		if (CollectionUtils.isEmpty(wfRequest.getUpdateFieldValues())) {
			throw new ApplicationException(Constants.FIELD_VALUE_VALIDATION_ERROR);
		}

	}

	/**
	 * Get the user profile
	 * 
	 * @param rootOrg
	 * @param org
	 * @param userId
	 * @return User profile based on user Id
	 */
	public Response getWfUserProfile(String rootOrg, String org, String wfId, String userId) {
		UserProfileWfStatus userProfile = userProfileWfStatusRepo.findByRootOrgAndOrgAndUserIdAndWfId(rootOrg, org,
				userId, wfId);
		List<UserProfileWfStatus> profileList = userProfile == null ? new ArrayList<>()
				: new ArrayList<>(Arrays.asList(userProfile));
		Response response = new Response();
		response.put(Constants.MESSAGE, Constants.SUCCESSFUL);
		response.put(Constants.DATA, profileList);
		response.put(Constants.STATUS, HttpStatus.OK);
		return response;
	}

	/**
	 * Get the user profiles
	 *
	 * @param rootOrg
	 * @param org
	 * @param status
	 * @return user profiles based on status
	 */
	public Response getWfUserProfileBasedOnStatus(String rootOrg, String org, String status) {
		List<UserProfileWfStatus> userProfiles = userProfileWfStatusRepo.findByRootOrgAndOrgAndCurrentStatus(rootOrg,
				org, status);
		Response response = new Response();
		response.put(Constants.MESSAGE, Constants.SUCCESSFUL);
		response.put(Constants.DATA, userProfiles);
		response.put(Constants.STATUS, HttpStatus.OK);
		return response;
	}

	/**
	 * Save the audit of workflow
	 *
	 * @param wfRequest
	 * @param rootOrg
	 * @param nextStatus
	 * @param workflowStatus
	 * @param wfId
	 */
	public void createUserProfileWfAudit(ProfileWfRequest wfRequest, String rootOrg, String nextStatus,
			boolean workflowStatus, String wfId) {
		try {
			UserProfileWfAudit userProfileWfAudit = new UserProfileWfAudit();
			userProfileWfAudit.setActorUUID(wfRequest.getActorUserId());
			userProfileWfAudit.setComment(wfRequest.getComment());
			userProfileWfAudit.setCreatedOn(new Date());
			userProfileWfAudit.setCurrentStatus(nextStatus);
			userProfileWfAudit.setRootOrg(rootOrg);
			userProfileWfAudit.setUserId(wfRequest.getUserId());
			userProfileWfAudit.setInWorkflow(workflowStatus);
			userProfileWfAudit.setWfId(wfId);
			userProfileWfAudit.setUpdateFieldValues(mapper.writeValueAsString(wfRequest.getUpdateFieldValues()));
			userProfileWfAuditRepo.save(userProfileWfAudit);
		} catch (JsonProcessingException e) {
			throw new ApplicationException(Constants.JSON_PARSING_ERROR + e.toString());
		}

	}

	/**
	 *
	 * @param rootOrg
	 * @param wfId
	 * @param userId
	 * @return
	 */
	public Response getUserProfileWfHistoryOnWfId(String rootOrg, String wfId, String userId) {
		Response response = new Response();
		List<UserProfileWfAudit> userProfileWfAuditList = userProfileWfAuditRepo.findByRootOrgAndUserIdAndWfId(rootOrg,
				userId, wfId);
		response.put(Constants.MESSAGE, Constants.SUCCESSFUL);
		response.put(Constants.DATA, userProfileWfAuditList);
		response.put(Constants.STATUS, HttpStatus.OK);
		return response;
	}

	/**
	 *
	 * @param rootOrg
	 * @param org
	 * @param status
	 * @return
	 */
	public Response getNextActionForState(String rootOrg, String org, String status) {
		Response response = new Response();
		try {
			ProfileWf workFlow = profileWfRepo.getWorkFlowForService(rootOrg, org, Constants.WF_SERVICE_NAME);
			WorkFlowModel workFlowModel = mapper.readValue(workFlow.getConfiguration(), WorkFlowModel.class);
			WfStatus wfStatus = getWfStatus(status, workFlowModel);
			List<HashMap<String, Object>> nextActionArray = new ArrayList<>();
			HashMap<String, Object> actionMap = null;
			if (!CollectionUtils.isEmpty(wfStatus.getActions())) {
				for (WfAction action : wfStatus.getActions()) {
					actionMap = new HashMap<>();
					actionMap.put(Constants.ACTION_CONSTANT, action.getAction());
					actionMap.put(Constants.ROLES_CONSTANT, action.getRoles());
					nextActionArray.add(actionMap);
				}
			}
			response.put(Constants.MESSAGE, Constants.SUCCESSFUL);
			response.put(Constants.DATA, nextActionArray);
			response.put(Constants.STATUS, HttpStatus.OK);
		} catch (IOException e) {
			throw new ApplicationException(Constants.JSON_PARSING_ERROR + e.toString());
		}
		return response;
	}

	/**
	 *
	 * @param rootOrg
	 * @param userId
	 * @return
	 */
	public Response getUserProfileWfHistory(String rootOrg, String userId) {
		Response response = new Response();
		List<UserProfileWfAudit> userProfileWfAuditList = userProfileWfAuditRepo.findByRootOrgAndUserId(rootOrg,
				userId);
		HashMap<String, List<UserProfileWfAudit>> history = new HashMap<>();

		for(UserProfileWfAudit audit : userProfileWfAuditList){
			if(StringUtils.isEmpty(history.get(audit.getWfId()))){
				List<UserProfileWfAudit> userProfileWfAudits = new ArrayList<>(Arrays.asList(audit));
				history.put(audit.getWfId(), userProfileWfAudits);
			}else{
				history.get(audit.getWfId()).add(audit);
			}
		}
		response.put(Constants.MESSAGE, Constants.SUCCESSFUL);
		response.put(Constants.DATA, history);
		response.put(Constants.STATUS, HttpStatus.OK);
		return response;
	}
}

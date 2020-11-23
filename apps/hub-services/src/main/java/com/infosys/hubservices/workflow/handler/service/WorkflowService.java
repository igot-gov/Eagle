package com.infosys.hubservices.workflow.handler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.hubservices.exception.ApplicationException;
import com.infosys.hubservices.workflow.handler.bodhi.WfRepo;
import com.infosys.hubservices.workflow.handler.config.Configuration;
import com.infosys.hubservices.workflow.handler.constants.Constants;
import com.infosys.hubservices.workflow.handler.models.*;
import com.infosys.hubservices.workflow.handler.models.cassandra.Workflow;
import com.infosys.hubservices.workflow.handler.postgres.entity.WfAuditEntity;
import com.infosys.hubservices.workflow.handler.postgres.entity.WfStatusEntity;
import com.infosys.hubservices.workflow.handler.postgres.repo.WfAuditRepo;
import com.infosys.hubservices.workflow.handler.postgres.repo.WfStatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Service
public class WorkflowService {

	@Autowired
	private WfRepo wfRepo;

	@Autowired
	private WfStatusRepo wfStatusRepo;

	@Autowired
	private WfAuditRepo wfAuditRepo;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private Configuration configuration;

	/**
	 * Change the status of workflow application
	 * 
	 * @param rootOrg
	 * @param org
	 * @param wfRequest
	 * @return Status change response
	 */
	public Response statusChange(String rootOrg, String org, WfRequest wfRequest) {
		String changeState = null;
		String wfId = wfRequest.getWfId();
		try {
			validateWfRequest(wfRequest);
			WfStatusEntity applicationStatus = wfStatusRepo.findByRootOrgAndOrgAndApplicationIdAndWfId(rootOrg, org,
					wfRequest.getUserId(), wfRequest.getWfId());
			Workflow workFlow = wfRepo.getWorkFlowForService(rootOrg, org, wfRequest.getServiceName());
			WorkFlowModel workFlowModel = mapper.readValue(workFlow.getConfiguration(), WorkFlowModel.class);
			WfStatus wfStatus = getWfStatus(wfRequest.getState(), workFlowModel);
			validateUserAndWfStatus(wfRequest, wfStatus, applicationStatus);
			WfAction wfAction = getWfAction(wfRequest.getAction(), wfStatus);
			
			// TODO get the actor roles and call the validateRoles method to check that
			// actor has proper role to take the workflow action
			
			String nextState = wfAction.getNextState();
			if (ObjectUtils.isEmpty(applicationStatus)) {
				applicationStatus = new WfStatusEntity();
				wfId = UUID.randomUUID().toString();
				applicationStatus.setWfId(wfId);
				applicationStatus.setServiceName(wfRequest.getServiceName());
				applicationStatus.setUserId(wfRequest.getUserId());
				applicationStatus.setApplicationId(wfRequest.getApplicationId());
				applicationStatus.setRootOrg(rootOrg);
				applicationStatus.setOrg(org);
				applicationStatus.setCreatedOn(new Date());
			}
			changeState = nextState;
			applicationStatus.setLastUpdatedOn(new Date());
			applicationStatus.setCurrentStatus(nextState);
			applicationStatus.setActorUUID(wfRequest.getActorUserId());
			applicationStatus.setUpdateFieldValues(mapper.writeValueAsString(wfRequest.getUpdateFieldValues()));
			wfStatusRepo.save(applicationStatus);

			WfStatus wfStatusCheckForNextState = getWfStatus(changeState, workFlowModel);

			createWfAudit(wfRequest, rootOrg, changeState, !wfStatusCheckForNextState.getIsLastState(), wfId);
		} catch (IOException e) {
			throw new ApplicationException(Constants.WORKFLOW_PARSING_ERROR_MESSAGE);
		}
		Response response = new Response();
		response.put(Constants.MESSAGE, Constants.STATUS_CHANGE_MESSAGE + changeState);
		response.put(Constants.STATUS, HttpStatus.OK);
		return response;
	}
	
	/**
	 * Validate application against workflow state
	 * 
	 * @param wfRequest
	 * @param wfStatus
	 * @param applicationStatus
	 */
	private void validateUserAndWfStatus(WfRequest wfRequest, WfStatus wfStatus,
										 WfStatusEntity applicationStatus) {

		if (StringUtils.isEmpty(wfRequest.getWfId()) && !wfStatus.getStartState()) {
			throw new ApplicationException(Constants.WORKFLOW_ID_ERROR_MESSAGE);
		}
		if (!ObjectUtils.isEmpty(applicationStatus)) {
			if (!wfRequest.getState().equalsIgnoreCase(applicationStatus.getCurrentStatus())) {
				throw new ApplicationException("Application is in " + applicationStatus.getCurrentStatus()
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
	private void validateWfRequest(WfRequest wfRequest) {

		if (StringUtils.isEmpty(wfRequest.getState())) {
			throw new ApplicationException(Constants.STATE_VALIDATION_ERROR);
		}

		if (StringUtils.isEmpty(wfRequest.getApplicationId())) {
			throw new ApplicationException(Constants.APPLICATION_ID_VALIDATION_ERROR);
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

		if (StringUtils.isEmpty(wfRequest.getServiceName())) {
			throw new ApplicationException(Constants.WORKFLOW_SERVICENAME_VALIDATION_ERROR);
		}

	}

	/**
	 *Get the application based on application id
	 *
	 * @param rootOrg
	 * @param org
	 * @param wfId
	 * @param applicationId
	 * @return Wf Application based on Id
	 */
	public Response getWfApplication(String rootOrg, String org, String wfId, String applicationId) {
		WfStatusEntity applicationStatus = wfStatusRepo.findByRootOrgAndOrgAndApplicationIdAndWfId(rootOrg, org,
				applicationId, wfId);
		List<WfStatusEntity> applicationList = applicationStatus == null ? new ArrayList<>()
				: new ArrayList<>(Arrays.asList(applicationStatus));
		Response response = new Response();
		response.put(Constants.MESSAGE, Constants.SUCCESSFUL);
		response.put(Constants.DATA, applicationList);
		response.put(Constants.STATUS, HttpStatus.OK);
		return response;
	}

	/**
	 * Get workflow applications based on status
	 *
	 * @param rootOrg
	 * @param org
	 * @param criteria
	 * @return workflow applications
	 */
	public Response wfApplicationSearch(String rootOrg, String org, SearchCriteria criteria) {
		if (criteria.isEmpty()) {
			throw new ApplicationException(Constants.SEARCH_CRITERIA_VALIDATION);
		}
		Integer limit = configuration.getDefaultLimit();
		Integer offset = configuration.getDefaultOffset();
		if (criteria.getLimit() == null && criteria.getOffset() == null)
			limit = configuration.getMaxLimit();
		if (criteria.getLimit() != null && criteria.getLimit() <= configuration.getDefaultLimit())
			limit = criteria.getLimit();
		if (criteria.getLimit() != null && criteria.getLimit() > configuration.getDefaultOffset())
			limit = configuration.getDefaultLimit();
		if (criteria.getOffset() != null)
			offset = criteria.getOffset();
		Pageable pageable = PageRequest.of(offset, limit + offset);

		Page<WfStatusEntity> statePage = wfStatusRepo.findByRootOrgAndOrgAndServiceNameAndCurrentStatus(rootOrg,
				org, criteria.getServiceName(), criteria.getApplicationStatus(), pageable);
		Response response = new Response();
		response.put(Constants.MESSAGE, Constants.SUCCESSFUL);
		response.put(Constants.DATA, statePage.getContent());
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
	public void createWfAudit(WfRequest wfRequest, String rootOrg, String nextStatus,
							  boolean workflowStatus, String wfId) {
		try {
			WfAuditEntity wfAuditEntity = new WfAuditEntity();
			wfAuditEntity.setActorUUID(wfRequest.getActorUserId());
			wfAuditEntity.setComment(wfRequest.getComment());
			wfAuditEntity.setCreatedOn(new Date());
			wfAuditEntity.setCurrentStatus(nextStatus);
			wfAuditEntity.setRootOrg(rootOrg);
			wfAuditEntity.setUserId(wfRequest.getUserId());
			wfAuditEntity.setInWorkflow(workflowStatus);
			wfAuditEntity.setWfId(wfId);
			wfAuditEntity.setApplicationId(wfRequest.getApplicationId());
			wfAuditEntity.setServiceName(wfRequest.getServiceName());
			wfAuditEntity.setUpdateFieldValues(mapper.writeValueAsString(wfRequest.getUpdateFieldValues()));
			wfAuditRepo.save(wfAuditEntity);
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
	public Response getApplicationHistoryOnWfId(String rootOrg, String wfId, String userId) {
		Response response = new Response();
		List<WfAuditEntity> wfAuditEntityList = wfAuditRepo.findByRootOrgAndApplicationIdAndWfId(rootOrg,
				userId, wfId);
		response.put(Constants.MESSAGE, Constants.SUCCESSFUL);
		response.put(Constants.DATA, wfAuditEntityList);
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
			Workflow workFlow = wfRepo.getWorkFlowForService(rootOrg, org, Constants.WF_SERVICE_NAME);
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
	 * @param applicationId
	 * @return
	 */
	public Response getApplicationWfHistory(String rootOrg, String applicationId) {
		Response response = new Response();
		List<WfAuditEntity> wfAuditEntityList = wfAuditRepo.findByRootOrgAndApplicationId(rootOrg,
				applicationId);
		HashMap<String, List<WfAuditEntity>> history = new HashMap<>();

		for(WfAuditEntity audit : wfAuditEntityList){
			if(StringUtils.isEmpty(history.get(audit.getWfId()))){
				List<WfAuditEntity> wfAuditEntities = new ArrayList<>(Arrays.asList(audit));
				history.put(audit.getWfId(), wfAuditEntities);
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

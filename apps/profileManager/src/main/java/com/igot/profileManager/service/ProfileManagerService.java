package com.igot.profileManager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igot.profileManager.constants.Constants;
import com.igot.profileManager.exception.ApplicationException;
import com.igot.profileManager.models.*;
import com.igot.profileManager.models.cassandra.ProfileWf;
import com.igot.profileManager.postgres.entity.UserProfileWfStatus;
import com.igot.profileManager.postgres.repo.UserProfileWfStatusRepo;
import com.igot.profileManager.repository.cassandra.bodhi.ProfileWfRepo;
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
    private ObjectMapper mapper;

    /**
     *Change the status of user profile
     * @param rootOrg
     * @param org
     * @param wfRequest
     * @return Status change response
     */
    public Response statusChange(String rootOrg, String org, ProfileWfRequest wfRequest) {
        String changeState = null;
        try {
            validateWfRequest(wfRequest);
            UserProfileWfStatus userStatus = userProfileWfStatusRepo.findByRootOrgAndOrgAndUserId(rootOrg, org, wfRequest.getUserId());
            ProfileWf workFlow = profileWfRepo.getWorkFlowForService(rootOrg, org, Constants.WF_SERVICE_NAME);
            WorkFlowModel workFlowModel = mapper.readValue(workFlow.getConfiguration(), WorkFlowModel.class);
            WfStatus wfStatus = getWfStatus(wfRequest.getState(), workFlowModel);
            if (ObjectUtils.isEmpty(userStatus) && !wfStatus.getStartState()) {
                throw new ApplicationException(Constants.WORKFLOW_INITIATE_ERROR_MESSAGE);
            }
            if (!ObjectUtils.isEmpty(userStatus)) {
                if (!wfRequest.getState().equalsIgnoreCase(userStatus.getCurrentStatus())) {
                    throw new ApplicationException("User profile is in " + userStatus.getCurrentStatus() + " State but trying to be move in " + wfRequest.getState() + " state!");
                }
            }
            WfAction wfAction = getWfAction(wfRequest.getAction(), wfStatus);
            //TODO get the actor roles and call the validateRoles method to check that actor has proper role to take the workflow action
            String nextState = wfAction.getNextState();
            List<WfAction> nextActions = getWfStatus(nextState, workFlowModel).getIsLastState() ? Collections.EMPTY_LIST : getWfStatus(nextState, workFlowModel).getActions();
            if (ObjectUtils.isEmpty(userStatus)) {
                userStatus = new UserProfileWfStatus();
                userStatus.setRootOrg(rootOrg);
                userStatus.setOrg(org);
                userStatus.setUserId(wfRequest.getUserId());
                userStatus.setCreatedOn(new Date());
            }
            changeState = nextState;
            userStatus.setLastUpdatedOn(new Date());
            userStatus.setCurrentStatus(nextState);
            userStatus.setActorUUID(wfRequest.getActorUserId());
            userStatus.setNextActions(getNextApplicableActions(nextActions));
            userProfileWfStatusRepo.save(userStatus);
            //TODO get user profile and update the status in that
            // TODO maintain the history of user profile workflow steps.
        } catch (IOException e) {
            throw new ApplicationException(Constants.WORKFLOW_PARSING_ERROR_MESSAGE);
        }
        Response response = new Response();
        response.put(Constants.MESSAGE, Constants.STATUS_CHANGE_MESSAGE + changeState);
        response.put(Constants.STATUS, HttpStatus.OK);
        return response;
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
            }else{
                HashMap<String , Object> actionMap = null;
                for(WfAction action : nextActions){
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
        if ((CollectionUtils.isEmpty(actionRoles)) || (CollectionUtils.isEmpty(actorRoles) && CollectionUtils.isEmpty(actionRoles)))
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
     *Validate the workflow request
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

    }

    /**
     *Get the user profile
     * @param rootOrg
     * @param org
     * @param userId
     * @return User profile based on user Id
     */
    public Response getWfUserProfile(String rootOrg, String org, String userId) {
        UserProfileWfStatus userProfile = userProfileWfStatusRepo.findByRootOrgAndOrgAndUserId(rootOrg, org, userId);
        List<UserProfileWfStatus> profileList = userProfile == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(userProfile));
        Response response = new Response();
        response.put(Constants.MESSAGE, Constants.SUCCESSFUL);
        response.put(Constants.DATA, profileList);
        response.put(Constants.STATUS, HttpStatus.OK);
        return response;
    }

    /**
     *Get the user profiles
     *
     * @param rootOrg
     * @param org
     * @param status
     * @return user profiles based on status
     */
    public Response getWfUserProfileBasedOnStatus(String rootOrg, String org, String status) {
        List<UserProfileWfStatus> userProfiles = userProfileWfStatusRepo.findByRootOrgAndOrgAndCurrentStatus(rootOrg, org, status);
        Response response = new Response();
        response.put(Constants.MESSAGE, Constants.SUCCESSFUL);
        response.put(Constants.DATA, userProfiles);
        response.put(Constants.STATUS, HttpStatus.OK);
        return response;
    }
}

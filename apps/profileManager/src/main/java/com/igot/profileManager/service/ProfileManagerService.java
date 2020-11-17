package com.igot.profileManager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igot.profileManager.constants.Constants;
import com.igot.profileManager.exception.ApplicationException;
import com.igot.profileManager.models.ProfileWfRequest;
import com.igot.profileManager.models.WfAction;
import com.igot.profileManager.models.WfStatus;
import com.igot.profileManager.models.WorkFlowModel;
import com.igot.profileManager.models.cassandra.ProfileWf;
import com.igot.profileManager.models.cassandra.ProfileWfStatus;
import com.igot.profileManager.models.cassandra.ProfileWfStatusPrimarykey;
import com.igot.profileManager.repository.cassandra.bodhi.ProfileWfRepo;
import com.igot.profileManager.repository.cassandra.bodhi.ProfileWfStatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class ProfileManagerService {

    @Autowired
    private ProfileWfRepo profileWfRepo;

    @Autowired
    private ProfileWfStatusRepo profileWfStatusRepo;

    @Autowired
    private ObjectMapper mapper;


    public Boolean statusChange(ProfileWfRequest wfRequest) {
        try {
            validateWfRequest(wfRequest);
            ProfileWfStatus userStatus = profileWfStatusRepo.getUserProfileStatus(Constants.ROOT_ORG, Constants.ORG, wfRequest.getState(), wfRequest.getUserId());
            ProfileWf workFlow = profileWfRepo.getWorkFlowForService(Constants.ROOT_ORG, Constants.ORG, Constants.WF_SERVICE_NAME);
            WorkFlowModel workFlowModel = mapper.readValue(workFlow.getConfiguration(), WorkFlowModel.class);
            WfStatus wfStatus = getWfStatus(wfRequest.getState(), workFlowModel);
//            if (ObjectUtils.isEmpty(userStatus) && !wfStatus.getStartState()) {
//                throw new ApplicationException("Workflow is not initiated!");
//            }
            if (!ObjectUtils.isEmpty(userStatus)) {
                if (!wfRequest.getState().equalsIgnoreCase(userStatus.getProfileWfStatusPrimarykey().getCurrentStatus())) {
                    throw new ApplicationException("User profile is in " + userStatus.getNext_actions() + " State!");
                }
            }
            WfAction wfAction = getWfAction(wfRequest.getAction(), wfStatus);
            //TODO get the actor roles and call the validateRoles method to check that actor has proper role to take the workflow action
            String nextState = wfAction.getNextState();
            List<WfAction> nextActions = getWfStatus(nextState, workFlowModel).getIslastState() ? Collections.EMPTY_LIST : getWfStatus(nextState, workFlowModel).getActions();
            if (ObjectUtils.isEmpty(userStatus)) {
                userStatus = new ProfileWfStatus();
                ProfileWfStatusPrimarykey wfStatusPrimarykey = new ProfileWfStatusPrimarykey(Constants.ROOT_ORG, Constants.ORG, wfRequest.getUserId(), nextState);
                userStatus.setProfileWfStatusPrimarykey(wfStatusPrimarykey);
                userStatus.setStartedOn(new Date());
            }
            userStatus.setLastUpdateOn(new Date());
            userStatus.getProfileWfStatusPrimarykey().setCurrentStatus(nextState);
            userStatus.setActor_uuid(wfRequest.getActorUserId());
            String applicableAction = mapper.writeValueAsString(nextActions);
            userStatus.setNext_actions(applicableAction);
            profileWfStatusRepo.save(userStatus);
            //TODO get user profile and update the status in that
            // TODO maintain the history of user profile workflow steps.
        } catch (IOException e) {
            throw new ApplicationException("Workflow parsing error occurred!");
        }
        return Boolean.TRUE;
    }

    private void validateRoles(List<String> actorRoles, List<String> actionRoles) {
        if ((CollectionUtils.isEmpty(actionRoles)) || (CollectionUtils.isEmpty(actorRoles) && CollectionUtils.isEmpty(actionRoles)))
            return;
        if (CollectionUtils.isEmpty(actorRoles)) {
            throw new ApplicationException("No role found for the actor!");
        }
        boolean roleFound = actorRoles.stream().anyMatch(role -> actionRoles.contains(role));
        if (!roleFound) {
            throw new ApplicationException("No proper role found for the actor!");
        }
    }

    private WfAction getWfAction(String action, WfStatus wfStatus) {
        WfAction wfAction = null;
        if (wfStatus.getIslastState()) {
            return wfAction;
        }
        for (WfAction filterAction : wfStatus.getActions()) {
            if (action.equals(filterAction.getAction())) {
                wfAction = filterAction;
            }
        }
        if (ObjectUtils.isEmpty(wfAction)) {
            throw new ApplicationException("No action found on given action!");
        }
        return wfAction;
    }

    private WfStatus getWfStatus(String state, WorkFlowModel workFlowModel) {
        WfStatus wfStatus = null;
        for (WfStatus status : workFlowModel.getWfstates()) {
            if (status.getState().equals(state)) {
                wfStatus = status;
            }
        }
        if (ObjectUtils.isEmpty(wfStatus)) {
            throw new ApplicationException("No wf state found on given state!");
        }
        return wfStatus;
    }

    private void validateWfRequest(ProfileWfRequest wfRequest) {

        if (StringUtils.isEmpty(wfRequest.getState())) {
            throw new ApplicationException("Work flow state can not be empty!");
        }

        if (StringUtils.isEmpty(wfRequest.getActorUserId())) {
            throw new ApplicationException("actor uuid can not be empty!");
        }

        if (StringUtils.isEmpty(wfRequest.getUserId())) {
            throw new ApplicationException("user uuid can not be empty!");
        }

        if (StringUtils.isEmpty(wfRequest.getAction())) {
            throw new ApplicationException("Work flow action can not be empty!");
        }

    }
}

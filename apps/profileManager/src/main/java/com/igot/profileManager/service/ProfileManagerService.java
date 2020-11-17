package com.igot.profileManager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igot.profileManager.constants.Constants;
import com.igot.profileManager.exception.ApplicationException;
import com.igot.profileManager.models.ProfileWfRequest;
import com.igot.profileManager.models.WfAction;
import com.igot.profileManager.models.WfStatus;
import com.igot.profileManager.models.WorkFlowModel;
import com.igot.profileManager.models.cassandra.ProfileWf;
import com.igot.profileManager.repository.cassandra.bodhi.ProfileWfRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class ProfileManagerService {

    @Autowired
    private ProfileWfRepo profileWfRepo;

    @Autowired
    private ObjectMapper mapper;


    public Boolean statusChange(ProfileWfRequest wfRequest) {
        try {
            validateWfRequest(wfRequest);
            ProfileWf workFlow = profileWfRepo.getWorkFlowForService(Constants.ROOT_ORG, Constants.ORG, Constants.WF_SERVICE_NAME);
            WorkFlowModel workFlowModel = mapper.readValue(workFlow.getConfiguration(), WorkFlowModel.class);
            WfStatus wfStatus = getWfStatus(wfRequest.getState(), workFlowModel);
            WfAction wfAction = getWfAction(wfRequest.getAction(), wfStatus);
            String nextState = wfAction.getNextState();
            List<WfAction> nextActions = getWfStatus(nextState, workFlowModel).getIslastState() ? Collections.EMPTY_LIST : getWfStatus(nextState, workFlowModel).getActions();
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

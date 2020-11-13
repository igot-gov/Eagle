package com.igot.profileManager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igot.profileManager.constants.Constants;
import com.igot.profileManager.models.ProfileWfRequest;
import com.igot.profileManager.models.WorkFlowModel;
import com.igot.profileManager.models.cassandra.ProfileWf;
import com.igot.profileManager.repository.cassandra.bodhi.ProfileWfRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ProfileManagerService {

    @Autowired
    private ProfileWfRepo profileWfRepo;


    public Boolean statusChange(ProfileWfRequest wfRequest) {
        ObjectMapper mapper = new ObjectMapper();
        ProfileWf workFlow = profileWfRepo.getWorkFlowForService(Constants.ROOT_ORG, Constants.ORG, Constants.WF_SERVICE_NAME);
        try {
            WorkFlowModel workFlowModel = mapper.readValue(workFlow.getConfiguration(), WorkFlowModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Boolean.TRUE;
    }
}

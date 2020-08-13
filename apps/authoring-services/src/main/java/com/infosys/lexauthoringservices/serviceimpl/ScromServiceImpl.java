/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.serviceimpl;

import com.infosys.lexauthoringservices.exception.BadRequestException;
import com.infosys.lexauthoringservices.model.Response;
import com.infosys.lexauthoringservices.model.ScromRequest;
import com.infosys.lexauthoringservices.model.cassandra.ScromModel;
import com.infosys.lexauthoringservices.model.cassandra.ScromPrimaryKey;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.ScromModelRepository;
import com.infosys.lexauthoringservices.service.ScromService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ScromServiceImpl implements ScromService {

    private Logger logger = LoggerFactory.getLogger(ScromServiceImpl.class);

    @Autowired
    ScromModelRepository scromModelRepository;

    @Override
    public Response upsert(ScromRequest scromData, String rootOrg, String org) throws Exception{

        Response response = new Response();

        try{

            ScromPrimaryKey scromPrimaryKey = new ScromPrimaryKey(rootOrg, org, scromData.getContentId(), scromData.getUserId());
            ScromModel scromModel = new ScromModel(scromPrimaryKey);
            scromModel.setInitialized(scromData.isInitialized());
            scromModel.setType(scromData.getType());
            scromModel.setCmiCoreExit(scromData.getCmiCoreExit());
            scromModel.setCmiCoreLessonStatus(scromData.getCmiCoreLessonStatus());
            scromModel.setCmiCoreSessionTime(scromData.getCmiCoreSessionTime());
            scromModel.setCmiSuspendData(scromData.getCmiSuspendData());
            scromModel.setErrors(scromData.getErrors());

            scromModelRepository.save(scromModel);

        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e);
        }

        response.put("Message", "Successfully upserted data");

        return response;
    }

    @Override
    public Response fetch(Map<String, Object> scromData) {

        String rootOrg = (String) scromData.get("rootOrg");
        String org = (String) scromData.get("org");
        String contentId = (String) scromData.get("contentId");
        String userId = (String) scromData.get("userId");

        if(rootOrg == null || rootOrg.isEmpty() || org == null || org.isEmpty() || contentId == null || contentId.isEmpty()
                || userId == null || userId.isEmpty() ) {

            throw new BadRequestException("Invalid request: rootOrg or org or contentId or userId is missing ");
        }

        ScromPrimaryKey scromPrimaryKey = new ScromPrimaryKey(rootOrg, org, contentId, userId);
        scromModelRepository.findById(scromPrimaryKey);

        return null;
    }

    @Override
    public Response delete(Map<String, Object> scromData) {
        return null;
    }
}

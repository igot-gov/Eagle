/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.model.Response;
import com.infosys.lexauthoringservices.model.ScromData;
import com.infosys.lexauthoringservices.model.cassandra.ScromModel;
import com.infosys.lexauthoringservices.model.cassandra.ScromPrimaryKey;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.ScromModelRepository;
import com.infosys.lexauthoringservices.service.ScromService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ScromServiceImpl implements ScromService {

    private Logger logger = LoggerFactory.getLogger(ScromServiceImpl.class);

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ScromModelRepository scromModelRepository;

    @Override
    public Response upsert(ScromData scromData, String rootOrg, String org) throws Exception{

        Response response = new Response();

        try{
            logger.info("scrom request: {}", mapper.writeValueAsString(scromData));

            ScromPrimaryKey scromPrimaryKey = new ScromPrimaryKey(rootOrg, org, scromData.getContentId(), scromData.getUserId());
            ScromModel scromModel = new ScromModel(scromPrimaryKey);
            scromModel.setInitialized(scromData.isInitialized());
            scromModel.setType(scromData.getType());
            scromModel.setCmiCoreExit(scromData.getCmiCoreExit());
            scromModel.setCmiCoreLessonStatus(scromData.getCmiCoreLessonStatus());
            scromModel.setCmiCoreSessionTime(scromData.getCmiCoreSessionTime());
            scromModel.setCmiSuspendData(scromData.getCmiSuspendData());

            scromModelRepository.save(scromModel);

        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e);
        }

        response.put("Message", "Successfully upserted data");

        return response;
    }

    @Override
    public Response fetch(String rootOrg, String org, String contentId, String userId) throws Exception {

        Response response = new Response();
        try{

            ScromPrimaryKey scromPrimaryKey = new ScromPrimaryKey(rootOrg, org, contentId, userId);
            Optional<ScromModel> scromModel =scromModelRepository.findById(scromPrimaryKey);
            logger.info("scrom model: {}", mapper.writeValueAsString(scromModel));

            scromModel.get().setScromPrimaryKey(scromPrimaryKey);
            ScromModel  model = scromModel.get();

            ScromData scromData = new ScromData();
            scromData.setUserId(model.getUserId());
            scromData.setContentId(model.getContentId());
            scromData.setType(model.getType());
            scromData.setInitialized(model.isInitialized());
            scromData.setCmiCoreExit(model.getCmiCoreExit());
            scromData.setCmiCoreLessonStatus(model.getCmiCoreLessonStatus());
            scromData.setCmiCoreSessionTime(model.getCmiCoreSessionTime());
            scromData.setCmiSuspendData(model.getCmiSuspendData());

            //response.put("data", scromModel.get());
            response.put("data", scromData);

        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e);
        }
        response.put("Message", "Successful");
        return response;
    }

    @Override
    public Response delete( String rootOrg, String org,  String contentId, String userId) throws Exception {
        Response response = new Response();
        try{

            ScromPrimaryKey scromPrimaryKey = new ScromPrimaryKey(rootOrg, org, contentId, userId);
            scromModelRepository.deleteById(scromPrimaryKey);

        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e);
        }
        response.put("Message", "Successful");
        return response;
    }
}

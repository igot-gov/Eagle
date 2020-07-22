/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.recommendationservice.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.recommendationservice.exception.ApplicationServiceError;
import com.infosys.recommendationservice.exception.BadRequestException;
import com.infosys.recommendationservice.model.cassandra.UserPositionCompetency;
import com.infosys.recommendationservice.model.cassandra.UserPositionCompetencyPrimarykey;
import com.infosys.recommendationservice.repository.cassandra.bodhi.UserPositionCompetencyRepository;
import com.infosys.recommendationservice.util.ComputeCompetency;
import com.infosys.recommendationservice.model.CompetencyRequest;
import com.infosys.recommendationservice.model.Response;
import com.infosys.recommendationservice.model.UserCompetencyRequest;
import com.infosys.recommendationservice.repository.cassandra.bodhi.CompetencyRepository;
import com.infosys.recommendationservice.service.UserCompentancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserCompetencyServiceImpl implements UserCompentancyService {

    @Autowired
    CompetencyRepository competencyRepository;
    //ComputeCompetency computeCompetency;

    @Autowired
    UserPositionCompetencyRepository userPositionCompetencyRepository;

    @Value("${competency.computation.enabled}")
    private boolean toCompute;

    private final ComputeCompetency computeCompetency = new ComputeCompetency(competencyRepository);


    public Response upsert(UserCompetencyRequest userCompetencyRequest, String rootOrg, String org) throws Exception{
        Response response = new Response();
        ObjectMapper mapper = new ObjectMapper();

        try {

            String userRole = userCompetencyRequest.getUserRole();
            String userId = userCompetencyRequest.getUserId();
            //TODO userId and mandatory params validations
            if(userId==null || userId.isEmpty() || userRole==null || userId.isEmpty() || userCompetencyRequest.getCompetencyRequests().size()==0){
                throw new BadRequestException("Invalid request: userId, userrole and competency cannot be empty");
            }

            UserPositionCompetencyPrimarykey pk = new UserPositionCompetencyPrimarykey(rootOrg, org, userId, userRole);

            //TODO enhance efficiency
            for(CompetencyRequest cq : userCompetencyRequest.getCompetencyRequests()){

                UserPositionCompetency userPositionCompetency = new UserPositionCompetency(pk, cq.getCompetency(), cq.getLevel());
                computeCompetency.compute(userPositionCompetency);
                userPositionCompetencyRepository.save(userPositionCompetency);
            }

        } catch (Exception e){
            e.printStackTrace();
            throw new ApplicationServiceError("Exception message: "+e.getMessage());
        }

        return response;
    }
    public Response delete(UserCompetencyRequest userCompetencyRequest, String rootOrg, String org){
        return null;
    }
}

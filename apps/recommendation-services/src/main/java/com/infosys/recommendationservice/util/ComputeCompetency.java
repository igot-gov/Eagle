/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.recommendationservice.util;

import com.infosys.recommendationservice.model.cassandra.Competency;
import com.infosys.recommendationservice.model.cassandra.CompetencyPrimarykey;
import com.infosys.recommendationservice.model.cassandra.UserCompetency;
import com.infosys.recommendationservice.repository.cassandra.bodhi.CompetencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;


public class ComputeCompetency {

    CompetencyRepository competencyRepository;

    public ComputeCompetency(CompetencyRepository competencyRepository){
        this.competencyRepository = competencyRepository;
    }

    public void compute(UserCompetency userCompetency){

        int userLevel = userCompetency.getLevel();
        CompetencyPrimarykey pk = new CompetencyPrimarykey(userCompetency.getUserCompetencyPrimarykey().getRootOrg(),userCompetency.getUserCompetencyPrimarykey().getOrg(),userCompetency.getCompetency());
        Optional<Competency> competency = competencyRepository.findById(pk);
        int expectedLevel = competency.get().getLevel();
        double diff = Double.compare(expectedLevel, userLevel);

        boolean isCompetant = (diff<0.0 || diff == 0.0) ? true : false;
        userCompetency.setDelta(diff);
        userCompetency.setCompetent(isCompetant);

    }


}

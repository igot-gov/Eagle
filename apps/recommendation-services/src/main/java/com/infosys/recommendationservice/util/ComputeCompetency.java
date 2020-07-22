/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.recommendationservice.util;

import com.infosys.recommendationservice.model.cassandra.*;
import com.infosys.recommendationservice.repository.cassandra.bodhi.CompetencyRepository;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ComputeCompetency {

    CompetencyRepository competencyRepository;

    public ComputeCompetency(CompetencyRepository competencyRepository){
        this.competencyRepository = competencyRepository;
    }

    public void compute(UserPositionCompetency userPositionCompetency){

        List<Integer> userLevel = userPositionCompetency.getUserLevel();
        PositionCompetencyPrimarykey pk = new PositionCompetencyPrimarykey(userPositionCompetency.getUserPositionCompetencyPrimarykey().getRootOrg(),userPositionCompetency.getUserPositionCompetencyPrimarykey().getOrg(),userPositionCompetency.getUserPositionCompetencyPrimarykey().getUserRole(),userPositionCompetency.getUserCompetency());
        Optional<PositionCompetency> pcompetency = competencyRepository.findById(pk);
        List<Integer> expectedLevel = pcompetency.get().getLevel();

        List<Integer> diff = new ArrayList<>(CollectionUtils.subtract(expectedLevel, userLevel));
        userPositionCompetency.setDelta(diff);

    }

}

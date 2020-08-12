/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.scoringengine.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.scoringengine.schema.model.ScoringSchema;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
public class ScoringSchemaLoader {


    private ScoringSchema scoringSchema;

    @PostConstruct
    public void load() throws Exception{

        File file = new ClassPathResource("ScoringSchema.json").getFile();
        //System.out.println("file "+file);//File file = new File(resource.getPath());

        ObjectMapper objectMapper = new ObjectMapper();
        this.scoringSchema = objectMapper.readValue(file, ScoringSchema.class);
        //System.out.println("scoringSchema "+objectMapper.writeValueAsString(scoringSchema));//File file = new File(resource.getPath());
    }

    public ScoringSchema getScoringSchema(){
        return scoringSchema;
    }


}

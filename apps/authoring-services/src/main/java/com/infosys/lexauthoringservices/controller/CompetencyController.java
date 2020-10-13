package com.infosys.lexauthoringservices.controller;

import com.infosys.lexauthoringservices.model.Competency;
import com.infosys.lexauthoringservices.model.CompetencyWebHookResponse;
import com.infosys.lexauthoringservices.service.CompetencyService;
import com.infosys.lexauthoringservices.util.LexConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("/action/competency")
public class CompetencyController {

    @Autowired
    private CompetencyService competencyService;

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CompetencyWebHookResponse> updateCompetencyData(@RequestHeader("rootOrg") String rootOrg, @Valid @RequestBody Competency competency) {
        competency.setRootOrg(rootOrg);
        competencyService.updateCompetencyData(competency);
        CompetencyWebHookResponse response = new CompetencyWebHookResponse();
        HashMap<String, Object> statusInfo = new HashMap<>();
        statusInfo.put(LexConstants.STATUS_CODE, HttpStatus.ACCEPTED.value());
        statusInfo.put(LexConstants.STATUS_MESSAGE, LexConstants.STATUS_MESSAGE_VALUE);
        statusInfo.put(LexConstants.ERROR_MESSAGE, "");
        response.setResponseData(true);
        response.setStatusInfo(statusInfo);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}

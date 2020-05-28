package com.infosys.lex.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infosys.lex.notification.dto.SMTPDTO;
import com.infosys.lex.notification.service.SMTPConfigService;

@RestController
@CrossOrigin(origins = "*")
public class SMTPController {

 

    @Autowired
    SMTPConfigService svc;

 

    @PostMapping("/v1/smtp-config")
    public ResponseEntity<?> putConfig(@RequestHeader(name = "rootOrg") String rootOrg,
    		@RequestHeader(name = "org") String org,@RequestBody SMTPDTO requestData) throws JsonProcessingException {

 

        svc.putSMTPConfig(rootOrg,org, requestData);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

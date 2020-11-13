package com.igot.profileManager.controller;

import com.igot.profileManager.models.ProfileWfRequest;
import com.igot.profileManager.service.ProfileManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class ProfileManagerController {

    @Autowired
    private ProfileManagerService profileManagerService;

    @PostMapping("/wf/transition")
    public ResponseEntity<Boolean> findRecommendedConnections(@RequestBody ProfileWfRequest profileWfRequest) {
        Boolean status = profileManagerService.statusChange(profileWfRequest);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}

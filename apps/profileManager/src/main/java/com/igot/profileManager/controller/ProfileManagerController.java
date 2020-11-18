package com.igot.profileManager.controller;

import com.igot.profileManager.models.ProfileWfRequest;
import com.igot.profileManager.models.Response;
import com.igot.profileManager.service.ProfileManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userprofile/")
public class ProfileManagerController {

    @Autowired
    private ProfileManagerService profileManagerService;

    @PostMapping("workflow/transition")
    public ResponseEntity<Response> profileWfTransition(@RequestHeader String rootOrg, @RequestHeader String org, @RequestBody ProfileWfRequest profileWfRequest) {
        Response response = profileManagerService.statusChange(rootOrg, org, profileWfRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "workflow/{wfId}/{userId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getWfUserProfile(@RequestHeader String rootOrg, @RequestHeader String org, @PathVariable("wfId") String wfId, @PathVariable("userId") String userId) {
        Response response = profileManagerService.getWfUserProfile(rootOrg, org, wfId, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "workflow/{profileStatus}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getWfUserProfiles(@RequestHeader String rootOrg, @RequestHeader String org, @PathVariable("profileStatus") String status) {
        Response response = profileManagerService.getWfUserProfileBasedOnStatus(rootOrg, org, status);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "workflow/{wfId}/{userId}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getUserProfileWfHistoryOnWfId(@RequestHeader String rootOrg, @PathVariable("wfId") String wfId, @PathVariable("userId") String userId) {
        Response response = profileManagerService.getUserProfileWfHistoryOnWfId(rootOrg, wfId, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "workflow/nextAction/{state}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getNextActionForState(@RequestHeader String rootOrg, @RequestHeader String org, @PathVariable("state") String state) {
        Response response = profileManagerService.getNextActionForState(rootOrg, org, state);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

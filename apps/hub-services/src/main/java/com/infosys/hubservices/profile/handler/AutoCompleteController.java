package com.infosys.hubservices.profile.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AutoCompleteController {

    @Autowired
    AutoCompleteService autoCompleteService;

    @GetMapping("/v1/user/autocomplete")
    public ResponseEntity<List<Map<String, Object>>> getUserSearchData(@RequestParam("searchString") String searchString) throws Exception {
        return new ResponseEntity<List<Map<String, Object>>>(autoCompleteService.getUserSearchData(searchString), HttpStatus.OK);
    }
}

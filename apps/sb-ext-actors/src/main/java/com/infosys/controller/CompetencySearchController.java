package com.infosys.controller;

import com.infosys.service.CompetencySearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/competency")
public class CompetencySearchController {

    @Autowired
    private CompetencySearchService competencySearchService;

    @GetMapping(value = "/searchOnId/{rootOrg}/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateCompetencyData(@PathVariable String rootOrg, @PathVariable String id) throws IOException {
        return new ResponseEntity<>(competencySearchService.getLexIdsContainingCompId(rootOrg, id), HttpStatus.OK);
    }
}

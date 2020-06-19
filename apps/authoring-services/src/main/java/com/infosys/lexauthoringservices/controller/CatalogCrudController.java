package com.infosys.lexauthoringservices.controller;

import com.infosys.lexauthoringservices.model.Catalog;
import com.infosys.lexauthoringservices.service.CatalogService;
import com.infosys.lexauthoringservices.util.LexConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/action/catalog")
public class CatalogCrudController {

    @Autowired
    CatalogService catalogService;


    @PostMapping("/add")
    public ResponseEntity<?> addCatalog(
            @RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "igot") String rootOrg,
            @RequestParam(value = LexConstants.LOCALE, defaultValue = "en") String locale,
            @Valid @RequestBody Catalog catalog) throws Exception {

        Map<String, Object> responseMap = new HashMap<>();

        Boolean existflag = catalogService.addCatalogTree(catalog, rootOrg, locale, 0, 1);

        responseMap.put("data",catalog);
        responseMap.put("message", existflag ?  "Catalog already exist" : "Successfully added catalog");
        return new ResponseEntity<>(responseMap, existflag ? HttpStatus.OK : HttpStatus.CREATED);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeCatalog(@Valid @RequestBody Catalog catalog,
            @RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "igot") String rootOrg) throws Exception{

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "Successfully deleted catalog with child references");

        catalogService.deleteCatalogTree(catalog, rootOrg);
        return new ResponseEntity<>(responseMap, HttpStatus.OK);

    }

}

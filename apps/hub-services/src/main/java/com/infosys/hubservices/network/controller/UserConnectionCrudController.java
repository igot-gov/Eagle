/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */
package com.infosys.hubservices.network.controller;

import com.infosys.hubservices.model.ConnectionRequest;
import com.infosys.hubservices.model.Response;
import com.infosys.hubservices.serviceimpl.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/connections")
public class UserConnectionCrudController {


    @Autowired
    private ConnectionService connectionService;

    @PostMapping("/add")
    public ResponseEntity<Response> add(@RequestHeader String rootOrg,
                                        @RequestBody ConnectionRequest request) throws Exception{
        Response response = connectionService.add(rootOrg, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PostMapping("/update")
    public ResponseEntity<Response> update(@RequestHeader String rootOrg,
                                           @RequestBody ConnectionRequest request) throws Exception{
        Response response = connectionService.update(rootOrg, request);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


}

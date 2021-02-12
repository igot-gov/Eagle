package com.infosys.hubservices.autocpmplete.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.hubservices.autocpmplete.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/portal/user/autocomplete")
	public ResponseEntity<List<Map<String, Object>>> addDepartment(@RequestParam("userName") String userName) throws Exception {
		return new ResponseEntity<List<Map<String, Object>>>(userService.searchUsername(userName),HttpStatus.OK);
	}
}

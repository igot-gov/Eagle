package com.infosys.hubservices.autocpmplete.service;

import java.util.List;
import java.util.Map;

public interface UserService {
	
	List<Map<String, Object>> searchUsername(String userName);
}

package com.infosys.hubservices.autocpmplete.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.hubservices.autocpmplete.repository.PersonalDetailsRepository;
import com.infosys.hubservices.autocpmplete.repository.UserProfileRepository;
import com.infosys.hubservices.util.Constants;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private PersonalDetailsRepository personalDetailsRepo;

	@Autowired
	private UserProfileRepository userProfileRepo;

	@Override
	public List<Map<String, Object>> searchUsername(String userName) {
		List<Object[]> personalDetailsList = personalDetailsRepo.findByUsernameStartsWithNative(userName);
		List<Map<String, Object>> listmap = new ArrayList<>();
		
		if (!personalDetailsList.isEmpty()) {
			
			Iterator<Object[]> iterator = personalDetailsList.iterator();
			while (iterator.hasNext()) {
				Map<String, Object> map = new HashMap<String, Object>();
				Object[] personalDetails = iterator.next();
				Object userProfile = userProfileRepo.findByPersonalDetailsosidNative(personalDetails[0].toString());
				map.put(Constants.Parmeters.FIRST_NAME, personalDetails[1]);
				map.put(Constants.Parmeters.SURNAME, personalDetails[2]);
				map.put(Constants.Parmeters.PRIMARY_EMAIL, personalDetails[3]);
				map.put(Constants.Parmeters.USERNAME, personalDetails[4]);
				if(userProfile != null)
					map.put(Constants.Parmeters.USER_ID, userProfile.toString());
				else
					map.put(Constants.Parmeters.USER_ID, "");
				
				listmap.add(map);
			}
		}
		return listmap;
	}
}

package org.sunbird.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenSaberApiRespResult {
	@JsonProperty("UserProfile")
	private List<OpenSaberApiUserProfile> userProfile;

	public List<OpenSaberApiUserProfile> getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(List<OpenSaberApiUserProfile> userProfile) {
		this.userProfile = userProfile;
	}
}

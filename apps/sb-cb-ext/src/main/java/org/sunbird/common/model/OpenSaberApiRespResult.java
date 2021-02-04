package org.sunbird.common.model;

import java.util.List;

public class OpenSaberApiRespResult {
	private List<OpenSaberApiUserProfile> UserProfile;

	public List<OpenSaberApiUserProfile> getUserProfile() {
		return UserProfile;
	}

	public void setUserProfile(List<OpenSaberApiUserProfile> userProfile) {
		this.UserProfile = userProfile;
	}
}

package org.sunbird.common.model;

import java.util.List;

public class OpenSaberApiRespResult {
	private List<OpenSaberApiUserProfile> userProfile;

	public List<OpenSaberApiUserProfile> getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(List<OpenSaberApiUserProfile> userProfile) {
		this.userProfile = userProfile;
	}
}

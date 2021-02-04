package org.sunbird.common.model;

public class OpenSaberApiUserProfile {
	private String id;
	private String userId;
	private String osid;
	private OpenSaberApiPersonalDetail personalDetails;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOsid() {
		return osid;
	}

	public void setOsid(String osid) {
		this.osid = osid;
	}

	public OpenSaberApiPersonalDetail getPersonalDetails() {
		return personalDetails;
	}

	public void setPersonalDetails(OpenSaberApiPersonalDetail personalDetails) {
		this.personalDetails = personalDetails;
	}
}

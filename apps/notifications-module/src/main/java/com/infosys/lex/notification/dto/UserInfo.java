package com.infosys.lex.notification.dto;

import java.util.List;
import java.util.Map;

public class UserInfo {

	private String rootOrg;

	private String userId;

	private String firstName;

	private String lastName;

	private String email;

	private String name;

	private List<String> orgs;

	private Map<String, Object> recieveConfig;

	private List<String> preferedLanguages;
	
	

	public UserInfo() {

	}

	public UserInfo(String rootOrg, String userId, String firstName, String lastName, String email, String name,
			List<String> orgs, Map<String, Object> recieveConfig, List<String> preferedLanguages) {
		this.rootOrg = rootOrg;
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.name = name;
		this.orgs = orgs;
		this.recieveConfig = recieveConfig;
		this.preferedLanguages = preferedLanguages;
	}
	

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getOrgs() {
		return orgs;
	}

	public void setOrgs(List<String> orgs) {
		this.orgs = orgs;
	}

	public Map<String, Object> getRecieveConfig() {
		return recieveConfig;
	}

	public void setRecieveConfig(Map<String, Object> recieveConfig) {
		this.recieveConfig = recieveConfig;
	}

	public List<String> getPreferedLanguages() {
		return preferedLanguages;
	}

	public void setPreferedLanguages(List<String> preferedLanguages) {
		this.preferedLanguages = preferedLanguages;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rootOrg == null) ? 0 : rootOrg.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserInfo other = (UserInfo) obj;
		if (rootOrg == null) {
			if (other.rootOrg != null)
				return false;
		} else if (!rootOrg.equals(other.rootOrg))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserInfo [rootOrg=" + rootOrg + ", userId=" + userId + ", firstName=" + firstName + ", lastName="
				+ lastName + ", email=" + email + ", name=" + name + ", orgs=" + orgs + ", recieveConfig="
				+ recieveConfig.toString() + ", preferedLanguage=" + preferedLanguages + "]";
	}
}

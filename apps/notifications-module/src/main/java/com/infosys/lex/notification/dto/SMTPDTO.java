package com.infosys.lex.notification.dto;

public class SMTPDTO {

	private String senderId;

	private String userName;

	private String password;

	private String host;

	private String port;
	
	private Integer chunkSize;
	
	private Boolean signEmail;

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Integer getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(Integer chunkSize) {
		this.chunkSize = chunkSize;
	}

	public Boolean getSignEmail() {
		return signEmail;
	}

	public void setSignEmail(Boolean signEmail) {
		this.signEmail = signEmail;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chunkSize == null) ? 0 : chunkSize.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + ((senderId == null) ? 0 : senderId.hashCode());
		result = prime * result + ((signEmail == null) ? 0 : signEmail.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
		SMTPDTO other = (SMTPDTO) obj;
		if (chunkSize == null) {
			if (other.chunkSize != null)
				return false;
		} else if (!chunkSize.equals(other.chunkSize))
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (senderId == null) {
			if (other.senderId != null)
				return false;
		} else if (!senderId.equals(other.senderId))
			return false;
		if (signEmail == null) {
			if (other.signEmail != null)
				return false;
		} else if (!signEmail.equals(other.signEmail))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	

	
}
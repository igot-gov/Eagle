package com.infosys.lex.notification.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "smtp_config", schema = "wingspan")
public class SMTPConfig {

	@EmbeddedId
	private SMTPConfigKey key;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "password")
	private String password;

	@Column(name = "host")
	private String host;

	@Column(name = "sign_email")
	private boolean signEmail;

	@Column(name = "port")
	private String port;

	@Column(name = "sender_id")
	private String senderId;

	@Column(name = "last_updated_on")
	private Timestamp lastUpdateOn;

	@Column(name = "last_updated_by")
	private String lastUpdateBy;
	
	@Column(name = "chunk_size")
	private Integer chunkSize;
	
	

	public Integer getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(Integer chunkSize) {
		this.chunkSize = chunkSize;
	}

	public boolean isSignEmail() {
		return signEmail;
	}

	public SMTPConfig() {

	}

	

	



	

	public SMTPConfig(SMTPConfigKey key, String userName, String password, String host, boolean signEmail, String port,
			String senderId, Timestamp lastUpdateOn, String lastUpdateBy, Integer chunkSize) {
		this.key = key;
		this.userName = userName;
		this.password = password;
		this.host = host;
		this.signEmail = signEmail;
		this.port = port;
		this.senderId = senderId;
		this.lastUpdateOn = lastUpdateOn;
		this.lastUpdateBy = lastUpdateBy;
		this.chunkSize = chunkSize;
	}

	@Override
	public String toString() {
		return "SMTPConfig [key=" + key + ", userName=" + userName + ", password=" + password + ", host=" + host
				+ ", signEmail=" + signEmail + ", port=" + port + ", senderId=" + senderId + ", lastUpdateOn="
				+ lastUpdateOn + ", lastUpdateBy=" + lastUpdateBy + ", chunkSize=" + chunkSize + "]";
	}

	public SMTPConfigKey getKey() {
		return key;
	}

	public void setKey(SMTPConfigKey key) {
		this.key = key;
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

	public boolean signEmail() {
		return signEmail;
	}

	public void setSignEmail(boolean signEmail) {
		this.signEmail = signEmail;
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

	public Timestamp getLastUpdateOn() {
		return lastUpdateOn;
	}

	public void setLastUpdateOn(Timestamp lastUpdateOn) {
		this.lastUpdateOn = lastUpdateOn;
	}

	public String getLastUpdateBy() {
		return lastUpdateBy;
	}

	public void setLastUpdateBy(String lastUpdateBy) {
		this.lastUpdateBy = lastUpdateBy;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
}

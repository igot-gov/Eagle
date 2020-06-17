package com.infosys.lex.userroles.postgres.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user_roles", schema = "wingspan")
public class UserRole {

	@EmbeddedId
	UserRolePrimaryKey userRolePrimaryKey;

	@Column(name = "updated_on")
	private Timestamp updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;

	public UserRole() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserRole(UserRolePrimaryKey userRolePrimaryKey, Timestamp updatedOn, String updatedBy) {
		super();
		this.userRolePrimaryKey = userRolePrimaryKey;
		this.updatedOn = updatedOn;
		this.updatedBy = updatedBy;
	}

	public UserRolePrimaryKey getUserRolePrimaryKey() {
		return userRolePrimaryKey;
	}

	public void setUserRolePrimaryKey(UserRolePrimaryKey userRolePrimaryKey) {
		this.userRolePrimaryKey = userRolePrimaryKey;
	}

	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString() {
		return "UserRole [userRolePrimaryKey=" + userRolePrimaryKey + ", updatedOn=" + updatedOn + ", updatedBy="
				+ updatedBy + "]";
	}

}

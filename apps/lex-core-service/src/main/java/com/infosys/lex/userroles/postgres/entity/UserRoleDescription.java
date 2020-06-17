package com.infosys.lex.userroles.postgres.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "role", schema = "wingspan")
public class UserRoleDescription {

	@EmbeddedId
	UserRoleDescriptionPrimaryKey key;

	@Column(name = "description")
	private String description;

	public UserRoleDescription() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_on")
	private Timestamp updatedOn;

	public UserRoleDescription(UserRoleDescriptionPrimaryKey key, String description, String updatedBy,
			Timestamp updatedOn) {
		super();
		this.key = key;
		this.description = description;
		this.updatedBy = updatedBy;
		this.updatedOn = updatedOn;
	}

	public UserRoleDescriptionPrimaryKey getKey() {
		return key;
	}

	public void setKey(UserRoleDescriptionPrimaryKey key) {
		this.key = key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}

	@Override
	public String toString() {
		return "UserRoleDescription [key=" + key + ", description=" + description + ", updatedBy=" + updatedBy
				+ ", updatedOn=" + updatedOn + "]";
	}

}

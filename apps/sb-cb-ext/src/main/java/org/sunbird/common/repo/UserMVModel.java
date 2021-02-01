package org.sunbird.common.repo;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("mv_user")
public class UserMVModel {

	@PrimaryKey
	@Column("email")
	private String email;
	
	@Column("id")
	private String id;	

	@Column("firstname")
	private String firstName;

	@Column("lastname")
	private String lastName;
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public UserMVModel(String email, String id, String firstName, String lastName) {
		super();
		this.email = email;
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public UserMVModel() {
		super();
	}

	@Override
	public String toString() {
		return "UserMVModel [email=" + email + ", id=" + id + ", firstName=" + firstName + ", lastName=" + lastName
				+ "]";
	}	

}
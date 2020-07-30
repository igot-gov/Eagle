/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.ext.Models.Cassandra;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("mv_user")
public class UserMV {

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

	public UserMV(String email, String id, String firstName, String lastName) {
		super();
		this.email = email;
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public UserMV() {
		super();
	}

	@Override
	public String toString() {
		return "UserMVModel [email=" + email + ", id=" + id + ", firstName=" + firstName + ", lastName=" + lastName
				+ "]";
	}	

}
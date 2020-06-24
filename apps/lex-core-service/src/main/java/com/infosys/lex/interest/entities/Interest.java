/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.interest.entities;


import java.util.Date;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "user_interest")
public class Interest {

	public Interest() {
		super();
	}

	@PrimaryKey
	private InterestKey interestKey;

	private Set<String> interest;

	@Column("created_on")
	private Date createdOn;

	@Column("updated_on")
	private Date updatedOn;

	public InterestKey getInterestKey() {
		return interestKey;
	}

	public void setInterestKey(InterestKey interestKey) {
		this.interestKey = interestKey;
	}

	public Set<String> getInterest() {
		return interest;
	}

	public void setInterest(Set<String> interest) {
		this.interest = interest;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public Interest(InterestKey interestKey, Set<String> interest,  Date createdOn,  Date updatedOn) {
		super();
		this.interestKey = interestKey;
		this.interest = interest;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	@Override
	public String toString() {
		return "Interest [interestKey=" + interestKey + ", interest=" + interest + ", createdOn=" + createdOn
				+ ", updatedOn=" + updatedOn + "]";
	}

}

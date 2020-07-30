package com.infosys.lex.ext.Models.Cassandra;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_terms_condition")
public class UserTermsAndCondition {
	@PrimaryKey
	private UserTermsAndConditionPrimaryKey userTermsAndConditionPrimaryKey;
	@Column("date_accepted")
	private Date dateAccepted;
	@Column("email")
	private String email;

	public UserTermsAndConditionPrimaryKey getUserTermsAndConditionPrimaryKey() {
		return userTermsAndConditionPrimaryKey;
	}

	public void setUserTermsAndConditionPrimaryKey(UserTermsAndConditionPrimaryKey userTermsAndConditionPrimaryKey) {
		this.userTermsAndConditionPrimaryKey = userTermsAndConditionPrimaryKey;
	}

	public Date getDateAccepted() {
		return dateAccepted;
	}

	public void setDateAccepted(Date dateAccepted) {
		this.dateAccepted = dateAccepted;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

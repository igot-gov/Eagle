package com.infosys.lex.ext.Models.Cassandra;

import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_topics_mapping")
public class UserTopicsMapping {
	@PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String userId;
	@Column("date_created")
	private Date dateCreated;
	@Column("date_modified")
	private Date dateModified;
	@Column("topic_names")
	private List<String> topicNames;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public List<String> getTopicNames() {
		return topicNames;
	}

	public void setTopicNames(List<String> topicNames) {
		this.topicNames = topicNames;
	}

}

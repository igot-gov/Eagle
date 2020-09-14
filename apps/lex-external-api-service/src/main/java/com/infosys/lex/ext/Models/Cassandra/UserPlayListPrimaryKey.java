package com.infosys.lex.ext.Models.Cassandra;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class UserPlayListPrimaryKey implements Serializable {
	private static final long serialVersionUID = 1L;
	@PrimaryKeyColumn(name = "user_email", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String userEmail;
	@PrimaryKeyColumn(name = "playlist_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
	private String playListId;

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getPlayListId() {
		return playListId;
	}

	public void setPlayListId(String playListId) {
		this.playListId = playListId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

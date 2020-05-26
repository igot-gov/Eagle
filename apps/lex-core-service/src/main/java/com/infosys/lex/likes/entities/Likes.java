/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.likes.entities;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_likes")
public class Likes {

	@PrimaryKey
	LikesKey likesKey;
	
	@Column("date_created")
	private Date dateCreated;
	
	public Likes(LikesKey likesKey) {
		this.likesKey=likesKey;
	}
	public Likes() {
		
	}
	public Likes(LikesKey likesKey,Timestamp time) {
		this.likesKey=likesKey;
		this.dateCreated=time;
	}
	public LikesKey getLikesKey() {
		return likesKey;
	}

	public void setLikesKey(LikesKey likesKey) {
		this.likesKey = likesKey;
	}

	public Date getTimeStamp() {
		return dateCreated;
	}

	public void setTimeStamp(Date timeStamp) {
		this.dateCreated = timeStamp;
	}
	
}

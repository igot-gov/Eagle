package com.infosys.lex.ext.Models.Cassandra;

import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_playlist")
public class UserPlayList {
	@PrimaryKey
	private UserPlayListPrimaryKey UserPlayListPrimaryKey;
	@Column("created_on")
	private Date createdOn;
	@Column("isshared")
	private Integer isShared;
	@Column("last_updated_on")
	private Date lastUpdatedOn;
	@Column("playlist_title")
	private String playListTitle;
	@Column("resource_ids")
	private List<String> resourceIds;
	@Column("shared_by")
	private String sharedBy;
	@Column("source_playlist_id")
	private String sourcePlayListId;
	@Column("visibility")
	private String visibility;

	public UserPlayListPrimaryKey getUserPlayListPrimaryKey() {
		return UserPlayListPrimaryKey;
	}

	public void setUserPlayListPrimaryKey(UserPlayListPrimaryKey userPlayListPrimaryKey) {
		UserPlayListPrimaryKey = userPlayListPrimaryKey;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getIsShared() {
		return isShared;
	}

	public void setIsShared(Integer isShared) {
		this.isShared = isShared;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getPlayListTitle() {
		return playListTitle;
	}

	public void setPlayListTitle(String playListTitle) {
		this.playListTitle = playListTitle;
	}

	public List<String> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(List<String> resourceIds) {
		this.resourceIds = resourceIds;
	}

	public String getSharedBy() {
		return sharedBy;
	}

	public void setSharedBy(String sharedBy) {
		this.sharedBy = sharedBy;
	}

	public String getSourcePlayListId() {
		return sourcePlayListId;
	}

	public void setSourcePlayListId(String sourcePlayListId) {
		this.sourcePlayListId = sourcePlayListId;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
}

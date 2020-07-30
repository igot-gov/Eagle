package com.infosys.lex.ext.Models.Cassandra;

import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("playlist_shared")
public class SharedPlayLists {
	@PrimaryKey
	SharedPlayListsPrimaryKey sharedPlayListsPrimaryKey;
	@Column("last_updated_on")
	private Date lastUpdatedOn;
	@Column("playlist_title")
	private String playListTitle;
	@Column("resource_ids")
	private List<String> resourceIds;
	@Column("shared_by")
	private String sharedBy;
	@Column("shared_on")
	private Date sharedOn;
	@Column("visibility")
	private String visibility;

	public SharedPlayListsPrimaryKey getSharedPlayListsPrimaryKey() {
		return sharedPlayListsPrimaryKey;
	}

	public void setSharedPlayListsPrimaryKey(SharedPlayListsPrimaryKey sharedPlayListsPrimaryKey) {
		this.sharedPlayListsPrimaryKey = sharedPlayListsPrimaryKey;
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

	public Date getSharedOn() {
		return sharedOn;
	}

	public void setSharedOn(Date sharedOn) {
		this.sharedOn = sharedOn;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

}
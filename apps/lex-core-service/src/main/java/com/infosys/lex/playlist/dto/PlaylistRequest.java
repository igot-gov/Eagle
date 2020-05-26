/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.playlist.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlaylistRequest implements Serializable {

	private static final long serialVersionUID = -30011456135291206L;

	@NotNull(message = "playlist.title.mandatory")
	@NotEmpty(message = "playlist.title.empty")
	@JsonProperty(value = "playlist_title")
	private String playListTitle;

	@JsonProperty(value = "content_ids")
	@NotNull(message = "contentIds.mandatory")
	@NotEmpty(message = "contentids.notNull")
	private List<String> contentIds;

	public PlaylistRequest() {
		super();
	}

	public PlaylistRequest(String playListTitle, List<String> contentIds) {
		super();
		this.playListTitle = playListTitle;
		this.contentIds = contentIds;
	}

	@Override
	public String toString() {
		return "PlaylistRequest [playListTitle=" + playListTitle + ", contentIds=" + contentIds + "]";
	}

	public String getPlayListTitle() {
		return playListTitle;
	}

	public void setPlayListTitle(String playListTitle) {
		this.playListTitle = playListTitle;
	}

	public List<String> getContentIds() {
		return contentIds;
	}

	public void setContentIds(List<String> contentIds) {
		this.contentIds = contentIds;
	}

}

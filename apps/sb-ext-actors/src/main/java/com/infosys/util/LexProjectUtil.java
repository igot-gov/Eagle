/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.util;

/**
 * Created by Krishnendu_C on 8/16/2018.
 */
public class LexProjectUtil {

	private static String bodhi_ui_index;

	static {
		if (System.getenv("bodhi_ui_index") == null)
			bodhi_ui_index = "mlsearch_*";
		else
			bodhi_ui_index = System.getenv("bodhi_ui_index");
	}

	public static enum EsIndex {
		multi_lingual_search_index("mlsearch"), sunbird("searchindex"), sunbirdDataAudit("sunbirddataaudit"),
		bodhi("mlsearch_*"), staging("lexcontentindex_14"), ekstep("ekstepcontentindex"), lexTopic("lex_topic"),
		lex_user_feedback("lex_feedback"), lexUserAutoComplete("userautocomplete"), authoring_tool("mlsearch_*"),
		skills("lexskillsindex_v1"), unit("lexunitindex"), authoring_tool_bkup("mlsearch_*"), client("clientindex_v1"),
		bodhi_ui(bodhi_ui_index), new_lex_search("mlsearch_*"), access_control_groups("accesscontrolgroups"),
		topic_topic("arl_lex_topic"), topic_details("topicdetails"), ml_search_all("mlsearch_*");

		private String indexName;

		private EsIndex(String name) {
			this.indexName = name;
		}

		public String getIndexName() {
			return this.indexName;
		}
	}

	public static enum Status {
		DRAFT("Draft"), LIVE("Live"), RETIRED("Retired");

		private String value;

		private Status(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	public static enum EsType {// resource("resource")
		course("course"), content("content"), user("user"), organisation("org"), usercourses("usercourses"),
		autoCompleteType("autocomplete"), usernotes("usernotes"), history("history"),
		userprofilevisibility("userprofilevisibility"), feedback("feedback"), resource("searchresources"),
		skills("skills"), unit("units"), client("clienttype"), new_lex_search("searchresources"),
		access_control_group("group"), topic_topic("topic_pid"), topic_details("doc");

		private String typeName;

		private EsType(String name) {
			this.typeName = name;
		}

		public String getTypeName() {
			return this.typeName;
		}
	}

	public static enum SearchType {
		INTEREST("Interest"), PLAYLIST("PlayList"),
		GOALS("Goals"), HISTORY("History"), TRENDING("Trending");

		private String value;

		private SearchType(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * Enum to hold the content type
	 *
	 * @author Krishnendu
	 */
	public enum ContentType {
		learningPath("Learning Path"), course("Course"), collection("Collection"), resource("Resource");

		private String contentType;

		private ContentType(String contentType) {
			this.contentType = contentType;
		}

		public String get() {
			return contentType;
		}

		private void set(String contentType) {
			this.contentType = contentType;
		}
	}

	/**
	 * Enum to hold the resource type
	 *
	 * @author Krishnendu
	 */
	public enum ResourceType {
		course("Course"), lecture("Lecture"), module("Module"), content("content");

		private String resourceType;

		private ResourceType(String resourceType) {
			this.resourceType = resourceType;
		}

		public String get() {
			return resourceType;
		}

		private void set(String resourceType) {
			this.resourceType = resourceType;
		}
	}

	/**
	 * Enum to hold the media type
	 *
	 * @author Krishnendu
	 */
	public enum MediaType {
		content("content");

		private String mediaType;

		private MediaType(String mediaType) {
			this.mediaType = mediaType;
		}

		public String get() {
			return mediaType;
		}

		private void set(String mediaType) {
			this.mediaType = mediaType;
		}
	}

	/**
	 * Enum to hold the mime type
	 *
	 * @author Krishnendu
	 */
	public enum MimeType {
		videoMp4("video/mp4"), youtube("video/x-youtube"), collection("application/vnd.ekstep.content-collection");

		private String mimeType;

		private MimeType(String mimeType) {
			this.mimeType = mimeType;
		}

		public String get() {
			return mimeType;
		}

		private void set(String mimeType) {
			this.mimeType = mimeType;
		}
	}

	/**
	 * Enum to hold the visibility
	 *
	 * @author Krishnendu
	 */
	public enum Visibility {
		defaultVisibility("Default"), parent("Parent");

		private String visibility;

		private Visibility(String visibility) {
			this.visibility = visibility;
		}

		public String get() {
			return visibility;
		}

		private void set(String visibility) {
			this.visibility = visibility;
		}
	}

	/**
	 * Enum to hold the ideal screen size
	 *
	 * @author Krishnendu
	 */
	public enum IdealScreenSize {
		seven("7"), ten("10"), thirteen("13");

		private String idealScreenSize;

		private IdealScreenSize(String idealScreenSize) {
			this.idealScreenSize = idealScreenSize;
		}

		public String get() {
			return idealScreenSize;
		}

		private void set(String idealScreenSize) {
			this.idealScreenSize = idealScreenSize;
		}
	}

	public static String getFormattedTime(Long durationInMillis) {
		long millis = durationInMillis % 1000;
		long second = (durationInMillis / 1000) % 60;
		long minute = (durationInMillis / (1000 * 60)) % 60;
		// return String.format("%02d:%02d.%d",minute, second, millis);
		return "Time taken: " + durationInMillis;
	}

}

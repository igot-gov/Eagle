package com.infosys.lex.progress.bodhi.repo;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MandatoryContentResponse {
	private boolean mandatoryCourseCompleted;
	private Map<String, MandatoryContentInfo> contentDetails;

	public void addContentInfo(String contentId, MandatoryContentInfo contentInfo) {
		if (contentDetails == null) {
			contentDetails = new HashMap<String, MandatoryContentInfo>();
		}
		contentDetails.put(contentId, contentInfo);
	}
}

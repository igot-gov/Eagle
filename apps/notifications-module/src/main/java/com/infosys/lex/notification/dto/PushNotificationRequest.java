package com.infosys.lex.notification.dto;

public class PushNotificationRequest {

	

	private String rootOrg;

	private String eventId;

	private String userId;

	private String subject;

	private String body;

	private String targetUrl;

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		result = prime * result + ((rootOrg == null) ? 0 : rootOrg.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((targetUrl == null) ? 0 : targetUrl.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PushNotificationRequest other = (PushNotificationRequest) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (eventId == null) {
			if (other.eventId != null)
				return false;
		} else if (!eventId.equals(other.eventId))
			return false;
		if (rootOrg == null) {
			if (other.rootOrg != null)
				return false;
		} else if (!rootOrg.equals(other.rootOrg))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (targetUrl == null) {
			if (other.targetUrl != null)
				return false;
		} else if (!targetUrl.equals(other.targetUrl))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	public PushNotificationRequest(String rootOrg, String eventId, String userId, String subject, String body,
			String targetUrl) {
		this.rootOrg = rootOrg;
		this.eventId = eventId;
		this.userId = userId;
		this.subject = subject;
		this.body = body;
		this.targetUrl = targetUrl;
	}

	public PushNotificationRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "PushNotificationRequest [rootOrg=" + rootOrg + ", eventId=" + eventId + ", userId=" + userId
				+ ", subject=" + subject + ", body=" + body + ", targetUrl=" + targetUrl + "]";
	}
	
	
}
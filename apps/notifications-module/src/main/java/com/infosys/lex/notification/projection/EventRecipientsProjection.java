package com.infosys.lex.notification.projection;

import org.springframework.beans.factory.annotation.Value;

public interface EventRecipientsProjection {

	@Value("#{target.tagsPrimaryKey.recipient}")
	public String getRecipient();

	@Value("#{target.tagsPrimaryKey.eventId}")
	public String getEventId();
}

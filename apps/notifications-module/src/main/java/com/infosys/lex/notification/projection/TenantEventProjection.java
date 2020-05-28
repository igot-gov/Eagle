package com.infosys.lex.notification.projection;

public interface TenantEventProjection {

	String getOrg();

	String getGroupId();

	String getGroupName();

	String getEventId();

	String getEventName();

	String getRecipient();

	String getModeId();

	String getModeName();

}

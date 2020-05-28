/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential

*/

package com.infosys.lex.notification.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.infosys.lex.notification.entity.TenantTemplate;
import com.infosys.lex.notification.entity.TenantTemplatePrimaryKey;
import com.infosys.lex.notification.projection.RecipientTemplateProjection;

public interface TenantTemplateRepository extends CrudRepository<TenantTemplate, TenantTemplatePrimaryKey> {

	@Query(nativeQuery = true, value = "select template_subject,template_text,language from wingspan.tenant_event_template where template_id=?1 and language in ?2")
	public List<Map<String, Object>> fetchTemplateByIdAndLanguage(String templateId, List<String> languages);

	@Query(nativeQuery = true, value = "select ten.event_id,ten.mode,ten.recipient,ten.template_id,tet.template_subject,tet.template_text,tet.language from wingspan.tenant_event_notification ten inner join wingspan.tenant_event_template tet on ten.template_id=tet.template_id where tet.language in ?4 and ten.root_org='default' and ten.org='default' and ten.event_id=?1 and ten.recipient=?2 and ten.mode=?3")
	public List<Map<String, Object>> fetchDefaultTemplateForEventAndRecipientRoleAndModeAndLanguage(String eventId,
			String recipientRole, String mode, List<String> languages);

	@Query(nativeQuery = true, value = "select notif.root_org as RootOrg,recipient as Recipient,temp.template_id as "
			+ "TemplateId,temp.template_text as TemplateText,temp.template_subject "
			+ "as TemplateSubject,language as Language from wingspan.tenant_event_notification notif "
			+ "left outer join (select * from wingspan.tenant_event_template "
			+ "where language in ?1) as temp on notif.template_id=temp.template_id "
			+ "where notif.root_org=?2 and notif.org=?3"
			+ " and notif.mode=?4 and notif.mode_activated='t' and notif.event_id=?5")
	public List<RecipientTemplateProjection> fetchTemplates(List<String> languages, String rootOrg, String org,
			String mode, String eventId);

	@Query(nativeQuery = true, value = "select notif.root_org as RootOrg,recipient as Recipient,temp.template_id as "
			+ "TemplateId,temp.template_text as TemplateText,temp.template_subject "
			+ "as TemplateSubject,language as Language from wingspan.tenant_event_notification notif inner join wingspan.tenant_event_template "
			+ "temp on notif.template_id=temp.template_id where notif.root_org=?1 and notif.org=?2 and "
			+ "language in ?3 and notif.mode_activated='t' and notif.event_id=?4 "
			+ "and notif.mode=?5 and notif.recipient in ?6")
	public List<RecipientTemplateProjection> fetchDefaultTemplates(String rootOrg, String org, List<String> languages,
			String eventId, String modeId, List<String> recipients);
	
	@Query(nativeQuery = true, value = "select template_id,template_subject,template_text,language from wingspan.tenant_event_template where template_id in ?1 and language in ?2")
	public List<Map<String, Object>> fetchTemplatesByIdsAndLanguage(Set<String> templateIds, Set<String> languages);
	

	
	
	@Query(nativeQuery = true,value  = "select template_id,template_subject,template_text,language from wingspan.tenant_event_template where template_id in ?5 and language in ?4\r\n" + 
			"UNION \r\n" + 
			"(select CAST('default' as VARCHAR) as template_id,tet.template_subject,tet.template_text,tet.language from wingspan.tenant_event_notification ten inner join wingspan.tenant_event_template tet on ten.template_id=tet.template_id "
			+ "where tet.language in ?4 and ten.root_org='default' and ten.org='default' and ten.event_id=?1 and ten.recipient=?2 and ten.mode=?3)")
	public List<Map<String,Object>> fetchAllTemplatesByIdsAndLanguagesWithDefaultTemplate(String eventId,
			String recipientRole, String mode, Set<String> languages,Set<String>templateIds);
	
	@Query(nativeQuery = true,value  = "select CAST('default' as VARCHAR) as template_id,tet.template_subject,tet.template_text,tet.language from wingspan.tenant_event_notification ten inner join wingspan.tenant_event_template tet on ten.template_id=tet.template_id "
			+ "where tet.language in ?4 and ten.root_org='default' and ten.org='default' and ten.event_id=?1 and ten.recipient=?2 and ten.mode=?3")
	public List<Map<String,Object>> fetchAllDefaultTemplatesForEventIdAndRecipientRoleAndModeAndLanguages(String eventId,
			String recipientRole, String mode, List<String> languages);
	
	@Query(nativeQuery = true,value = "select template_id,template_subject,template_text,language from wingspan.tenant_event_template where template_id in ?4 \r\n" + 
			"	UNION " + 
			"	(select CAST('default' as VARCHAR) as template_id,tet.template_subject,tet.template_text,tet.language from wingspan.tenant_event_notification ten inner join wingspan.tenant_event_template tet on ten.template_id=tet.template_id " + 
			"	where  ten.root_org='default' and ten.org='default' and ten.event_id=?1 and ten.recipient=?2 and ten.mode=?3)")
	public List<Map<String,Object>> fetchAllTemplatesByTemplateIdsWithDefaultTemplate(String eventId,
			String recipientRole, String mode, Set<String>templateIds);
	
	@Query(nativeQuery = true,value  = "select CAST('default' as VARCHAR) as template_id,tet.template_subject,tet.template_text,tet.language from wingspan.tenant_event_notification ten inner join wingspan.tenant_event_template tet on ten.template_id=tet.template_id "
			+ "where  ten.root_org='default' and ten.org='default' and ten.event_id=?1 and ten.recipient=?2 and ten.mode=?3")
	public List<Map<String,Object>> fetchAllDefaultTemplatesForEventIdAndRecipientRoleAndMode(String eventId,
			String recipientRole, String modes);


}

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

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.notification.entity.TenantEvent;
import com.infosys.lex.notification.entity.TenantEventPrimaryKey;

@Repository
public interface TenantEventRepository extends CrudRepository<TenantEvent, TenantEventPrimaryKey> {

	@Query(nativeQuery = true, value = "select eg.group_id,eg.group_name,ten.event_id,eg.event_name,eg.event_description,ten.recipient,rd.recipient_name,rd.recipient_description,rd.admin_description,eg.language,ten.mode,tl.mode_name,ten.mode_activated,receiver_emails from wingspan.tenant_event_notification ten inner join wingspan.event_group eg on ten.event_id=eg.event_id inner join wingspan.tenant_mode_language tl on ten.mode=tl.mode inner join wingspan.recipient_description rd on ten.event_id=rd.event_id and ten.recipient=rd.recipient where tl.language=?1 and rd.language=?1 and ten.root_org='default' and ten.org='default' and ten.mode_activated=true and eg.language=?1 and ten.mode in (select distinct(mode) from wingspan.tbl_tenant_mode where root_org='default' and activated=true)")
	public List<Map<String, Object>> getDefaultActivatedEventsWithLangPreference(String language);

	@Query(nativeQuery = true, value = "select eg.group_id,eg.group_name,ten.event_id,eg.event_name,eg.event_description,ten.recipient,rd.recipient_name,rd.recipient_description,rd.admin_description,eg.language,ten.mode,tl.mode_name,ten.mode_activated,receiver_emails from wingspan.tenant_event_notification ten inner join wingspan.event_group eg on ten.event_id=eg.event_id inner join wingspan.tenant_mode_language tl on ten.mode=tl.mode inner join wingspan.recipient_description rd on ten.event_id=rd.event_id and ten.recipient=rd.recipient where tl.language=?3 and ten.root_org=?1 and ten.org=?2 and eg.language=?3 and ten.mode in (select distinct(mode) from wingspan.tbl_tenant_mode where root_org=?1 and org = ?2 and activated=true)")
	public List<Map<String, Object>> getTenantConfiguredEventsWithLangPreference(String rootOrg, String org,
			String language);

	@Query(nativeQuery = true, value = "select ften.org as org,ften.event_id as event_id,ften.receiver_emails as receiver_emails,eg.event_name as event_name,eg.group_id as group_id,eg.group_name as group_name,eg.event_description,ften.recipient as recipient,res_desc.recipient_name recipient_name ,res_desc.recipient_description,res_desc.admin_description,ften.mode as mode,tml.mode_name as mode_name,ften.mode_activated as mode_activated from \r\n"
			+ "(select * from wingspan.tenant_event_notification \r\n" + "	where root_org=?1 and org=?2 \r\n"
			+ "	and event_id in (Select event_id from wingspan.tenant_event_notification ten inner join wingspan.tbl_tenant_mode ttm on ten.mode=ttm.mode \r\n"
			+ "	where ten.root_org='default' and ten.org='default' and ttm.activated=true and ten.mode_activated=true) \r\n"
			+ "	and mode_activated=true \r\n"
			+ "	and mode in (select mode from wingspan.tbl_tenant_mode where root_org='default' and org='default' and activated=true) \r\n"
			+ "	and mode in (select mode from wingspan.tbl_tenant_mode where root_org=?1 and org=?2 and activated=true)\r\n"
			+ ") ften \r\n"
			+ "inner join wingspan.tenant_mode_language tml on ften.mode=tml.mode and tml.language in ?3 \r\n"
			+ "inner join wingspan.event_group eg on ften.event_id=eg.event_id  and eg.language in ?3\r\n"
			+ "inner join wingspan.recipient_description res_desc on res_desc.recipient = ften.recipient and ften.event_id = res_desc.event_id\r\n"
			+ "where res_desc.language in ?3")
	public List<Map<String, Object>> getTenantConfiguredEvents(String rootOrg, String org, List<String> language);

	@Query(nativeQuery = true, value = "select ten.root_org,ten.org,eg.group_id,eg.group_name,eg.event_id, eg.event_name,eg.language,eg.event_description,"
			+ "ten.recipient,rd.recipient_name,rd.recipient_description,rd.admin_description,ten.mode,tl.mode_name,ten.mode_activated,ten.receiver_emails "
			+ "from wingspan.tenant_event_notification ten inner join wingspan.tenant_event_notification def on def.event_id=ten.event_id "
			+ "and def.recipient=ten.recipient and def.mode=ten.mode inner join wingspan.event_group eg on ten.event_id=eg.event_id  "
			+ "inner join wingspan.tenant_mode_language tl on tl.mode=ten.mode "
			+ "inner join wingspan.recipient_description rd on eg.event_id=rd.event_id "
			+ "and ten.recipient=rd.recipient where def.root_org='default' "
			+ "and def.org='default' and def.mode_activated=true and ten.root_org=?1 and tl.language in ?3  "
			+ "and ten.org in ?2 and eg.language in ?3 and rd.language in ?3 and ten.mode_activated=true "
			+ "order by ten.root_org,ten.org,eg.group_id,eg.event_name,ten.recipient,ten.mode")
	public List<Map<String, Object>> getActivatedEventsByRootOrgAndOrgs(String rootOrg, List<String> orgs,
			List<String> languages);

	@Query(nativeQuery = true, value = "select ten.root_org,ten.org,ten.event_id,ten.recipient,ten.mode,ten.mode_activated,ten.template_id,ten.receiver_emails from wingspan.tenant_event_notification ten inner join wingspan.tenant_event_notification def on def.event_id=ten.event_id and def.recipient=ten.recipient and def.mode=ten.mode  where def.root_org='default' and def.org='default' and def.mode_activated=true and def.event_id=?3 and ten.root_org =?1 and ten.org in ?2 and ten.event_id=?3 and ten.mode_activated=true order by ten.org,ten.event_id,ten.recipient,ten.mode")
	public List<Map<String, Object>> getActivatedModesForRootOrgAndOrgs(String rootOrg, List<String> orgs,
			String eventId);
}
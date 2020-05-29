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
package com.infosys.lex.notification.bodhi.repositoryImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.notification.bodhi.repository.UserNotificationRepositoryCustom;
import com.infosys.lex.notification.dto.NotificationDigestDTO;
import com.infosys.lex.notification.dto.NotificationSendDTO;
import com.infosys.lex.notification.exception.InvalidDataInputException;

@Repository
public class UserNotificationRepositoryImpl implements UserNotificationRepositoryCustom {

	@Autowired
	CassandraOperations cassandraTemplate;

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public NotificationSendDTO getAllNotifications(String rootOrg, String userId, String page, Integer size,
			String classification) throws Exception {

		List<String> classificationToFetch = new ArrayList<>();
		if (classification.equalsIgnoreCase("all"))
			classificationToFetch.addAll(Arrays.asList("Information", "Action"));
		else
			classificationToFetch.add(classification);

		Where select = QueryBuilder
				.select("user_id", "classified_as", "notification_id", "event_id", "message", "receiving_role",
						"received_on", "seen", "target_data")
				.from("user_notification").where(QueryBuilder.eq("root_org", rootOrg))
				.and(QueryBuilder.eq("user_id", userId)).and(QueryBuilder.in("classified_as", classificationToFetch));

		select.setFetchSize(size);

		if (!page.equals("0"))
			select.setPagingState(PagingState.fromString(page));

		ResultSet results = cassandraTemplate.getCqlOperations().queryForResultSet(select);
		PagingState nextPage = results.getExecutionInfo().getPagingState();
		int remaining = results.getAvailableWithoutFetching();

		List<NotificationDigestDTO> response = new ArrayList<NotificationDigestDTO>();
		for (Row digest : results) {
			Map<String, Object> targetData = new HashMap<>();
			if (digest.getString("target_data") != null && !digest.getString("target_data").toString().isEmpty()) {
				targetData = mapper.readValue(digest.getString("target_data"),
						new TypeReference<Map<String, Object>>() {
						});
			}
			response.add(new NotificationDigestDTO(digest.getUUID("notification_id").toString(),
					digest.getString("classified_as"), digest.getBool("seen"), digest.getString("message"),
					new Timestamp(digest.getTimestamp("received_on").getTime()), userId, targetData,
					digest.getString("event_id")));

			if (--remaining == 0)
				break;
		}
		return new NotificationSendDTO(response, nextPage == null ? "-1" : nextPage.toString());
	}

	/**
	 * This method returns either seen or unseen notification but doesnt return all
	 * the notification (classifications)
	 */
	@Override
	public NotificationSendDTO getNotificationsBySeen(String rootOrg, String userId, String page, Integer size,
			String classification, Boolean seen) throws Exception {

		if (classification.equals("all"))
			throw new InvalidDataInputException("Notification classification passed is empty", null);

		Where select = QueryBuilder
				.select("user_id", "classified_as", "notification_id", "event_id", "message", "receiving_role",
						"received_on", "seen", "target_data")
				.from("user_notification").where(QueryBuilder.eq("root_org", rootOrg))
				.and(QueryBuilder.eq("user_id", userId)).and(QueryBuilder.eq("classified_as", classification))
				.and(QueryBuilder.eq("seen", seen));

		select.setFetchSize(size);

		if (!page.equals("0"))
			select.setPagingState(PagingState.fromString(page));

		ResultSet results = cassandraTemplate.getCqlOperations().queryForResultSet(select);
		PagingState nextPage = results.getExecutionInfo().getPagingState();
		int remaining = results.getAvailableWithoutFetching();

		List<NotificationDigestDTO> response = new ArrayList<NotificationDigestDTO>();
		for (Row digest : results) {
			Map<String, Object> targetData = new HashMap<>();
			if (digest.getString("target_data") != null && !digest.getString("target_data").toString().isEmpty()) {
				targetData = mapper.readValue(digest.getString("target_data"),
						new TypeReference<Map<String, Object>>() {
						});
			}
			response.add(new NotificationDigestDTO(digest.getUUID("notification_id").toString(),
					digest.getString("classified_as"), digest.getBool("seen"), digest.getString("message"),
					new Timestamp(digest.getTimestamp("received_on").getTime()), userId, targetData,
					digest.getString("event_id")));

			if (--remaining == 0)
				break;
		}
		return new NotificationSendDTO(response, nextPage == null ? "-1" : nextPage.toString());
	}
}
package com.infosys.lex.notification.consumerImpl;

import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.notification.consumer.EmailNotificationConsumer;
import com.infosys.lex.notification.dto.EmailRequest;
import com.infosys.lex.notification.exception.ApplicationLogicException;
import com.infosys.lex.notification.service.EmailService;
import com.infosys.lex.notification.service.NotificationConsumerUtilService;
import com.infosys.lex.notification.util.LexNotificationLogger;

@Service
public class EmailNotificationConsumerImpl implements EmailNotificationConsumer {

	@Autowired
	EmailService emailService;

	@Autowired
	NotificationConsumerUtilService consumerUtilServ;

	private LexNotificationLogger logger = new LexNotificationLogger(getClass().getName());

	private static final ObjectMapper mapper = new ObjectMapper();

	@KafkaListener(id = "id1", groupId = "email-notification-consumer", topicPartitions = {
			@TopicPartition(topic = "email_notification_events", partitions = { "0", "1", "2", "3" }) })
	public void consumeEmailEvent(ConsumerRecord<?, ?> consumerRecord) {

		if (consumerUtilServ.checkEventTimestamp(consumerRecord.timestamp())) {
			String message = String.valueOf(consumerRecord.value());
			EmailRequest emailEvent = null;
			try {
				emailEvent = mapper.readValue(message, new TypeReference<EmailRequest>() {
				});
				emailService.sendEmail(emailEvent);
			} catch (IOException e) {
				logger.error(e);
				consumerUtilServ.saveError("Could not parse request body", "Could not parse request body", e,
						emailEvent);
			} catch (ApplicationLogicException e) {
				logger.error(e);
				consumerUtilServ.saveError(emailEvent.getRootOrg(), emailEvent.getEventId(), e, emailEvent);
			} catch (Exception e) {
				logger.fatal(e);
				consumerUtilServ.saveError(emailEvent.getRootOrg(), emailEvent.getEventId(), e, emailEvent);
			}
		}
	}

}

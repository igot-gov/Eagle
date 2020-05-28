package com.infosys.lex.notification.consumerImpl;

import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.notification.consumer.PushNotificationConsumer;
import com.infosys.lex.notification.dto.PushNotificationRequest;
import com.infosys.lex.notification.exception.ApplicationLogicException;
import com.infosys.lex.notification.service.NotificationConsumerUtilService;
import com.infosys.lex.notification.service.PushService;
import com.infosys.lex.notification.util.LexNotificationLogger;

@Service
public class PushNotificationConsumerImpl implements PushNotificationConsumer {

	@Autowired
	NotificationConsumerUtilService consumerUtilService;

	@Autowired
	PushService pushService;

	private LexNotificationLogger logger = new LexNotificationLogger(getClass().getName());

	private static final ObjectMapper mapper = new ObjectMapper();

	@KafkaListener(id = "id2", groupId = "push_notification-consumer", topicPartitions = {
			@TopicPartition(topic = "push_notification_events", partitions = { "0", "1", "2", "3" }) })
	public void consumePushNotificationEvent(ConsumerRecord<?, ?> consumerRecord) {

		if (consumerUtilService.checkEventTimestamp(consumerRecord.timestamp())) {
			String message = String.valueOf(consumerRecord.value());
			PushNotificationRequest pushNotificationEvent = null;
			try {
				pushNotificationEvent = mapper.readValue(message, new TypeReference<PushNotificationRequest>() {
				});
				pushService.sendPush(pushNotificationEvent);
			} catch (IOException e) {
				logger.error(e);
				consumerUtilService.saveError(pushNotificationEvent.getRootOrg(), pushNotificationEvent.getEventId(), e,
						pushNotificationEvent);
			} catch (ApplicationLogicException e) {
				logger.error(e);
				consumerUtilService.saveError(pushNotificationEvent.getRootOrg(), pushNotificationEvent.getEventId(), e,
						pushNotificationEvent);
			} catch (Exception e) {
				logger.fatal(e);
				consumerUtilService.saveError(pushNotificationEvent.getRootOrg(), pushNotificationEvent.getEventId(), e,
						pushNotificationEvent);
			}
		}
	}
}

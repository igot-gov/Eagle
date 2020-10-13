package com.infosys.lexauthoringservices.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.model.Competency;
import com.infosys.lexauthoringservices.service.CompetencyService;
import com.infosys.lexauthoringservices.util.LexLogger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CompetencyDataConsumer {

	@Autowired
	private LexLogger logger;

	@Autowired
	private CompetencyService competencyService;

	@KafkaListener(id = "id0", groupId = "competencyUpdateTopic-consumer", topicPartitions = {
			@TopicPartition(topic = "${kafka.topics.competency.update}", partitions = { "0", "1", "2", "3" }) })
	public void processMessage(ConsumerRecord<String, String> data) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String message = String.valueOf(data.value());

		try {
			Competency competency = mapper.readValue(message, Competency.class);
			competencyService.processUpdateCompetencyData(competency);
		} catch (Exception ex) {
			logger.error("Competency Update Process Exception : " + ex.toString());
			throw ex;
		}
	}
}

package com.infosys.lexauthoringservices.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.util.LexLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {

	@Autowired
	private LexLogger logger;

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	public void push(String topic, Object value) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			kafkaTemplate.send(topic, mapper.writeValueAsString(value));
		} catch (JsonProcessingException e) {
			logger.error(e);
		}
	}
}

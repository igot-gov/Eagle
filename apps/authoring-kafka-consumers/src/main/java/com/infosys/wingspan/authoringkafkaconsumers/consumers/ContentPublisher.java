/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*
 * © 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
 * Version: 1.10
 * <p>
 * Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
 * this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
 * the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
 * by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of
 * this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
 * under the law.
 * <p>
 * Highly Confidential
 */
package com.infosys.wingspan.authoringkafkaconsumers.consumers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.wingspan.authoringkafkaconsumers.consumers.impl.ContentPublisherImpl;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ContentPublisher {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${infosys.spring.kafka.publisher.error.output.topic}")
    String publisherErrorTopic;

    @Autowired
    ContentPublisherImpl contentPublisherImpl;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Logger logger = LoggerFactory.getLogger(ContentPublisher.class);

    private String publisherErrorTopicKey = "publishpipeline-stage1-error";

    @SuppressWarnings("unchecked")
    @KafkaListener(clientIdPrefix = "publisher", groupId = "new-es-replication-group", topics = "publishpipeline-stage1")
    public void listenSearch(ConsumerRecord<?, ?> consumerRecord, Acknowledgment acknowledgment) {
        UUID uuid = UUID.randomUUID();

        String message = String.valueOf(consumerRecord.value());

        String logMsg = "" + consumerRecord.partition() + "-" + consumerRecord.offset();
        logger.info(uuid + "->" + "PUBLISHER START - " + logMsg);

        Map<String, Object> failures;
        try {
            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {});

            failures = contentPublisherImpl.processMessage(data, uuid);

            if (null != failures && !failures.isEmpty()) {
                failures.put("ORIGINAL_MESSAGE", data);
                kafkaTemplate.send(publisherErrorTopic, publisherErrorTopicKey, objectMapper.writeValueAsString(failures));
                logger.error(uuid + "->" + "PUBLISHER FAILURE - " + logMsg);
            } else
                logger.info(uuid + "->" + "PUBLISHER SUCCESS - " + logMsg);
        } catch (Exception e) {
            e.printStackTrace();
            kafkaTemplate.send(publisherErrorTopic, publisherErrorTopicKey, message + "--->" + e.getMessage());
            logger.error(uuid + "->" + "PUBLISHER FATAL - " + logMsg);
        }

        acknowledgment.acknowledge();
    }



}

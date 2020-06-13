///**
// * © 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
// * Version: 1.10
// * <p>
// * Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
// * this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
// * the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
// * by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of
// * this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
// * under the law.
// * <p>
// * Extremely Highly Confidential
// */
//package com.infosys.lexauthoringservices.controller;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.infosys.lexauthoringservices.util.InterMLSearchIndexer;
//import com.infosys.lexauthoringservices.util.LexLogger;
//import com.infosys.lexauthoringservices.util.PublishPipeLineStage1;
//import com.infosys.lexauthoringservices.util.SearchIndexer;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.annotation.TopicPartition;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//import java.util.*;
//
//@Service
//public class SearchIndexerConsumer {
//    LexLogger lexLogger = new LexLogger("SearchIndexerConsumer");
//
//    ObjectMapper objectMapper = new ObjectMapper();
//
//    @Autowired
//    KafkaTemplate<String, String> kafkaTemplate;
//    @Value("${infosys.spring.kafka.publisher.error.output.topic}")
//    String publisherErrorTopic;
//    @Value("${infosys.spring.kafka.search.indexer.error.output.topic}")
//    String searchIndexerErrorTopic;
//    @Autowired
//    private SearchIndexer searchIndexer;
//    @Autowired
//    private InterMLSearchIndexer interMLSearchIndexer;
//    @Autowired
//    private PublishPipeLineStage1 publishPipeLineStage1;
//    private String searchIndexerErrorTopicKey = "search-indexer-error";
//    private String publisherErrorTopicKey = "publishpipeline-stage1-error";
//
//    @SuppressWarnings("unchecked")
//    @KafkaListener(id = "search-indexer-errors", clientIdPrefix = "error_indexer", groupId = "es-replication-group", topics = "search-indexer-errors")
//    public void listenErrorSearch(ConsumerRecord<?, ?> consumerRecord) {
//        UUID uuid = UUID.randomUUID();
//        String message = String.valueOf(consumerRecord.value());
//        String logMsg = "" + consumerRecord.partition() + "-" + consumerRecord.offset();
//        System.out.println(uuid + "->" + "SEARCH ERROR INDEXER START   - " + logMsg);
//        try {
//            List<Map<String, Object>> data = objectMapper.readValue(message, new TypeReference<Object>() {
//            });
//            for (Map<String, Object> errObj : data) {
//                Map<String, Object> indexDoc = new HashMap<>();
//                Map<String, Object> dataValue = (Map<String, Object>) errObj.getOrDefault("transactionData", new HashMap<>());
//                String neo4jNodeId = (String) errObj.getOrDefault("nodeGraphId", "").toString();
//                String lex_id = (String) errObj.getOrDefault("nodeUniqueId", "");
//                String opType = (String) errObj.getOrDefault("operationType", "ERROR");
//                String rootOrg = (String) errObj.getOrDefault("graphId", "");
//                String locale = (String) errObj.getOrDefault("locale", "default_locale");
//                String nodeType = (String) errObj.getOrDefault("nodeType", "DEFAULT_VALUE");
//                String samzaError = (String) errObj.getOrDefault("SAMZA_ERROR", "SAMZA_ERROR_DEFAULT");
//                List<Map<String, Object>> stackTrace = (List<Map<String, Object>>) errObj.getOrDefault("SAMZA_ERROR_STACK_TRACE", new ArrayList<>());
//                indexDoc.put("transaction_data", dataValue);
//                indexDoc.put("neo4j_node_id", neo4jNodeId);
//                indexDoc.put("identifier", lex_id);
//                indexDoc.put("operation_type", opType);
//                indexDoc.put("neo4j_label", rootOrg);
//                indexDoc.put("locale", locale);
//                indexDoc.put("node_type", nodeType);
//                indexDoc.put("samza_error", samzaError);
//                indexDoc.put("error_stack_trace", stackTrace);
//                searchIndexer.errorDocIndexing(message, indexDoc);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println(uuid + "->" + "SEARCH ERROR INDEXER FAIL   - " + logMsg);
//        }
//    }
//
//
//    //    @KafkaListener(id = "searcher-indexer1", groupId = "es-replication-group", topicPartitions = {@TopicPartition(topic = "learning-graph-events", partitions = "0")})
//    @KafkaListener(id = "searcher-indexer", groupId = "es-replication-group", topics = "learning-graph-events")
//    public void listenSearch(ConsumerRecord<?, ?> consumerRecord) {
//        UUID uuid = UUID.randomUUID();
//        String message = String.valueOf(consumerRecord.value());
//        String logMsg = "" + consumerRecord.partition() + "-" + consumerRecord.offset();
//        System.out.println(uuid + "->" + "SEARCH INDEXER START   - " + logMsg);
//        try {
//            List<Map<String, Object>> failures = new ArrayList<>();
//            List<Map<String, Object>> data = objectMapper.readValue(message, new TypeReference<Object>() {
//            });
//            failures = searchIndexer.processMessageEnvelope(data, uuid);
//            if (!failures.isEmpty()) {
//                kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey,
//                        objectMapper.writeValueAsString(failures));
//                System.out.println(uuid + "->" + "SEARCH INDEXER FAILURE - " + logMsg + "-" + failures.size());
//            }
//            System.out.println(uuid + "->" + "SEARCH INDEXER SUCCESS - " + logMsg);
//        } catch (Exception e) {
//            e.printStackTrace();
//            kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, message + "--->" + e.getMessage());
//            System.out.println(uuid + "->" + "SEARCH INDEXER FATAL   - " + logMsg);
//        }
//    }
//
//////////    @KafkaListener(id = "searcher-indexer2", groupId = "es-replication-group", topicPartitions = {@TopicPartition(topic = "learning-graph-events", partitions = "1")})
//////////    public void listenSearch1(ConsumerRecord<?,?> consumerRecord) {
//////////        UUID uuid = UUID.randomUUID();
//////////        String message = String.valueOf(consumerRecord.value());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$MESSAGE OFFSET FROM SEARCH 2$$$$$$$$$$$$$$$");
//////////        System.out.println(uuid+"->"+consumerRecord.offset());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//////////        try {
//////////            List<Map<String, Object>> failures = new ArrayList<>();
//////////            List<Map<String, Object>> data = objectMapper.readValue(message, new TypeReference<Object>() {
//////////            });
//////////            failures = searchIndexer.processMessageEnvelope(data, uuid);
//////////            if (!failures.isEmpty()) {
//////////                kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, objectMapper.writeValueAsString(failures));
//////////            }
//////////        } catch (Exception e) {
//////////            e.printStackTrace();
//////////            kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, message + "--->" + e.getMessage());
//////////        }
//////////    }
//////////
//////////    @KafkaListener(id = "searcher-indexer3", groupId = "es-replication-group", topicPartitions = {@TopicPartition(topic = "learning-graph-events", partitions = "2")})
//////////    public void listenSearch2(ConsumerRecord<?,?> consumerRecord) {
//////////        UUID uuid = UUID.randomUUID();
//////////        String message = String.valueOf(consumerRecord.value());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$MESSAGE OFFSET FROM SEARCH 3$$$$$$$$$$$$$$$");
//////////        System.out.println(uuid+"->"+consumerRecord.offset());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//////////        try {
//////////            List<Map<String, Object>> failures = new ArrayList<>();
//////////            List<Map<String, Object>> data = objectMapper.readValue(message, new TypeReference<Object>() {
//////////            });
//////////            failures = searchIndexer.processMessageEnvelope(data, uuid);
//////////            if (!failures.isEmpty()) {
//////////                kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, objectMapper.writeValueAsString(failures));
//////////            }
//////////        } catch (Exception e) {
//////////            e.printStackTrace();
//////////            kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, message + "--->" + e.getMessage());
//////////        }
//////////    }
//////////
//////////    @KafkaListener(id = "searcher-indexer4", groupId = "es-replication-group", topicPartitions = {@TopicPartition(topic = "learning-graph-events", partitions = "3")})
//////////    public void listenSearch3(ConsumerRecord<?,?> consumerRecord) {
//////////        UUID uuid = UUID.randomUUID();
//////////        String message = String.valueOf(consumerRecord.value());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$MESSAGE OFFSET FROM SEARCH 4$$$$$$$$$$$$$$$");
//////////        System.out.println(uuid+"->"+consumerRecord.offset());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//////////        try {
//////////            List<Map<String, Object>> failures = new ArrayList<>();
//////////            List<Map<String, Object>> data = objectMapper.readValue(message, new TypeReference<Object>() {
//////////            });
//////////            failures = searchIndexer.processMessageEnvelope(data, uuid);
//////////            if (!failures.isEmpty()) {
//////////                kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, objectMapper.writeValueAsString(failures));
//////////            }
//////////        } catch (Exception e) {
//////////            e.printStackTrace();
//////////            kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, message + "--->" + e.getMessage());
//////////        }
//////////    }
//////////
//////////    @KafkaListener(id = "searcher-indexer5", groupId = "es-replication-group", topicPartitions = {@TopicPartition(topic = "learning-graph-events", partitions = "4")})
//////////    public void listenSearch5(ConsumerRecord<?,?> consumerRecord) {
//////////        UUID uuid = UUID.randomUUID();
//////////        String message = String.valueOf(consumerRecord.value());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$MESSAGE OFFSET FROM SEARCH 5$$$$$$$$$$$$$$$");
//////////        System.out.println(uuid+"->"+consumerRecord.offset());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//////////        try {
//////////            List<Map<String, Object>> failures = new ArrayList<>();
//////////            List<Map<String, Object>> data = objectMapper.readValue(message, new TypeReference<Object>() {
//////////            });
//////////            failures = searchIndexer.processMessageEnvelope(data, uuid);
//////////            if (!failures.isEmpty()) {
//////////                kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, objectMapper.writeValueAsString(failures));
//////////            }
//////////        } catch (Exception e) {
//////////            e.printStackTrace();
//////////            kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, message + "--->" + e.getMessage());
//////////        }
//////////    }
//////////
//////////    @KafkaListener(id = "searcher-indexer6", groupId = "es-replication-group", topicPartitions = {@TopicPartition(topic = "learning-graph-events", partitions = "5")})
//////////    public void listenSearch6(ConsumerRecord<?,?> consumerRecord) {
//////////        UUID uuid = UUID.randomUUID();
//////////        String message = String.valueOf(consumerRecord.value());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$MESSAGE OFFSET FROM SEARCH 6$$$$$$$$$$$$$$$");
//////////        System.out.println(uuid+"->"+consumerRecord.offset());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//////////        try {
//////////            List<Map<String, Object>> failures = new ArrayList<>();
//////////            List<Map<String, Object>> data = objectMapper.readValue(message, new TypeReference<Object>() {
//////////            });
//////////            failures = searchIndexer.processMessageEnvelope(data, uuid);
//////////            if (!failures.isEmpty()) {
//////////                kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, objectMapper.writeValueAsString(failures));
//////////            }
//////////        } catch (Exception e) {
//////////            e.printStackTrace();
//////////            kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, message + "--->" + e.getMessage());
//////////        }
//////////    }
//////////
//////////    @KafkaListener(id = "searcher-indexer7", groupId = "es-replication-group", topicPartitions = {@TopicPartition(topic = "learning-graph-events", partitions = "6")})
//////////    public void listenSearch7(ConsumerRecord<?,?> consumerRecord) {
//////////        UUID uuid = UUID.randomUUID();
//////////        String message = String.valueOf(consumerRecord.value());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$MESSAGE OFFSET FROM SEARCH 7$$$$$$$$$$$$$$$");
//////////        System.out.println(uuid+"->"+consumerRecord.offset());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//////////        try {
//////////            List<Map<String, Object>> failures = new ArrayList<>();
//////////            List<Map<String, Object>> data = objectMapper.readValue(message, new TypeReference<Object>() {
//////////            });
//////////            failures = searchIndexer.processMessageEnvelope(data, uuid);
//////////            if (!failures.isEmpty()) {
//////////                kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, objectMapper.writeValueAsString(failures));
//////////            }
//////////        } catch (Exception e) {
//////////            e.printStackTrace();
//////////            kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, message + "--->" + e.getMessage());
//////////        }
//////////    }
//////////
//////////
//////////    @KafkaListener(id = "searcher-indexer8", groupId = "es-replication-group", topicPartitions = {@TopicPartition(topic = "learning-graph-events", partitions = "7")})
//////////    public void listenSearch8(ConsumerRecord<?,?> consumerRecord) {
//////////        UUID uuid = UUID.randomUUID();
//////////        String message = String.valueOf(consumerRecord.value());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$MESSAGE OFFSET FROM SEARCH 8$$$$$$$$$$$$$$$");
//////////        System.out.println(uuid+"->"+consumerRecord.offset());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//////////        try {
//////////            List<Map<String, Object>> failures = new ArrayList<>();
//////////            List<Map<String, Object>> data = objectMapper.readValue(message, new TypeReference<Object>() {
//////////            });
//////////            failures = searchIndexer.processMessageEnvelope(data, uuid);
//////////            if (!failures.isEmpty()) {
//////////                kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, objectMapper.writeValueAsString(failures));
//////////            }
//////////        } catch (Exception e) {
//////////            e.printStackTrace();
//////////            kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, message + "--->" + e.getMessage());
//////////        }
//////////    }
//////////
//////////    @KafkaListener(id = "searcher-indexer9", groupId = "es-replication-group", topicPartitions = {@TopicPartition(topic = "learning-graph-events", partitions = "8")})
//////////    public void listenSearch9(ConsumerRecord<?,?> consumerRecord) {
//////////        UUID uuid = UUID.randomUUID();
//////////        String message = String.valueOf(consumerRecord.value());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$MESSAGE OFFSET FROM SEARCH 9$$$$$$$$$$$$$$$");
//////////        System.out.println(uuid+"->"+consumerRecord.offset());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//////////        try {
//////////            List<Map<String, Object>> failures = new ArrayList<>();
//////////            List<Map<String, Object>> data = objectMapper.readValue(message, new TypeReference<Object>() {
//////////            });
//////////            failures = searchIndexer.processMessageEnvelope(data, uuid);
//////////            if (!failures.isEmpty()) {
//////////                kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, objectMapper.writeValueAsString(failures));
//////////            }
//////////        } catch (Exception e) {
//////////            e.printStackTrace();
//////////            kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, message + "--->" + e.getMessage());
//////////        }
//////////    }
//////////
//////////    @KafkaListener(id = "searcher-indexer10", groupId = "es-replication-group", topicPartitions = {@TopicPartition(topic = "learning-graph-events", partitions = "9")})
//////////    public void listenSearch10(ConsumerRecord<?,?> consumerRecord) {
//////////        UUID uuid = UUID.randomUUID();
//////////        String message = String.valueOf(consumerRecord.value());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$MESSAGE OFFSET FROM SEARCH 10$$$$$$$$$$$$$$$");
//////////        System.out.println(uuid+"->"+consumerRecord.offset());
//////////        System.out.println(uuid+"->"+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//////////        try {
//////////            List<Map<String, Object>> failures = new ArrayList<>();
//////////            List<Map<String, Object>> data = objectMapper.readValue(message, new TypeReference<Object>() {
//////////            });
//////////            failures = searchIndexer.processMessageEnvelope(data, uuid);
//////////            if (!failures.isEmpty()) {
//////////                kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, objectMapper.writeValueAsString(failures));
//////////            }
//////////        } catch (Exception e) {
//////////            e.printStackTrace();
//////////            kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, message + "--->" + e.getMessage());
//////////        }
//////////    }
//
//    @KafkaListener(clientIdPrefix = "publisher", groupId = "es-replication-group", topicPartitions = {
//            @TopicPartition(topic = "publishpipeline-stage1", partitions = "0")})
//    public void listenPublish(ConsumerRecord<?, ?> consumerRecord) {
//        String message = String.valueOf(consumerRecord.value());
//        UUID uuid = UUID.randomUUID();
//        System.out.println(uuid + "$$$$$$$$$$$$$$$$MESSAGE OFFSET FROM PUBLISH$$$$$$$$$$$$$$$");
//        System.out.println(uuid + "" + consumerRecord.offset());
//        System.out.println(uuid + "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//        try {
//            Map<String, Object> data = objectMapper.readValue(message, new TypeReference<Object>() {
//            });
//            if (!publishPipeLineStage1.processMessage(data, uuid)) {
//                kafkaTemplate.send(publisherErrorTopic, publisherErrorTopicKey, message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            kafkaTemplate.send(publisherErrorTopic, publisherErrorTopicKey, message + "--->" + e.getMessage());
//        }
//    }
//
//    @KafkaListener(id = "inter-ml-search-indexer", groupId = "es-replication-group", topicPartitions = {
//            @TopicPartition(topic = "learning-graph-events", partitions = "0")})
//    public void listenMLSearch(ConsumerRecord<?, ?> consumerRecord) {
//        String message = String.valueOf(consumerRecord.value());
//        System.out.println("$$$$$$$$$$$$$$$$MESSAGE OFFSET FROM ML SEARCH$$$$$$$$$$$$$$$");
//        System.out.println(consumerRecord.offset());
//        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//        try {
//            List<Map<String, Object>> failures = new ArrayList<>();
//            List<Map<String, Object>> data = objectMapper.readValue(message, new TypeReference<Object>() {
//            });
//            failures = interMLSearchIndexer.processMessageEnvelope(data);
//            if (!failures.isEmpty()) {
//                kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey,
//                        objectMapper.writeValueAsString(failures));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            kafkaTemplate.send(searchIndexerErrorTopic, searchIndexerErrorTopicKey, message + "--->" + e.getMessage());
//        }
//    }
//
//}

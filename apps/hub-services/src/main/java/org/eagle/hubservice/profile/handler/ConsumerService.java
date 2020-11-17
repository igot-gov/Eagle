
package org.eagle.hubservice.profile.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

public class ConsumerService implements IConsumerService{
    private Logger logger = LoggerFactory.getLogger(ConsumerService.class);


    @Override
    @KafkaListener(topics = "${add.response.topic.name}", groupId = "${add.topic.group.id}", containerFactory = "customKafkaListenerContainerFactory")
    public void consumeCreateProfile(Object message) {

        //TODO: with handling validation in producer
    }

    @Override
    public void consumeUpdateProfile(Object message) {
        //TODO

    }
}

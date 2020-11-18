
/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.profile.handler;

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

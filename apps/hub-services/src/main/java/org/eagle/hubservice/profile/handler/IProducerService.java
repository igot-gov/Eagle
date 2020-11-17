/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package org.eagle.hubservice.profile.handler;

public interface IProducerService {

    /**
     * Produces any kafka event
     * @param topic     name of topic
     * @param message   message to push into the topic
     */

    public void sendMessage(String topic, Object message);
}

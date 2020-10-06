/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class NotificationEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @JsonProperty(value = "event-id")
    private String eventId;

    @Transient
    @JsonProperty(value = "tag-value-pair")
    private Map<String, Object> tagValues;

    @NotNull
    @NotEmpty
    @JsonProperty(value = "recipients")
    private Map<String, List<String>> recipients;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Map<String, Object> getTagValues() {
        return tagValues;
    }

    public void setTagValues(Map<String, Object> tagValues) {
        this.tagValues = tagValues;
    }

    public Map<String, List<String>> getRecipients() {
        return recipients;
    }

    public void setRecipients(Map<String, List<String>> recipients) {
        this.recipients = recipients;
    }
}

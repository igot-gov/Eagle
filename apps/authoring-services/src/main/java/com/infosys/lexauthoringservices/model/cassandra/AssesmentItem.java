/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.model.cassandra;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AssesmentItem {

    private String code;
    private String header;
    private String description;
    private Map<String, String> questionaryItems = new HashMap<>();


    public AssesmentItem(String header, String description, Map<String, String> questionaryItems){

        this.header = header;
        this.description = description;
        this.code = UUID.nameUUIDFromBytes(header.getBytes()).toString();
        this.questionaryItems = questionaryItems;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHeader() {
        return header;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getQuestionaryItems() {
        return questionaryItems;
    }
}

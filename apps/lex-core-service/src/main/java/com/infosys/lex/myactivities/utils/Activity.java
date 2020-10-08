/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lex.myactivities.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Activity {

    @JsonIgnore
    private String name;
    private Constants.Unit unit;
    private Object value;

    public Activity(Constants.Unit unit, Object value){
        this.unit = unit;
        this.value = value;
    }
    public Activity(String name, Constants.Unit unit, Object value){
        this.name = name;
        this.unit = unit;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Constants.Unit getUnit() {
        return unit;
    }

    public void setUnit(Constants.Unit unit) {
        this.unit = unit;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}



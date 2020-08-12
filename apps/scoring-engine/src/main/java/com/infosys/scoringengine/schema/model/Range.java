/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.scoringengine.schema.model;

import jdk.nashorn.internal.ir.annotations.Ignore;

public class Range {

    @Ignore
    private String name;
    private Integer min;
    private Integer max;

    private Integer assignedValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getAssignedValue() {
        return assignedValue;
    }

    public void setAssignedValue(Integer assignedValue) {
        this.assignedValue = assignedValue;
    }
}

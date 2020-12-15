/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.scoringengine.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.OptionalDouble;


public class MathFunction {

    public static Double sum(List<Double> doubles){

        return doubles.stream().mapToDouble(Double::doubleValue).sum();
    }

    public static OptionalDouble min(List<Double> doubles){

        return doubles.stream().mapToDouble(Double::doubleValue).min();
    }

    public static OptionalDouble max(List<Double> doubles){

        return doubles.stream().mapToDouble(Double::doubleValue).max();
    }

    public static OptionalDouble mean(List<Double> doubles){

        return doubles.stream().mapToDouble(Double::doubleValue).average();
    }

    public static Double weightedAvg(List<Double> doubles, double weigtage){

        return (doubles.stream().mapToDouble(Double::doubleValue).sum()) * weigtage;
    }

    public static Double maxWeightedAvg(Double totalMaxValue, double weigtage){
        return  totalMaxValue * weigtage;
    }

    public static Double minWeightedAvg(Double totallMinValue, double weigtage){
        return  totallMinValue * weigtage;
    }

    public static Double maxWeightedAvg(List<Double> doubles, double weigtage){

        OptionalDouble maxValue = doubles.stream().mapToDouble(Double::doubleValue).max() ;
        double count = doubles.stream().mapToDouble(Double::doubleValue).count();
        return  (maxValue.getAsDouble() * count) * weigtage;
    }

    public static Double minWeightedAvg(List<Double> doubles, double weigtage){

        OptionalDouble maxValue = doubles.stream().mapToDouble(Double::doubleValue).max() ;
        double count = doubles.stream().mapToDouble(Double::doubleValue).count();
        return  (maxValue.getAsDouble() * count) * weigtage;
    }

    public static Double weightedScore(double totalValue, double maxvalue, double weigtage) {
        if (maxvalue == 0)
            return 0.0;
        return (totalValue / maxvalue) * weigtage * 100;
    }






}

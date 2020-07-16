package com.infosysit.rainforest.services;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * Created by jithilprakash.pj on 10/17/2018.
 */

public class TestRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCases3.class);
        Result result1 = JUnitCore.runClasses(TestCases4.class);
    }

}

package com.infosysit.sdk.services;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by jithilprakash.pj on 10/3/2018.
 */
public class ContentSQLServiceTest {
    @Test
    public void deleteDir() throws Exception {
        File file = new File("/");
        assertTrue(ContentSQLService.deleteDir(file));
    }

}
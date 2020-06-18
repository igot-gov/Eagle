package com.infosysit.sdk.services;

import java.io.File;


/**
 * Created by akansha.goyal on 3/27/2018.
 */

public class ContentSQLService {


    public static boolean deleteDir(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
//        return false;
    }

//    public static List<String> getAllSuccessfullDownloads(){
//        SQLiteHelperJava database = new SQLiteHelperJava(sContext);
////        List<String> successfullResource = database.successfullDownloadedContent();
//        List<String> successfullResource = new ArrayList<>();
//        return successfullResource;
//    }
}

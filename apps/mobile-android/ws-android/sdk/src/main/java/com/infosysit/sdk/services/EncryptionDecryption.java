package com.infosysit.sdk.services;

import android.content.Context;

import android.util.Log;

import com.infosysit.sdk.UtilityJava;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by akansha.goyal on 3/21/2018.
 */

public class EncryptionDecryption {

    private static final int CHUNK_SIZE = 4096;

    private static byte[] convertToByte(String inputPath, String inputFile, Context context) {
        File toRead = context.getExternalFilesDir(inputPath+inputFile);
//    File toRead = new File(file, inputFile);

    byte[] bytes = null;
    if(toRead.exists()){
        int size = (int) toRead.length();
        bytes = new byte[size];
        byte tmpBuff[] = new byte[size];
        try (FileInputStream fis = new FileInputStream(toRead)) {
            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } catch (IOException e) {
            Log.e("EncryptionDecryption", e.getMessage());
            return null;
        }
    }
    return bytes;

}





    public static void encryptData(String key, String inputPath, String inputFile, String outputPath, String outputFile, Context context) throws Exception {
//        Log.d("encryptData",fileName);
            Log.d("encryptdata", "input path " + inputPath);
            Log.d("encryptdata", "input file " + inputFile);
            byte[] b = convertToByte(inputPath, inputFile,context);
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] digestOfPassword = md.digest(key.getBytes(StandardCharsets.UTF_16LE));

            SecretKeySpec skeySpec = new SecretKeySpec(digestOfPassword, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(b);
            saveInFile(encrypted, outputPath, outputFile,context);

            // delete original file after encryption
        File file = new File(context.getExternalFilesDir(""), inputPath);
            File toDelete = new File(file, inputFile);

            if (toDelete.exists()) {
                toDelete.delete();
            }
        }




        public static void decryptDataOpenRap(String key, String inputPath, String inputFile, String outputPath, String outputFile, Context context) {
            File file = new File(context.getExternalFilesDir(""), inputPath);
            File toRead = new File(file, inputFile);
            File oFile = new File(context.getExternalFilesDir(""), outputPath);
            File toWrite = new File(oFile, outputFile);
            Log.d("JsonObject","PAth: "+oFile.getPath());

            if (!oFile.exists()) {
                oFile.mkdirs();
            }

            byte[] buffer = new byte[CHUNK_SIZE];
            try(FileInputStream fis = new FileInputStream(toRead); FileOutputStream fos = new FileOutputStream(toWrite)) {
                int read = 0;
                int offset = read;
                MessageDigest md = MessageDigest.getInstance("md5");
                byte[] digestOfPassword = md.digest(key.getBytes(StandardCharsets.UTF_16LE));
//            Log.d("EncryptionDecryption", "lenght of enc data " + encryptedData.length);
                SecretKeySpec skeySpec = new SecretKeySpec(digestOfPassword, "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
                CipherInputStream in = new CipherInputStream(fis, cipher);
                Log.d("Decrypted", "filesize " + toRead.length());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((read = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                    offset += read;
                }
                fos.flush();

                if (outputFile.endsWith(".zip"))
                    UtilityJava.unzipFile(outputFile,context);

            }
            catch (Exception e) {
                Log.e("Decryption", e.getMessage());
            }
        }

        private static void saveInFile (byte[] dataToSave , String outputPath , String outputFile, Context context){
            File dir = context.getExternalFilesDir(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File toWrite = new File(dir, outputFile);

            try (FileOutputStream fos = new FileOutputStream(toWrite)) {
                fos.write(dataToSave);
            } catch (IOException e) {
                Log.e("EncryptionDecryption", e.getMessage());
            }
        }
}
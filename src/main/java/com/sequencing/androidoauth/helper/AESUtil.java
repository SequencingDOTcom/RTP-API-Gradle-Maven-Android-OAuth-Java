package com.sequencing.androidoauth.helper;

/**
 * Created by omazurova on 3/23/2017.
 */

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AESUtil{

    private static final String ENCRYPTION_IV = "3n3CrwwnzMqxOssv";

    public static String encrypt(String src, String clientId) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, makeKey(clientId), makeIv());
            return Base64.encodeBytes(cipher.doFinal(src.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String src, String clientId) {
        String decrypted = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, makeKey(clientId), makeIv());
            decrypted = new String(cipher.doFinal(Base64.decode(src)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return decrypted;
    }

    static AlgorithmParameterSpec makeIv() {
        try {
            return new IvParameterSpec(ENCRYPTION_IV.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Key makeKey(String clientId) {
        try {
            byte[] key = clientId.getBytes("UTF-8");
            if( key.length > 32 ) key = Arrays.copyOfRange (key, 0, 32);
            return new SecretKeySpec(key, "AES");
        }  catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
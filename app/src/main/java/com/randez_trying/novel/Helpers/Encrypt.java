package com.randez_trying.novel.Helpers;

import android.annotation.SuppressLint;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {

    public static String encode(String s, String key) {
        try {
            SecretKeySpec keySpec = genPassword(key);
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encValue = cipher.doFinal(s.getBytes());
            return java.util.Base64.getEncoder().encodeToString(encValue);
        } catch (Exception e) {
            return s;
        }
    }

    public static String decode(byte[] bytes, String key) {
        try {
            String s = new String(bytes);
            SecretKeySpec keySpec = genPassword(key);
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] toFirst = java.util.Base64.getDecoder().decode(s.getBytes());
            return new String(cipher.doFinal(toFirst));
        } catch (Exception e) {
            return new String(bytes);
        }
    }

    private static SecretKeySpec genPassword(String key) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
        digest.update(bytes, 0, bytes.length);
        byte[] newKey = digest.digest();
        return new SecretKeySpec(newKey, "AES");
    }

    public static String md5(String string) {
        try {
            byte[] bytes = string.getBytes();
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(bytes);
            BigInteger bigInteger = new BigInteger(1, digest.digest());
            return bigInteger.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
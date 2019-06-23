package com.example.springboot.controller;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by yixian on 2016-04-28.
 */
public class PasswordUtils {
    private static final SecretKey AES_KEY = new SecretKeySpec(Base64.decodeBase64("i35raT1etyA8SDrN5NjjkXVYanQBAZbzuAKjpRTj4og"), "AES");
    private static byte[] AES_IV = StringUtils.getBytesUtf8("xdY=+%f4@b!k2aTV");

    public static String hashPwd(String originalPwd, String salt) {
        return Base64.encodeBase64String(DigestUtils.sha256(salt + originalPwd));
    }

    public static String newSalt() {
        return RandomStringUtils.random(30, 0, 0, true, true, null, new SecureRandom());
    }

    public static String encryptAESPwd(String pwd) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, AES_KEY,
                    new IvParameterSpec(AES_IV));
            int saltLen = RandomUtils.nextInt(0, 16 - 4) + 4;
            byte[] saltBytes = new byte[saltLen];
            new SecureRandom().nextBytes(saltBytes);
            saltBytes[0] = (byte) ((saltBytes[0] & 0xfc) | (saltLen & 0x03));
            saltBytes[1] = (byte) ((saltBytes[1] & 0xf3) | (saltLen & 0x0c));
            saltBytes[2] = (byte) ((saltBytes[2] & 0xcf) | (saltLen & 0x30));
            saltBytes[3] = (byte) ((saltBytes[3] & 0x3f) | (saltLen & 0xc0));
            byte[] encrypted = cipher.doFinal(ArrayUtils.addAll(saltBytes, StringUtils.getBytesUtf8(pwd)));
            return Base64.encodeBase64String(encrypted);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException ignored) {
            ignored.printStackTrace();
            return "";
        }
    }

    public static String decryptAESPwd(String aesPwd) {
        if (aesPwd != null && aesPwd.length() > 0) {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.DECRYPT_MODE, AES_KEY,
                        new IvParameterSpec(AES_IV));
                byte[] encryptedBytes = Base64.decodeBase64(aesPwd);
                byte[] decrypted = cipher.doFinal(encryptedBytes);
                int saltLength = (decrypted[0] & 0x03) | (decrypted[1] & 0x0c) | (decrypted[2] & 0x30) | (decrypted[3] & 0xc0);
                return StringUtils.newStringUtf8(ArrayUtils.subarray(decrypted, saltLength, decrypted.length + 1));
            } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException ignored) {
            }
        }
        return null;
    }
}

package com.example.springboot.test;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

public class TestRSA {

    public static byte[] decryptByPrivateKey(PrivateKey privateKey, byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(2, privateKey);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;

        for(int i = 0; inputLen - offSet > 0; offSet = i * 256) {
            byte[] cache;
            if(inputLen - offSet > 256) {
                cache = cipher.doFinal(encryptedData, offSet, 256);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }

            out.write(cache, 0, cache.length);
            ++i;
        }

        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    public static byte[] encryptByPublicKey(PublicKey publicKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(2048, publicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;

        for(int i = 0; inputLen - offSet > 0; offSet = i * 244) {
            byte[] cache;
            if(inputLen - offSet > 244) {
                cache = cipher.doFinal(data, offSet, 244);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }

            out.write(cache, 0, cache.length);
            ++i;
        }

        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }
}

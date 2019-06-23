package com.example.springboot.test;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * Created by humf.需要依赖 commons-codec 包 
 */
public class RSACoder {

    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    public static byte[] decryptBASE64(String key) {
        return Base64.decodeBase64(key);
    }

    public static String encryptBASE64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    /** *//**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /** *//**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 256;
    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       加密数据
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        // 解密由base64编码的私钥
        byte[] keyBytes = decryptBASE64(privateKey);
        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data);
        return encryptBASE64(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param data      加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return 校验成功返回true 失败返回false
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        // 解密由base64编码的公钥
        byte[] keyBytes = decryptBASE64(publicKey);
        // 构造X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 取公钥匙对象
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data);
        // 验证签名是否正常
        return signature.verify(decryptBASE64(sign));
    }

    public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception{
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 解密<br>
     * 用私钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(String data, String key)
            throws Exception {
        return decryptByPrivateKey(decryptBASE64(data),key);
    }

    /**
     * 解密<br>
     * 用公钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, String key)
            throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 加密<br>
     * 用公钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(String data, String key)
            throws Exception {
        // 对公钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }

    /**
     * 加密<br>
     * 用私钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key)
            throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 取得私钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Key> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return encryptBASE64(key.getEncoded());
    }

    /**
     * 取得公钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Key> keyMap)
            throws Exception {
        Key key = keyMap.get(PUBLIC_KEY);
        return encryptBASE64(key.getEncoded());
    }

    /**
     * 初始化密钥
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Key> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        Map<String, Key> keyMap = new HashMap(2);
        keyMap.put(PUBLIC_KEY, keyPair.getPublic());// 公钥
        keyMap.put(PRIVATE_KEY, keyPair.getPrivate());// 私钥
        return keyMap;
    }

    public static void main(String[] args) throws Exception {
        Map<String, Key> keyMap = initKey();
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0tSdvOjDBZLzB5tWpcpYVNRK0DHmPeTEmf67rWU37aPMH3i/caozIGXapPjaqfgS2Bymp8Mr/hKVSHZT3HvaHYLSI0QMiFvpuhv5mWHgOCrunbULiVSjuR/7oJH8DSwd/T1SE+LBJFxq+8lFM92top/zG9fdxe3gcvHnxUdRLPx/OyDfSlNSnA+qoOISTMiUSxvFYf3PdXkJZy0txgzX+70Ak5y4pIcH3oI0siS0hJQP08Z/MduPseNtV+JjB8GDla+SjqantnfLbLu44UqVhSZ279B7zhMZPSpq5/cZIfWbgXkso7Ge9dxA3WmZYZRbP3QIi9+odTOaHPTkZGPTgwIDAQAB";
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDS1J286MMFkvMHm1alylhU1ErQMeY95MSZ/rutZTfto8wfeL9xqjMgZdqk+Nqp+BLYHKanwyv+EpVIdlPce9odgtIjRAyIW+m6G/mZYeA4Ku6dtQuJVKO5H/ugkfwNLB39PVIT4sEkXGr7yUUz3a2in/Mb193F7eBy8efFR1Es/H87IN9KU1KcD6qg4hJMyJRLG8Vh/c91eQlnLS3GDNf7vQCTnLikhwfegjSyJLSElA/Txn8x24+x421X4mMHwYOVr5KOpqe2d8tsu7jhSpWFJnbv0HvOExk9Kmrn9xkh9ZuBeSyjsZ713EDdaZlhlFs/dAiL36h1M5oc9ORkY9ODAgMBAAECggEAMFg443ysW7baq7/fGp1PrAQSM7N9jGvM+VMETjqLnJ6WzBCwNaxFlQRGItY9rgQIri3DuWxzxgsU2Ezp5gEwftvku9l61jndWqPXlGhxNfACT3+YaaFS+bxvwt424f5f0hYhoBW7HE9k6N+6Uq5ehpAO+y+A10Y41aLK9t3nq1zAbxum/h1cWBzjjJ6W9KOYt/RTlxJAieUlrwN7XbP2GPFutN9FRGc7t7QAJfHAygMv3uhFfuHy01EGcvKJK7wgquab6s5NmWfS3w0Fesm/tHXy3q5lcUTnBxL9hrIGC5mW/cWMWkXvK5dBoSLVnF06RiioIdz3Hh18BtxdJsgvAQKBgQDs4u5ri8BbUnyBAOkZzeCL4ypwmnr/zMRxsCH9nBB8j1vUG5kO9XPsn4CTiHWq+L4TXSX9F49NZr+aW8xB0udVdRQfTAvCpspKEqWvL8ZldZj8QVoVClWH+4cfwWjDtqDBv9GEab7hFxhanzCCy4Z4Doa1TI829jrOCNg5E7HZYwKBgQDj13rqr5PPVSM3Fz+SBu/7Zic8vIJXQQldhWsWhfxK2Z17rP4+TTfDbrVzv7HI0VaraO20EDkDuE1KgHb0MhrFATUOhqN5M5Ah6Fz2RuQnXpD3Os7pmR0Gl8zHv72/Af55SCctlA7KYcGP1eTAvnsR8fwBGKkTeqs3zRqf81JHYQKBgQDpTe+HmFJRHwJkg6MQuQWogIlx8ZOmj+QCHHi9sJFEPGUymCO86d3MdKNLqqBJTUbXB2y8whFvJ/0ZZ8PaKH+SWQcCdAE5KtAc+BSaX7FEh/euqiawhkcK8J16lyA5cYsOh38wrH1Eb1ybFCHRD+ZWI+hkcwyyEvyDo45hWJZXBQKBgFa6qKR7T2Wl//CxndZFY8ar3RXGoEr8i8cYyWnaeO45JjR05pbqp+Zx1wqHlvJdlYNMPufDISDkYC+S9TUBeaUyT3MHF2VEloIBxJlO5PyDh+VEco0DZsQitqX2vBDgfnDGYXt9F6A8VAUfo6MnHwv2tDnqnDm5AUINhiwK4HAhAoGBALH8vxXfdqD3o7uRgg/JTM9hhiguK6mX/UDd2TQhybL3w6oEpHS4HdpNk+sOJA1Xf0HIc82AEqaUSxfA9mZsF/MByldNWM5LXDC/qiR7Nj6kfulgb22XtEJ3cA9dKo7KBNkRg1VcoRoB05Q7hIJOLocwjvbSNlOKFHWAsUQJ/MHP";

        System.out.println(keyMap);
        System.out.println("-----------------------------------");
        System.out.println(publicKey);
        System.out.println("-----------------------------------");
        System.out.println(privateKey);
        System.out.println("-----------------------------------");
        byte[] encryptByPrivateKey = encryptByPrivateKey("{\"sysCode\":\"P AYJP\",\"op\":\"U\",\"loginName\":\"NM35\",\"roles\":[{\"roleDesc\":\"无\",\"roleCode\":\"admin\",\"roleName\":\"系统管理员\",\"subsysRoleId\":\"1\"},{\"roleDesc\":\" \",\"roleCode\":\"opra\",\"roleName\":\"运营人员\",\"subsysRoleId\":\"2\"}],\"operatorId\":7}".getBytes(),privateKey);
//        byte[] encryptByPublicKey = encryptByPublicKey("{\"sysCode\":\"P AYJP\",\"op\":\"U\",\"loginName\":\"NM35\",\"roles\":[{\"roleDesc\":\"无\",\"roleCode\":\"admin\",\"roleName\":\"系统管理员\",\"subsysRoleId\":\"1\"},{\"roleDesc\":\" \",\"roleCode\":\"opra\",\"roleName\":\"运营人员\",\"subsysRoleId\":\"2\"}],\"operatorId\":7}",publicKey);
        byte[] encryptByPublicKey = encryptByPublicKey("{\"sysCode\":\"P AYJP\",\"op\":\"U\",\"loginName\":\"NM35\",\"roles\":[{\"roleDesc\":\"无\",\"roleCode\":\"admin\",\"roleName\":\"系统管理员\",\"subsysRoleId\":\"1\"},{\"roleDesc\":\" \",\"roleCode\":\"opra\",\"roleName\":\"运营人员\",\"subsysRoleId\":\"2\"}],\"operatorId\":7}".getBytes(), publicKey);
        System.out.println(new String(encryptByPrivateKey));
        System.out.println("-----------------------------------");
        System.out.println(new String(encryptByPublicKey));
        System.out.println("-----------------------------------");
        String sign = sign(encryptByPrivateKey,privateKey);
        System.out.println(sign);
        System.out.println("-----------------------------------");
        boolean verify = verify(encryptByPrivateKey,publicKey,sign);
        System.out.println(verify);
        System.out.println("-----------------------------------");
        byte[] decryptByPublicKey = decryptByPublicKey(encryptByPrivateKey,publicKey);
        byte[] decryptByPrivateKey = decryptByPrivateKey1(encryptByPublicKey,privateKey);
        System.out.println("------------------decryptByPublicKey-----------------");
        System.out.println(new String(decryptByPublicKey));
        System.out.println("------------------decryptByPrivateKey-----------------");
        System.out.println(new String(decryptByPrivateKey));

    }

    public static byte[] decryptByPrivateKey1(byte[] encryptedData, String privateKey)
            throws Exception {
        byte[] keyBytes = decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    public static byte[] decode(String base64) throws Exception {
        return Base64.decodeBase64(base64.getBytes());
    }

    /** *//**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data 源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)
            throws Exception {
        byte[] keyBytes = decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

}
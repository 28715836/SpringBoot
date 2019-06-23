package com.example.springboot.test;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

public class RSAGenerator {

    private String ALGORITHM_RSA = "RSA";
    private String DEFAULT_ENCODING = "UTF-8";

    public static final String KEY_TYPE_PUBLIC = "PUBLIC";
    public static final String KEY_TYPE_PRIVATE = "PRIVATE";

    private RSAPublicKey publicKey;

    private String publicKeyStr;

    /**
     * 私钥
     */
    private RSAPrivateKey privateKey;

    private String privateKeyStr;

    /**
     * 用于加解密
     */
    private Cipher cipher;

    /**
     * 明文块的长度 它必须小于密文块的长度 - 11
     */
    private int originLength = 128;
    /**
     * 密文块的长度
     */
    private int encrytLength = 256;

    public String encryptByPublic(String content, RSAPublicKey publicKey) {
        String encode = "";
        try {
            cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 该密钥能够加密的最大字节长度
            int splitLength = publicKey.getModulus().bitLength() / 8 - 11;
            byte[][] arrays = splitBytes(content.getBytes(), splitLength);
            // 加密
            StringBuffer buffer = new StringBuffer();
            for (byte[] array : arrays) {
                buffer.append(bytesToHexString(cipher.doFinal(array)));
            }
            encode = buffer.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return encode;
    }

    /**
     * 用私钥解密
     * @param content
     * @return 解密后的原文
     */
    public String decryptByPrivate(String content, RSAPrivateKey privateKey) {
        String decode = "";
        try {
            cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // 该密钥能够加密的最大字节长度
            int splitLength = privateKey.getModulus().bitLength() / 8;
            byte[] contentBytes = hexStringToBytes(content);

            byte[][] arrays = splitBytes(contentBytes, splitLength);
            StringBuffer stringBuffer = new StringBuffer();
            for (byte[] array : arrays) {
                stringBuffer.append(new String(cipher.doFinal(array)));
            }
            decode = stringBuffer.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return decode;
    }

    public static byte[][] splitBytes(byte[] bytes, int splitLength) {
        // bytes与splitLength的余数
        int remainder = bytes.length % splitLength;
        // 数据拆分后的组数，余数不为0时加1
        int quotient = remainder > 0 ? bytes.length / splitLength + 1
                : bytes.length / splitLength;
        byte[][] arrays = new byte[quotient][];
        byte[] array = null;
        for (int i = 0; i < quotient; i++) {
            // 如果是最后一组（quotient-1）,同时余数不等于0，就将最后一组设置为remainder的长度
            if (i == quotient - 1 && remainder != 0) {
                array = new byte[remainder];
                System.arraycopy(bytes, i * splitLength, array, 0, remainder);
            } else {
                array = new byte[splitLength];
                System.arraycopy(bytes, i * splitLength, array, 0, splitLength);
            }
            arrays[i] = array;
        }
        return arrays;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(0xFF & bytes[i]);
            if (temp.length() < 2) {
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    public static byte[] hexStringToBytes(String hex) {
        int len = (hex.length() / 2);
        hex = hex.toUpperCase();
        byte[] result = new byte[len];
        char[] chars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(chars[pos]) << 4 | toByte(chars[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static void main(String[] args) throws InvalidKeySpecException {
        PublicKey publicKey = RSACrypt.resolvePublic(Base64.decodeBase64("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0tSdvOjDBZLzB5tWpcpYVNRK0DHmPeTEmf67rWU37aPMH3i/caozIGXapPjaqfgS2Bymp8Mr/hKVSHZT3HvaHYLSI0QMiFvpuhv5mWHgOCrunbULiVSjuR/7oJH8DSwd/T1SE+LBJFxq+8lFM92top/zG9fdxe3gcvHnxUdRLPx/OyDfSlNSnA+qoOISTMiUSxvFYf3PdXkJZy0txgzX+70Ak5y4pIcH3oI0siS0hJQP08Z/MduPseNtV+JjB8GDla+SjqantnfLbLu44UqVhSZ279B7zhMZPSpq5/cZIfWbgXkso7Ge9dxA3WmZYZRbP3QIi9+odTOaHPTkZGPTgwIDAQAB"));
        RSAGenerator rsaGenerator = new RSAGenerator();
        String str="{\"sysCode\":\"P AYJP\",\"op\":\"U\",\"loginName\":\"NM35\",\"roles\":[{\"roleDesc\":\"无\",\"roleCode\":\"admin\",\"roleName\":\"系统管理员\",\"subsysRoleId\":\"1\"},{\"roleDesc\":\" \",\"roleCode\":\"opra\",\"roleName\":\"运营人员\",\"subsysRoleId\":\"2\"}],\"operatorId\":7}";
        String encode = rsaGenerator.encryptByPublic(str, (RSAPublicKey) publicKey);
        System.out.println(encode);
        PrivateKey privateKey = RSACrypt.resolvePrivate(Base64.decodeBase64("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDS1J286MMFkvMHm1alylhU1ErQMeY95MSZ/rutZTfto8wfeL9xqjMgZdqk+Nqp+BLYHKanwyv+EpVIdlPce9odgtIjRAyIW+m6G/mZYeA4Ku6dtQuJVKO5H/ugkfwNLB39PVIT4sEkXGr7yUUz3a2in/Mb193F7eBy8efFR1Es/H87IN9KU1KcD6qg4hJMyJRLG8Vh/c91eQlnLS3GDNf7vQCTnLikhwfegjSyJLSElA/Txn8x24+x421X4mMHwYOVr5KOpqe2d8tsu7jhSpWFJnbv0HvOExk9Kmrn9xkh9ZuBeSyjsZ713EDdaZlhlFs/dAiL36h1M5oc9ORkY9ODAgMBAAECggEAMFg443ysW7baq7/fGp1PrAQSM7N9jGvM+VMETjqLnJ6WzBCwNaxFlQRGItY9rgQIri3DuWxzxgsU2Ezp5gEwftvku9l61jndWqPXlGhxNfACT3+YaaFS+bxvwt424f5f0hYhoBW7HE9k6N+6Uq5ehpAO+y+A10Y41aLK9t3nq1zAbxum/h1cWBzjjJ6W9KOYt/RTlxJAieUlrwN7XbP2GPFutN9FRGc7t7QAJfHAygMv3uhFfuHy01EGcvKJK7wgquab6s5NmWfS3w0Fesm/tHXy3q5lcUTnBxL9hrIGC5mW/cWMWkXvK5dBoSLVnF06RiioIdz3Hh18BtxdJsgvAQKBgQDs4u5ri8BbUnyBAOkZzeCL4ypwmnr/zMRxsCH9nBB8j1vUG5kO9XPsn4CTiHWq+L4TXSX9F49NZr+aW8xB0udVdRQfTAvCpspKEqWvL8ZldZj8QVoVClWH+4cfwWjDtqDBv9GEab7hFxhanzCCy4Z4Doa1TI829jrOCNg5E7HZYwKBgQDj13rqr5PPVSM3Fz+SBu/7Zic8vIJXQQldhWsWhfxK2Z17rP4+TTfDbrVzv7HI0VaraO20EDkDuE1KgHb0MhrFATUOhqN5M5Ah6Fz2RuQnXpD3Os7pmR0Gl8zHv72/Af55SCctlA7KYcGP1eTAvnsR8fwBGKkTeqs3zRqf81JHYQKBgQDpTe+HmFJRHwJkg6MQuQWogIlx8ZOmj+QCHHi9sJFEPGUymCO86d3MdKNLqqBJTUbXB2y8whFvJ/0ZZ8PaKH+SWQcCdAE5KtAc+BSaX7FEh/euqiawhkcK8J16lyA5cYsOh38wrH1Eb1ybFCHRD+ZWI+hkcwyyEvyDo45hWJZXBQKBgFa6qKR7T2Wl//CxndZFY8ar3RXGoEr8i8cYyWnaeO45JjR05pbqp+Zx1wqHlvJdlYNMPufDISDkYC+S9TUBeaUyT3MHF2VEloIBxJlO5PyDh+VEco0DZsQitqX2vBDgfnDGYXt9F6A8VAUfo6MnHwv2tDnqnDm5AUINhiwK4HAhAoGBALH8vxXfdqD3o7uRgg/JTM9hhiguK6mX/UDd2TQhybL3w6oEpHS4HdpNk+sOJA1Xf0HIc82AEqaUSxfA9mZsF/MByldNWM5LXDC/qiR7Nj6kfulgb22XtEJ3cA9dKo7KBNkRg1VcoRoB05Q7hIJOLocwjvbSNlOKFHWAsUQJ/MHP=="));
        System.out.println(rsaGenerator.decryptByPrivate(encode, (RSAPrivateKey) privateKey));
    }
}

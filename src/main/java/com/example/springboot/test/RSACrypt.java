package com.example.springboot.test;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.JCERSAPublicKey;
import org.springframework.web.server.ServerErrorException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSACrypt {
    private static Provider securityProvider = new BouncyCastleProvider();

    /**
     * 生成RSA密钥对
     *
     * @return RSA密钥对
     */
    public static KeyPair generateKeyPairs() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", securityProvider);
            gen.initialize(2048);
            return gen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            //shall never happens
            throw new RuntimeException(e);
        }
    }

    public static PrivateKey loadPrivateKey(InputStream ins) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
            String line;
            StringBuilder certBuffer = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-")) {
                    continue;
                }
                certBuffer.append(line);
            }
            return resolvePrivate(Base64.decodeBase64(certBuffer.toString()));
        } catch (IOException | InvalidKeySpecException e) {
            throw new ServerErrorException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(ins);
        }
    }

    public static PublicKey loadPublicKey(InputStream ins) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
            String line;
            StringBuilder certBuffer = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-")) {
                    continue;
                }
                certBuffer.append(line).append("\r");
            }
            return resolvePublic(Base64.decodeBase64(certBuffer.toString()));
        } catch (IOException | InvalidKeySpecException e) {
            throw new ServerErrorException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(ins);
        }
    }

    /**
     * 根据16进制表达的密钥字符串生成私钥对象
     *
     * @param keyHex 16进制表达的RSA私钥
     * @return RSA私钥
     * @throws DecoderException        字符串不是16进制数
     * @throws InvalidKeySpecException 非法密钥
     */
    public static PrivateKey resolvePrivateHex(String keyHex) throws DecoderException, InvalidKeySpecException {
        return resolvePrivate(Hex.decodeHex(keyHex.toCharArray()));
    }

    public static PrivateKey resolvePrivate(byte[] bytes) throws InvalidKeySpecException {
        PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(bytes);
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA", securityProvider);
            return factory.generatePrivate(pkcs8);
        } catch (NoSuchAlgorithmException e) {
            //Shall Never happens
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据16进制表达的密钥字符串生成公钥对象
     *
     * @param keyHex 16进制表达的RSA公钥
     * @return RSA公钥
     * @throws DecoderException        字符串不是16进制数
     * @throws InvalidKeySpecException 非法密钥
     */
    public static PublicKey resolvePublicHex(String keyHex) throws DecoderException, InvalidKeySpecException {
        return resolvePublic(Hex.decodeHex(keyHex.toCharArray()));
    }

    public static PublicKey resolvePublic(byte[] bytes) throws InvalidKeySpecException {
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(bytes);
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA", securityProvider);
            return factory.generatePublic(x509);
        } catch (NoSuchAlgorithmException e) {
            //Shall Never Happense
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] fixResultFromJS(byte[] raw) {
        int zeroIndex = ArrayUtils.indexOf(raw, (byte) 0) + 1;
        return ArrayUtils.subarray(raw, zeroIndex, raw.length);
    }

    /**
     * 将密钥序列化为16进制字符串
     *
     * @param key 密钥对象
     * @return 16进制字符串
     */
    public static String getKeyHex(Key key) {
        return Hex.encodeHexString(key.getEncoded());
    }

    public static String getKeyB64(Key key) {
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 将公钥序列化为16进制字符串(为前端js rsa库做的适配)
     *
     * @param key 公钥对象
     * @return 16进制字符串
     */
    public static String getKeyHexOfJS(PublicKey key) {
        JCERSAPublicKey jce = (JCERSAPublicKey) key;
        return jce.getModulus().toString(16);
    }

    public static byte[] encrypt(byte[] source, Key key) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        return doCrypt(source, Cipher.ENCRYPT_MODE, key);
    }

    public static byte[] decrypt(byte[] input, Key key) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        return doCrypt(input, Cipher.DECRYPT_MODE, key);
    }

    private static byte[] doCrypt(byte[] input, int mode, Key key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", securityProvider);
            cipher.init(mode, key);
            return cipher.doFinal(input);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            //Shall Never happen
            e.printStackTrace();
            return new byte[0];
        }
    }
}

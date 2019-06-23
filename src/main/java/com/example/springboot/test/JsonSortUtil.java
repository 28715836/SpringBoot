package com.example.springboot.test;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class JsonSortUtil {

/** *//**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;

	/** *//**
	 * RSA最大解密密文大小
	 */
	private static final int MAX_DECRYPT_BLOCK = 256;

	public static final String KEY_ALGORITHM = "RSA";

	@SuppressWarnings("all")
	public static JSONObject sortJsonObject(JSONObject obj) {
		Map map = new TreeMap();
		Iterator<String> it =  obj.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object value = obj.get(key);
			if (value instanceof JSONObject) {
				map.put(key, sortJsonObject(JSONObject.parseObject(value.toString())));
			} else if (value instanceof JSONArray) {
				map.put(key, sortJsonArray(JSONArray.parseArray(value.toString())));
			} else {
				map.put(key, value);
			}
		}
		JSONObject sortJSON = new JSONObject(true);
		sortJSON.putAll(map);
		return sortJSON;
	}

	/**
	 * JSONArray排序
	 *
	 * @param array
	 * @return
	 */
	@SuppressWarnings("all")
	public static JSONArray sortJsonArray(JSONArray array) {
		List list = new ArrayList();
		int size = array.size();
		for (int i = 0; i < size; i++) {
			Object obj = array.get(i);
			if (obj instanceof JSONObject) {
				list.add(sortJsonObject(JSONObject.parseObject(obj.toString())));
			} else if (obj instanceof JSONArray) {
				list.add(sortJsonArray(JSONArray.parseArray(obj.toString())));
			} else {
				list.add(obj);
			}
		}
		list.sort((o1, o2) -> o1.toString().compareTo(o2.toString()));
		return JSONArray.parseArray(JSON.toJSONString(list));
	}

	public static String jsonStr(JSONObject obj) {
		StringBuilder sb = new StringBuilder();
		return strJsonObj(obj, sb);
	}

	private static String strJsonArray(JSONArray array, StringBuilder sb) {
		for (Object obj : array) {
			if (obj instanceof JSONObject) {
				strJsonObj(JSONObject.parseObject(obj.toString()), sb);
			} else if (obj instanceof JSONArray) {
				strJsonArray(JSONArray.parseArray(obj.toString()), sb);
			} else {
				sb.append(obj);
			}
		}
		return sb.toString();
	}

	private static String strJsonObj(JSONObject obj, StringBuilder sb) {
		for (String key : obj.keySet()) {
			Object value = obj.get(key);
			if (value instanceof JSONObject) {
				sb.append(key);
				strJsonObj(JSONObject.parseObject(value.toString()), sb);
			} else if (value instanceof JSONArray) {
				sb.append(key);
				strJsonArray(JSONArray.parseArray(value.toString()), sb);
			} else {
				sb.append(key);
				sb.append(value);
			}
		}
		return sb.toString();
	}

	public static HashMap<String, String> getKeys() throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
		KeyPairGenerator keyPairGen = null;
		try {
			keyPairGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 初始化密钥对生成器，密钥大小为96-1024位
		keyPairGen.initialize(1024, new SecureRandom());
		// 生成一个密钥对，保存在keyPair中
		KeyPair keyPair = keyPairGen.generateKeyPair();
		// 得到公钥字符串
		String publicKey = Base64Utils.encode(keyPair.getPublic().getEncoded());
		// 得到私钥字符串
		String privateKey =  Base64Utils.encode(keyPair.getPrivate().getEncoded());
		map.put("publicKey", publicKey);
		map.put("privateKey", privateKey);
		return map;
	}

	public static void main(String args[]) throws Exception {
		JSONObject json = new JSONObject();
		json.put("deliveryTermCount", null);
		System.out.println(json.getIntValue("deliveryTermCount"));
		/*StringBuilder sb = new StringBuilder();
		String data = "[{\"merchantId\":\"1234\",\"merAdmissionId\":\"pine\",\"parentMerchantId\":\"333\",\"merAdmissionStatus\":\"01\",\"merPayConfig\":{\"wechatMerchantId\":\"1234\",\"alipayOfflinePid\":\"pine\",\"alipaySubMerchantId\":\"333\",\"maxOrderAmt\":\"01\"}},{\"merchantId\":\"3123\",\"merAdmissionId\":\"pine\",\"parentMerchantId\":\"333\",\"merAdmissionStatus\":\"01\",\"merPayConfig\":{\"wechatMerchantId\":\"1234\",\"alipayOfflinePid\":\"pine\",\"alipaySubMerchantId\":\"333\",\"maxOrderAmt\":\"01\"}},{\"merchantId\":\"312\",\"merAdmissionId\":\"12\",\"parentMerchantId\":\"33eqweq3\",\"merAdmissionStatus\":\"01\",\"merPayConfig\":{\"wechatMerchantId\":\"1234\",\"alipayOfflinePid\":\"pine\",\"alipaySubMerchantId\":\"333\",\"maxOrderAmt\":\"01\"}}]";
//		String data = "{\"a\": \"1\", 	\"c\": \"2}]]]\", 	\"d\": { 		\"z\": \"2\",\"a\": \"2\",\"g\": \"2\", \"h\": \"2\" 	}, 	\"b\": [{ 		\"roleName\": \"bd_user\", 		\"roleCode\": \"1\" 	}, { 		\"roleName\": \"cdmin\", 		\"roleCode\": \"1\" 	}, 	\"zest\",	\"test\", { 		\"roleName\": \"bd_user\", 		\"roleCode\": \"2\" 	}] }";
		JSONArray array = JSONObject.parseArray(data);
		System.out.println("排序后的结果为:"+sortJsonArray(array));
		System.out.println("排序后的结果为:"+strJsonArray(sortJsonArray(array), sb));
		KeyPair keyPair = RSACrypt.generateKeyPairs();
		String publicKey = base64ToStr(keyPair.getPublic().getEncoded());
		String privateKey = base64ToStr(keyPair.getPrivate().getEncoded());
		System.out.println(publicKey);
		System.out.println(privateKey);*/
		PublicKey publicKey = RSACrypt.resolvePublic(Base64.decodeBase64("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0tSdvOjDBZLzB5tWpcpYVNRK0DHmPeTEmf67rWU37aPMH3i/caozIGXapPjaqfgS2Bymp8Mr/hKVSHZT3HvaHYLSI0QMiFvpuhv5mWHgOCrunbULiVSjuR/7oJH8DSwd/T1SE+LBJFxq+8lFM92top/zG9fdxe3gcvHnxUdRLPx/OyDfSlNSnA+qoOISTMiUSxvFYf3PdXkJZy0txgzX+70Ak5y4pIcH3oI0siS0hJQP08Z/MduPseNtV+JjB8GDla+SjqantnfLbLu44UqVhSZ279B7zhMZPSpq5/cZIfWbgXkso7Ge9dxA3WmZYZRbP3QIi9+odTOaHPTkZGPTgwIDAQAB"));
		byte[] encryptData = encryptByPublicKey("{\"sysCode\":\"P AYJP\",\"op\":\"U\",\"loginName\":\"NM35\",\"roles\":[{\"roleDesc\":\"无\",\"roleCode\":\"admin\",\"roleName\":\"系统管理员\",\"subsysRoleId\":\"1\"},{\"roleDesc\":\" \",\"roleCode\":\"opra\",\"roleName\":\"运营人员\",\"subsysRoleId\":\"2\"}],\"operatorId\":7}".getBytes(), "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0tSdvOjDBZLzB5tWpcpYVNRK0DHmPeTEmf67rWU37aPMH3i/caozIGXapPjaqfgS2Bymp8Mr/hKVSHZT3HvaHYLSI0QMiFvpuhv5mWHgOCrunbULiVSjuR/7oJH8DSwd/T1SE+LBJFxq+8lFM92top/zG9fdxe3gcvHnxUdRLPx/OyDfSlNSnA+qoOISTMiUSxvFYf3PdXkJZy0txgzX+70Ak5y4pIcH3oI0siS0hJQP08Z/MduPseNtV+JjB8GDla+SjqantnfLbLu44UqVhSZ279B7zhMZPSpq5/cZIfWbgXkso7Ge9dxA3WmZYZRbP3QIi9+odTOaHPTkZGPTgwIDAQAB");
		String encryptStr = "yulctjdCOhaWuX2QRaeCrjw+JQxHpbHVCUQ9wGWOYy9t9HMKDrl5gBmvmNg/FA5iBktaE1Q57ypW\n" +
				"8hzNl3NmpI2r260JsLJ3pUm6MArOhX3FpKkpF5293yqPo8K6feKXa2z3bJ+VCeLJ+0gR7qK6RYTx\n" +
				"zcmkvVQXZ4wS3mxqQufIBVV9POg0gbnLsorOPJW4XAGbs7ovhG4tWICKacCL/Njjmbn83U8vn3Yq\n" +
				"HE36fGchuFWo9OvwreWdryJhIwVpE2eufUWwyoWVmcllqXZc5cK3WBM3B5fnDFODLR3d5jOYFR+1\n" +
				"2rc8r/AhYWu4jXLt4UnmWH5ziygaKGwjJjI5Vw==";
		System.out.println(encryptStr);
		PrivateKey privateKey = RSACrypt.resolvePrivate(Base64.decodeBase64("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDS1J286MMFkvMHm1alylhU1ErQMeY95MSZ/rutZTfto8wfeL9xqjMgZdqk+Nqp+BLYHKanwyv+EpVIdlPce9odgtIjRAyIW+m6G/mZYeA4Ku6dtQuJVKO5H/ugkfwNLB39PVIT4sEkXGr7yUUz3a2in/Mb193F7eBy8efFR1Es/H87IN9KU1KcD6qg4hJMyJRLG8Vh/c91eQlnLS3GDNf7vQCTnLikhwfegjSyJLSElA/Txn8x24+x421X4mMHwYOVr5KOpqe2d8tsu7jhSpWFJnbv0HvOExk9Kmrn9xkh9ZuBeSyjsZ713EDdaZlhlFs/dAiL36h1M5oc9ORkY9ODAgMBAAECggEAMFg443ysW7baq7/fGp1PrAQSM7N9jGvM+VMETjqLnJ6WzBCwNaxFlQRGItY9rgQIri3DuWxzxgsU2Ezp5gEwftvku9l61jndWqPXlGhxNfACT3+YaaFS+bxvwt424f5f0hYhoBW7HE9k6N+6Uq5ehpAO+y+A10Y41aLK9t3nq1zAbxum/h1cWBzjjJ6W9KOYt/RTlxJAieUlrwN7XbP2GPFutN9FRGc7t7QAJfHAygMv3uhFfuHy01EGcvKJK7wgquab6s5NmWfS3w0Fesm/tHXy3q5lcUTnBxL9hrIGC5mW/cWMWkXvK5dBoSLVnF06RiioIdz3Hh18BtxdJsgvAQKBgQDs4u5ri8BbUnyBAOkZzeCL4ypwmnr/zMRxsCH9nBB8j1vUG5kO9XPsn4CTiHWq+L4TXSX9F49NZr+aW8xB0udVdRQfTAvCpspKEqWvL8ZldZj8QVoVClWH+4cfwWjDtqDBv9GEab7hFxhanzCCy4Z4Doa1TI829jrOCNg5E7HZYwKBgQDj13rqr5PPVSM3Fz+SBu/7Zic8vIJXQQldhWsWhfxK2Z17rP4+TTfDbrVzv7HI0VaraO20EDkDuE1KgHb0MhrFATUOhqN5M5Ah6Fz2RuQnXpD3Os7pmR0Gl8zHv72/Af55SCctlA7KYcGP1eTAvnsR8fwBGKkTeqs3zRqf81JHYQKBgQDpTe+HmFJRHwJkg6MQuQWogIlx8ZOmj+QCHHi9sJFEPGUymCO86d3MdKNLqqBJTUbXB2y8whFvJ/0ZZ8PaKH+SWQcCdAE5KtAc+BSaX7FEh/euqiawhkcK8J16lyA5cYsOh38wrH1Eb1ybFCHRD+ZWI+hkcwyyEvyDo45hWJZXBQKBgFa6qKR7T2Wl//CxndZFY8ar3RXGoEr8i8cYyWnaeO45JjR05pbqp+Zx1wqHlvJdlYNMPufDISDkYC+S9TUBeaUyT3MHF2VEloIBxJlO5PyDh+VEco0DZsQitqX2vBDgfnDGYXt9F6A8VAUfo6MnHwv2tDnqnDm5AUINhiwK4HAhAoGBALH8vxXfdqD3o7uRgg/JTM9hhiguK6mX/UDd2TQhybL3w6oEpHS4HdpNk+sOJA1Xf0HIc82AEqaUSxfA9mZsF/MByldNWM5LXDC/qiR7Nj6kfulgb22XtEJ3cA9dKo7KBNkRg1VcoRoB05Q7hIJOLocwjvbSNlOKFHWAsUQJ/MHP"));
//		byte[] decryptDate = RSACrypt.decrypt(Base64.decodeBase64(encryptStr.getBytes()), privateKey);
//		System.out.println(new String(decryptDate));
		byte[] decryptByDate = decryptByPrivateKey(Base64.decodeBase64(encryptStr.getBytes()), "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDS1J286MMFkvMHm1alylhU1ErQMeY95MSZ/rutZTfto8wfeL9xqjMgZdqk+Nqp+BLYHKanwyv+EpVIdlPce9odgtIjRAyIW+m6G/mZYeA4Ku6dtQuJVKO5H/ugkfwNLB39PVIT4sEkXGr7yUUz3a2in/Mb193F7eBy8efFR1Es/H87IN9KU1KcD6qg4hJMyJRLG8Vh/c91eQlnLS3GDNf7vQCTnLikhwfegjSyJLSElA/Txn8x24+x421X4mMHwYOVr5KOpqe2d8tsu7jhSpWFJnbv0HvOExk9Kmrn9xkh9ZuBeSyjsZ713EDdaZlhlFs/dAiL36h1M5oc9ORkY9ODAgMBAAECggEAMFg443ysW7baq7/fGp1PrAQSM7N9jGvM+VMETjqLnJ6WzBCwNaxFlQRGItY9rgQIri3DuWxzxgsU2Ezp5gEwftvku9l61jndWqPXlGhxNfACT3+YaaFS+bxvwt424f5f0hYhoBW7HE9k6N+6Uq5ehpAO+y+A10Y41aLK9t3nq1zAbxum/h1cWBzjjJ6W9KOYt/RTlxJAieUlrwN7XbP2GPFutN9FRGc7t7QAJfHAygMv3uhFfuHy01EGcvKJK7wgquab6s5NmWfS3w0Fesm/tHXy3q5lcUTnBxL9hrIGC5mW/cWMWkXvK5dBoSLVnF06RiioIdz3Hh18BtxdJsgvAQKBgQDs4u5ri8BbUnyBAOkZzeCL4ypwmnr/zMRxsCH9nBB8j1vUG5kO9XPsn4CTiHWq+L4TXSX9F49NZr+aW8xB0udVdRQfTAvCpspKEqWvL8ZldZj8QVoVClWH+4cfwWjDtqDBv9GEab7hFxhanzCCy4Z4Doa1TI829jrOCNg5E7HZYwKBgQDj13rqr5PPVSM3Fz+SBu/7Zic8vIJXQQldhWsWhfxK2Z17rP4+TTfDbrVzv7HI0VaraO20EDkDuE1KgHb0MhrFATUOhqN5M5Ah6Fz2RuQnXpD3Os7pmR0Gl8zHv72/Af55SCctlA7KYcGP1eTAvnsR8fwBGKkTeqs3zRqf81JHYQKBgQDpTe+HmFJRHwJkg6MQuQWogIlx8ZOmj+QCHHi9sJFEPGUymCO86d3MdKNLqqBJTUbXB2y8whFvJ/0ZZ8PaKH+SWQcCdAE5KtAc+BSaX7FEh/euqiawhkcK8J16lyA5cYsOh38wrH1Eb1ybFCHRD+ZWI+hkcwyyEvyDo45hWJZXBQKBgFa6qKR7T2Wl//CxndZFY8ar3RXGoEr8i8cYyWnaeO45JjR05pbqp+Zx1wqHlvJdlYNMPufDISDkYC+S9TUBeaUyT3MHF2VEloIBxJlO5PyDh+VEco0DZsQitqX2vBDgfnDGYXt9F6A8VAUfo6MnHwv2tDnqnDm5AUINhiwK4HAhAoGBALH8vxXfdqD3o7uRgg/JTM9hhiguK6mX/UDd2TQhybL3w6oEpHS4HdpNk+sOJA1Xf0HIc82AEqaUSxfA9mZsF/MByldNWM5LXDC/qiR7Nj6kfulgb22XtEJ3cA9dKo7KBNkRg1VcoRoB05Q7hIJOLocwjvbSNlOKFHWAsUQJ/MHP");
		System.out.println(new String(decryptByDate));
	}
	public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
			throws Exception {
		byte[] keyBytes = Base64Utils.decode(privateKey);
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

	public static byte[] encryptByPublicKey(byte[] data, String publicKey)
			throws Exception {
		byte[] keyBytes = Base64Utils.decode(publicKey);
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

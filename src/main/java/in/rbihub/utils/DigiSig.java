package in.rbihub.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author metamug.com
 */
public class DigiSig {

	private static final String SPEC = "secp256k1";
	private static final String ALGO = "SHA256withECDSA";
	private static final Logger log = LogManager.getLogger(DigiSig.class);

//	private JSONObject sender() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
//			InvalidKeyException, UnsupportedEncodingException, SignatureException {
//
//		ECGenParameterSpec ecSpec = new ECGenParameterSpec(SPEC);
//		KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
//		g.initialize(ecSpec, new SecureRandom());
//		KeyPair keypair = g.generateKeyPair();
//		PublicKey publicKey = keypair.getPublic();
//		PrivateKey privateKey = keypair.getPrivate();
//
//		String plaintext = "Hello";
//
//		// ...... sign
//		Signature ecdsaSign = Signature.getInstance(ALGO);
//		ecdsaSign.initSign(privateKey);
//		ecdsaSign.update(plaintext.getBytes("UTF-8"));
//		byte[] signature = ecdsaSign.sign();
//		String pub = Base64.getEncoder().encodeToString(publicKey.getEncoded());
//		String sig = Base64.getEncoder().encodeToString(signature);
//		System.out.println(sig);
//		System.out.println(pub);
//
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put("publicKey", pub);
//			obj.put("signature", sig);
//			obj.put("message", plaintext);
//			obj.put("algorithm", ALGO);
//		} catch (Exception exp) {
//
//		}
//
//		return obj;
//	}

	/**
	 * The method to sign the JSONObject data and send the JSONObject as response
	 * with Algorithm , publicKey and JSONObject data The Algorithm used is
	 * SHA256withECDSA
	 * 
	 * @param data
	 * @param logstmt
	 * @return
	 */
	public static JSONObject signData(JSONObject data, String logstmt,PublicKey pubKey, PrivateKey privkey) {
		JSONObject returnData = new JSONObject();
		try {
			returnData.put("data", data);
			ECGenParameterSpec ecSpec = new ECGenParameterSpec(SPEC);
			PublicKey publicKey = null;
			PrivateKey privateKey =null;
			if(pubKey ==null || pubKey.toString().trim().equals("")) {
				KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
				g.initialize(ecSpec, new SecureRandom());
				KeyPair keypair = g.generateKeyPair();
				publicKey = keypair.getPublic();
				privateKey = keypair.getPrivate();
			}else {
				publicKey = pubKey;
				privateKey = privkey;
			}
//			KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
//			g.initialize(ecSpec, new SecureRandom());
//			KeyPair keypair = g.generateKeyPair();
//			PublicKey publicKey = keypair.getPublic();
//			PrivateKey privateKey = keypair.getPrivate();
			
			Signature ecdsaSign = Signature.getInstance(ALGO);
			ecdsaSign.initSign(privateKey);
			ecdsaSign.update(data.toString().getBytes("UTF-8"));
			byte[] signature = ecdsaSign.sign();
			String pub = Base64.getEncoder().encodeToString(publicKey.getEncoded());
			String sig = Base64.getEncoder().encodeToString(signature);
			log.info(logstmt+ "\tSignature : "+sig);
			log.info(logstmt+ "\tPublicKey : "+pub);
			log.info(logstmt+ "\tData : "+data.toString());
			returnData.put("publicKey", pub);
			returnData.put("signature", sig);
			returnData.put("algorithm", ALGO);

		} catch (Exception exp) {

		}
		return returnData;

	}

	/***
	 * 
	 * Verify the data with the Signature and publicKey
	 * @param data
	 * @param signature
	 * @param publickKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 * @throws SignatureException
	 * @throws JSONException
	 */
	public boolean verify(String data, String signature, String publickKey) throws NoSuchAlgorithmException, InvalidKeySpecException,
			InvalidKeyException, UnsupportedEncodingException, SignatureException, JSONException {

		Signature ecdsaVerify = Signature.getInstance(ALGO);
		KeyFactory kf = KeyFactory.getInstance("EC");

		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publickKey));

		KeyFactory keyFactory = KeyFactory.getInstance("EC");
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

		ecdsaVerify.initVerify(publicKey);
		ecdsaVerify.update(data.getBytes("UTF-8"));
		boolean result = ecdsaVerify.verify(Base64.getDecoder().decode(signature));

		return result;
	}

//	private boolean receiver(JSONObject obj) throws NoSuchAlgorithmException, InvalidKeySpecException,
//			InvalidKeyException, UnsupportedEncodingException, SignatureException, JSONException {
//
//		Signature ecdsaVerify = Signature.getInstance(obj.getString("algorithm"));
//		KeyFactory kf = KeyFactory.getInstance("EC");
//
//		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(obj.getString("publicKey")));
//
//		KeyFactory keyFactory = KeyFactory.getInstance("EC");
//		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
//
//		ecdsaVerify.initVerify(publicKey);
//		ecdsaVerify.update(obj.getString("message").getBytes("UTF-8"));
//		boolean result = ecdsaVerify.verify(Base64.getDecoder().decode(obj.getString("signature")));
//
//		return result;
//	}

	public static void main(String[] args) throws JSONException {
		try {
			DigiSig digiSig = new DigiSig();
			//JSONObject obj = digiSig.sender();
			//boolean result = digiSig.receiver(obj);
			
			String signature = "MEQCIGrRbR62EJRQJzKYHUEC5cuXW9oXIZO8E9B6CArv/a8hAiBktHKgs1cChArLZG33Ykh7lvjwmzCvSNCfGfvsE70cAQ==";
			String data = "{\"ownerDetail\":[{\"columnno\":\"5\",\"fatherName\":\"भारतसिंह\",\"ownershiptype\":\"भूमि स्वामी\",\"address\":\"मालखेड़ा तराना उज्जैन मध्य प्रदेश \",\"ownerName\":\"प्रेमसिंह  \",\"ownerShare\":\"1\",\"ownerCaste\":\"सामान्य\",\"AadharTokenNo\":\"01001187VKWJWPj37A8DTkQ9Hl6EdmtE9gZLhODSlfDmhDtfy3qYCD85SpzHPrXIGJ6Zohhk\",\"flnel\":\"प्रेमसिंह  \"}]}";
			String publicKey = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAElVnZy/qcXsARr6mH45zHOHlJpqsOnUCPpfjdfWMwQirM1ux5+CumIphcRv6PM1QtPoW2POuw5Kf0hHe5CddRpg==";
			boolean result = digiSig.verify(data,signature, publicKey);
			System.out.println(result);
		} catch (NoSuchAlgorithmException ex) {
			log.info(ex);
		}catch (InvalidKeyException ex) {
			log.info(ex);
		} catch (UnsupportedEncodingException ex) {
			log.info(ex);
		} catch (SignatureException ex) {
			log.info(ex);
		} catch (InvalidKeySpecException ex) {
			log.info(ex);
		}
	}

}

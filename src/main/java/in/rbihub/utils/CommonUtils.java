package in.rbihub.utils;


import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import in.rbihub.error.InvalidParamException;
import in.rbihub.service.PersonService;

//public class CommonUtils{
//	
//}

public class CommonUtils implements ICommonMethods {
	
	public static void main(String args[]) {
		getInstance();
	}
	private static final Logger log = LogManager.getLogger(CommonUtils.class);
	public static CommonUtils getInstance() {
		return new CommonUtils();
	}
	
	private CommonUtils() {
		
	}

	@Override
	public String encryptWithPublicKey(String _inputText, String pubKeyBase64Encoded) 
			throws 	IllegalBlockSizeException,	BadPaddingException,
					NoSuchAlgorithmException,	InvalidKeySpecException,
					NoSuchPaddingException, 	InvalidKeyException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pubKeyBase64Encoded.replace("#", "/").getBytes()));
		PublicKey pk = (PublicKey) keyFactory.generatePublic(publicKeySpec);
		Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pk);
        byte[] encryptedBytes = cipher.doFinal(_inputText.getBytes());
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	@Override
	public String decryptWithPrivateKey(String _inputText, PrivateKey privkey) 
			throws 	NoSuchAlgorithmException, 	NoSuchPaddingException, 
					InvalidKeyException, 		IllegalBlockSizeException, 
					BadPaddingException 
	{
		Cipher cipherPrivKey = Cipher.getInstance("RSA");
		cipherPrivKey.init(cipherPrivKey.DECRYPT_MODE, privkey);
        byte[] decryptedBytes = cipherPrivKey.doFinal(Base64.getDecoder().decode(_inputText.getBytes()));
        return new String(decryptedBytes);
	}

	/**
	 * Method to digitally sign a String with a Private Key
	 * @author Rakesh Ranjan
	 * @return String. Base64 encoded String of the signature
	 * @param _inputText: Unsigned input String
	 * @param _privKey: Private Key
	 * @param _password: Password as char array
	 * @param _algorithm: Algorithm 
	 */
	
	@Override
	public String sign(String _inputText, PrivateKey _privKey, char[] _password, Algorithms _algorithm) 
			throws 	NoSuchAlgorithmException, 
					InvalidKeyException, 
					SignatureException {
		Signature signature=Signature.getInstance(_algorithm.name());
		signature.initSign(_privKey);
		signature.update(_inputText.getBytes());
		byte[] signedInputText=signature.sign();
		return Base64.getEncoder().encodeToString(signedInputText);
	}

	/**
	 * Method to verify a digitally signed String with a Public Key
	 * @author Rakesh Ranjan
	 * @return boolean. True when signature is found to be valid and false when signature cannot be verified
	 * @param _originalString: Unsigned string that needs to be verified
	 * @param _signature: Base64 representation of the signature
	 * @param _pubKey: Public Key whose private key was used for signing the _originalString
	 * @param _algorithm: Algorithm 
	 */
	
	@Override
	public boolean verifySignatureWithPublicKey(String _originalString, String _signature, PublicKey _pubKey, Algorithms _algorithm) 
			throws 	NoSuchAlgorithmException, 
					InvalidKeyException, 
					SignatureException {
		Signature signature=Signature.getInstance(_algorithm.name());
		signature.initVerify(_pubKey);
		signature.update(_originalString.getBytes());
		return signature.verify(Base64.getDecoder().decode(_signature.getBytes()));
	}

	/**
	 * Method to verify a digitally signed String with a Public Key encoded value
	 * @author Rakesh Ranjan
	 * @return boolean. True when signature is found to be valid and false when signature cannot be verified
	 * @param _originalString: Unsigned string that needs to be verified
	 * @param _signature: Base64 representation of the signature
	 * @param _pubKey: Public Key whose private key was used for signing the _originalString
	 * @param _signatureAlgorithm: Algorithm (SHA256withRSA)
	 * @param _keyFactoryAlgorithm: Algorith (RSA)
	 * @throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException 
	 * 
	 */
	
	@Override
	public boolean verifySignatureWithPublicKeyEncoded(String _originalString, String _signature, String _pubKeyEncoded, Algorithms _signatureAlgorithm, Algorithms _keyFactoryAlgorithm) 
			throws 	NoSuchAlgorithmException, 
					InvalidKeyException, 
					SignatureException, 
					InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance(_keyFactoryAlgorithm.name());
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(_pubKeyEncoded.getBytes()));
		PublicKey pk = (PublicKey) keyFactory.generatePublic(publicKeySpec);		
		
		Signature signature=Signature.getInstance(_signatureAlgorithm.name());
		signature.initVerify(pk);
		signature.update(_originalString.getBytes());
		return signature.verify(Base64.getDecoder().decode(_signature.getBytes()));
	}

	/**
	 * Method to verify whether an IP address belongs to an IP pool
	 * @author Rakesh Ranjan
	 * @return boolean. True when the IP address falls within the given range and false when otherwise
	 * @param ipStart: Starting IP address of the IP pool
	 * @param ipEnd: Ending IP address of the IP pool
	 * @param ipToCheck: IP address to check
	 * @throws UnknownHostException 
	 * 
	 */
	@Override
	public boolean isIPWithinPool(String ipStart, String ipEnd, String ipToCheck) throws UnknownHostException {
		long ipLo = ipToLong(InetAddress.getByName(ipStart));
		long ipHi = ipToLong(InetAddress.getByName(ipEnd));
		long ipToTest = ipToLong(InetAddress.getByName(ipToCheck));
		return (ipToTest >= ipLo && ipToTest <= ipHi);
	}
	
	private static long ipToLong(InetAddress ip) {
		byte[] octets = ip.getAddress();
		long result = 0;
		for (byte octet : octets) {
			result <<= 8;
			result |= octet & 0xff;
		}
		return result;
	}

	@Override
	public boolean isDigitalCertificateWithinValidityDate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	/**
	 * Method to obtain Hex encoded SHA256 hash of inputText
	 * @author Rakesh Ranjan
	 * @return String. SHA256 message digest hash as Hex encoded
	 * @param inputText: Input String
	 * 
	 */
	@Override
	public String getSHA256Hash(String inputText) {
		return DigestUtils.sha256Hex(inputText);
	}
	
	@Override
	public JSONObject getResponseObject(
					String serviceName, 
					InvalidParamException.ErrorCodes errorCode, 
					String message, 
					String secretKey,
					boolean isSignatureRequired,
					PrivateKey privKey, 
					char[] password, 
					String txnCode) 
			throws 	InvalidKeyException, 
					NoSuchAlgorithmException, 
					SignatureException 
			
	{
		JSONObject returnObj= null;
		try {
		 returnObj=new JSONObject();
		JSONObject dataObj=new JSONObject();
		dataObj.put("message", message);
		returnObj.put("data", dataObj);
		
		//Sign the data object
		if(isSignatureRequired && privKey!=null) {
			String signature=CommonUtils.getInstance().sign(dataObj.toString(), privKey, password, Algorithms.SHA256withRSA);
			returnObj.put("signature",signature);
		}
		
		//Add header
		JSONObject headerObj=new JSONObject();
		headerObj.put("service", serviceName);
		headerObj.put("txncode", txnCode);
		headerObj.put("ts", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
		if(errorCode.equals(InvalidParamException.ErrorCodes.E000)) {
			headerObj.put("status", "success");
			headerObj.put("errorcode", errorCode.name());
		} else {
			headerObj.put("status", "failed");
			headerObj.put("errorcode", errorCode.name());
			headerObj.put("errordesc", InvalidParamException.getErrorDescription(errorCode));
		}
		headerObj.put("hmacSHA512",generateHmac(Algorithms.HmacSHA512, dataObj.toString(), secretKey)); //SHA512 works faster on 64 bit machines
		returnObj.put("header", headerObj);
		}catch (Exception exp) {
		
		}
		return returnObj;
		
	}
	
	@Override
	public JSONObject getPlatformResponseObject(
			InvalidParamException.ErrorCodes errorCode, 
					JSONObject data,
					String secretKey,
					boolean isSignatureRequired,
					PrivateKey privKey, 
					PublicKey pubKey, 
					char[] password, 
					String txnCode,
					String logmsg) 
			
			
	{
		JSONObject returnObj= null;
		try {
		 returnObj=new JSONObject();
		 returnObj.put("data", data);
		// if(data ==null || data.length())
		//Sign the data object
		if(isSignatureRequired ) {
			try { 
				JSONObject signedData = DigiSig.signData(data, logmsg, pubKey,privKey);
				if(signedData.has("signature")) {
					returnObj.put("signature",signedData.get("signature"));
					returnObj.put("publickey",signedData.get("publicKey"));
					returnObj.put("algorithm",signedData.get("algorithm"));
				}
					
			}catch(Exception exp) {
				
			}
		}
		
		//Add header
		JSONObject metaObj=new JSONObject();
		metaObj.put("txncode", txnCode);
		// format should be as 2023-03-13T15:04:01+0530
		OffsetDateTime offsetDT = OffsetDateTime.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

		metaObj.put("ts", fmt.format(offsetDT));
		metaObj.put("ver", "1.0");
		returnObj.put("meta", metaObj);
		
		JSONObject resultObj=new JSONObject();
		
		if(errorCode.equals(InvalidParamException.ErrorCodes.E000)) {
			resultObj.put("status", "success");
			resultObj.put("errcode", errorCode.name());
			resultObj.put("info", "");
			log.info(logmsg + ": Request processed successfully with  :" + txnCode);
			
		} else {
			resultObj.put("status", "failed");
			resultObj.put("errcode", errorCode.name());
			resultObj.put("info", InvalidParamException.getErrorDescription(errorCode));
			log.info(logmsg + ": Request processing failed  for  :" + txnCode);
		}
		returnObj.put("hmac",generateHmac(Algorithms.HmacSHA256, data.toString(), secretKey)); //SHA512 works faster on 64 bit machines
		
		returnObj.put("result", resultObj);
		
		
		}catch (Exception exp) {
		
		}
		return returnObj;
		
	}
	
	@Override
	public String generateRandomSessionKey(int len) {
		SecureRandom random = new SecureRandom();
		final String CHARACTER_SET="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ$#@";
		StringBuffer buff = new StringBuffer(len);
		for(int i=0;i<len;i++) {
            int offset = random.nextInt(CHARACTER_SET.length());
            buff.append(CHARACTER_SET.substring(offset,offset+1));
        }
		return buff.toString();
	}

	@Override
	public String generateHmac(Algorithms algorithm, String data, String key) throws InvalidKeyException, NoSuchAlgorithmException {
//		return new HmacUtils(algorithm.name(), key).hmacHex(data);
		Mac shaNNN_HMAC = Mac.getInstance(algorithm.name());
		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), algorithm.name());
		shaNNN_HMAC.init(secret_key);
		String hash = Base64.getEncoder().encodeToString(shaNNN_HMAC.doFinal(data.getBytes()));
		return hash;
	}
}


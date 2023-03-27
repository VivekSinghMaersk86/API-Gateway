package in.rbihub.utils;

import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONObject;

import in.rbihub.error.InvalidParamException;


public interface ICommonMethods {
	
	public enum Algorithms{
		SHA256withRSA,
		RSA, MD5, HmacMD5, HmacSHA256,
		HmacSHA512
	}
	
//	public enum ErrorCodes {
//		E001, E002, E003, E004, E005,
//		E006, E007, E025, E026, E027, E028, E029, E030, E999, E000
//	}

	public String encryptWithPublicKey(String _inputText, String pubKeyBase64Encoded) 
			throws 	IllegalBlockSizeException, 	BadPaddingException, 
					NoSuchAlgorithmException, 	InvalidKeySpecException, 
					NoSuchPaddingException, 	InvalidKeyException;
	
	public String decryptWithPrivateKey(String _inputText, PrivateKey privkey) 
			throws 	NoSuchAlgorithmException, 	NoSuchPaddingException, 
					InvalidKeyException, 		IllegalBlockSizeException, 
					BadPaddingException;
	
	public String getSHA256Hash(String inputText);
	
	public String sign(String _inputText, PrivateKey _privKey, char[] _password, Algorithms _algorithm) 
			throws 	NoSuchAlgorithmException, 
					InvalidKeyException, 
					SignatureException;
	
	public boolean verifySignatureWithPublicKey(String _originalString, String _signature, PublicKey _pubKey, Algorithms _algorithm) 
			throws 	NoSuchAlgorithmException, 
					InvalidKeyException, 
					SignatureException;

	public boolean verifySignatureWithPublicKeyEncoded(String _originalString, String _signature, String _pubKeyEncoded, Algorithms _signatureAlgorithm, Algorithms _keyFactoryAlgorithm) 
			throws 	NoSuchAlgorithmException, 
					InvalidKeyException, 
					SignatureException, 
					InvalidKeySpecException;
	
	public boolean isIPWithinPool(String ipStart, String ipEnd, String ipToCheck) throws UnknownHostException;
	
	public boolean isDigitalCertificateWithinValidityDate();
	
	
	
	public JSONObject getResponseObject(	String serviceName, 
			InvalidParamException.ErrorCodes errorCode, 
											String message, 
											String secretKey, 
											boolean isSignatureRequired, 
											PrivateKey privKey, 
											char[] password, 
											String txnCode) 
			throws 	InvalidKeyException, 
					NoSuchAlgorithmException, 
					SignatureException;
	
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
	throws 	InvalidKeyException, 
			NoSuchAlgorithmException, 
			SignatureException ;
	
	public String generateRandomSessionKey(int len);
	
	public String generateHmac(Algorithms algorithm, String data, String key) throws InvalidKeyException, NoSuchAlgorithmException;
}
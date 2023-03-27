package in.rbihub.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Hashtable;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import in.rbihub.transformation.Transformation;

public class RSA {

	private static Hashtable<String, RSAPublicKey> clientIDToPublicKeyMap = new Hashtable<String, RSAPublicKey>();

	public static void main(String[] args) {
		RSA rsa = new RSA();
		try {
			File file = new File("src/main/resources/sslcert/private_key.pem");
			// RSAPrivateKey privatekey = rsa.readPEMPrivateKey(file);
			RSAPrivateKey privatekey = rsa.readPEMPrivateKey(file);

			System.out.println("Private key : " + privatekey.toString());

			RSAPublicKey publickey = rsa.getPublicKey("src/main/resources/sslcert/public.pub");
			System.out.println("Public key : " + publickey.toString());
		} catch (Exception exp) {
			exp.printStackTrace();
		}

	}

	private static String getKey(String filename) throws IOException {
		// Read key from file
		String strKeyPEM = "";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = br.readLine()) != null) {
			strKeyPEM += line + "\n";
		}
		br.close();
		return strKeyPEM;
	}

	public static RSAPrivateKey getPrivateKey(String filename) throws IOException, GeneralSecurityException {
		String privateKeyPEM = getKey(filename);
		return getPrivateKeyFromString(privateKeyPEM);
	}

	public static RSAPrivateKey getPrivateKeyFromString(String key) throws IOException, GeneralSecurityException {
		String privateKeyPEM = key;
		privateKeyPEM = privateKeyPEM.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
		privateKeyPEM = privateKeyPEM.replace("-----END RSA PRIVATE KEY-----", "");
		byte[] encoded = Base64.decodeBase64(privateKeyPEM);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
		RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
		return privKey;
	}

	public static RSAPublicKey getClientIDPublicKey(String clientId) {
		RSAPublicKey publicKey = null;
		if (clientIDToPublicKeyMap.containsKey(clientId)) {
			publicKey = clientIDToPublicKeyMap.get(clientId);
		} else {

		}
		return publicKey;

	}

//	public static void loadPublicKeys(String publicKeyPath) {
//		File folder = new File(publicKeyPath);
//		if (!folder.isDirectory()) {
//return;
//		} else {
//			FilenameFilter filenameFilter = 
//			// list out all the file name and filter by the extension
//			String[] list = folder.list(extFilter);
//
//			if (list.length == 0) {
//				return;
//			}
//
//			for (int i = 0; i < list.length; i++) {
//				System.out.println("File :" + list[i]);
//			}
//		}
//
//	}

//inner class, generic extension filter
	public class MyExtFilter implements FilenameFilter {

		private String ext;

		public MyExtFilter(String ext) {
			this.ext = ext;
		}

		public boolean accept(File dir, String name) {
			return (name.endsWith(ext));
		}
	}

	public static RSAPrivateKey readPrivateKey(File file) throws Exception {
		String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());

		String privateKeyPEM = key.replace("-----BEGIN RSA PRIVATE KEY-----", "").replaceAll(System.lineSeparator(), "")
				.replace("-----END RSA PRIVATE KEY-----", "");

		byte[] encoded = Base64.decodeBase64(privateKeyPEM);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
		return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	}

	public static RSAPrivateKey readPEMPrivateKey(File file) throws Exception {
		KeyFactory factory = KeyFactory.getInstance("RSA");

		try (FileReader keyReader = new FileReader(file); PemReader pemReader = new PemReader(keyReader)) {

			PemObject pemObject = pemReader.readPemObject();
			byte[] content = pemObject.getContent();
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
			return (RSAPrivateKey) factory.generatePrivate(privKeySpec);
		}
	}

	public static RSAPublicKey getPublicKey(String filename) throws IOException, GeneralSecurityException {
		String publicKeyPEM = getKey(filename);
		return getPublicKeyFromString(publicKeyPEM);
	}

	public static RSAPublicKey getPublicKeyFromString(String key) throws IOException, GeneralSecurityException {
		String publicKeyPEM = key;
		publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
		publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
		byte[] encoded = Base64.decodeBase64(publicKeyPEM);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
		return pubKey;
	}

	public static String sign(PrivateKey privateKey, String message)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
		Signature sign = Signature.getInstance("SHA1withRSA");
		sign.initSign(privateKey);
		sign.update(message.getBytes("UTF-8"));
		return new String(Base64.encodeBase64(sign.sign()), "UTF-8");
	}

	public static boolean verify(PublicKey publicKey, String message, String signature)
			throws SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
		Signature sign = Signature.getInstance("SHA1withRSA");
		sign.initVerify(publicKey);
		sign.update(message.getBytes("UTF-8"));
		return sign.verify(Base64.decodeBase64(signature.getBytes("UTF-8")));
	}

	public static String encrypt(String rawText, PublicKey publicKey) throws IOException, GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return Base64.encodeBase64String(cipher.doFinal(rawText.getBytes("UTF-8")));
	}

	public static String decrypt(String cipherText, PrivateKey privateKey)
			throws IOException, GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return new String(cipher.doFinal(Base64.decodeBase64(cipherText)), "UTF-8");
	}
}

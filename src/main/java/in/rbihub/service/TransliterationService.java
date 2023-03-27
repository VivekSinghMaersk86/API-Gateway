package in.rbihub.service;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import in.rbihub.config.ApplicationConfig;
import in.rbihub.error.InvalidParamException;
import in.rbihub.utils.CommonUtils;
import in.rbihub.utils.Helper;
import in.rbihub.utils.RSA;
import in.rbihub.validation.RequestDataValidations;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransliterationService {

	private static final Logger log = LogManager.getLogger(TransliterationService.class);

	@Autowired
	private ApplicationConfig applicationConfig;

	private PrivateKey privateKey = null;

	/**
	 * This method is responsible to validate all common uri params like version,
	 * txncode, lang and apikey
	 * 
	 * @param logtrcmsg
	 * @param version
	 * @param txncode
	 * @param lang
	 * @param apikey
	 * @return
	 * @throws SignatureException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public JSONObject validateCommonURIParams(String logtrcmsg, String version, String txncode, String lang,
			String apikey) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		JSONObject resp = null;
		boolean isValid = false;
		JSONObject data = new JSONObject();
		try {
			isValid = RequestDataValidations.getInstance().validateCommonURLParamsWithOutConsent(version, apikey, lang,
					txncode);

		} catch (InvalidParamException invExp) {
			resp = CommonUtils.getInstance().getPlatformResponseObject(invExp.getErrorCode(), data,
					applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
			log.info(logtrcmsg + ": Request processed with response:" + resp);
			return resp;
		}

		if (!isValid) {
			resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E028, data,
					applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
			log.info(logtrcmsg + ": Request processed with response:" + resp);
		}

		return resp;
	}

	/**
	 * Validation for all common URL Parameters for POST like version, apikey, lang
	 * 
	 * @param version
	 * @param apikey
	 * @param lang
	 * @param txncode
	 * @return true in case validation is success
	 */
	public JSONObject validateCommonURPOSTLParams(String logtrcmsg, String version, String lang, String apikey,
			String txncode, String contentType, Integer contentLength, int recievedContentLegnth) {
		JSONObject resp = null;
		boolean isValid = false;
		JSONObject data = new JSONObject();
		try {
			isValid = RequestDataValidations.getInstance().validateCommonURPOSTLParams(version, apikey, lang,
					contentType, contentLength, recievedContentLegnth);

		} catch (InvalidParamException invExp) {
			resp = CommonUtils.getInstance().getPlatformResponseObject(invExp.getErrorCode(), data,
					applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
			log.info(logtrcmsg + ": Request processed with response:" + resp);
			return resp;
		}

		if (!isValid) {
			resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E028, data,
					applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
			log.info(logtrcmsg + ": Request processed with response:" + resp);
		}

		return resp;
	}

	/**
	 * This method is responsible to validate all request body txncode, lang and
	 * apikey
	 * 
	 * @param logtrcmsg
	 * @param jsonData
	 * @param txncode
	 * @return
	 * @throws SignatureException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public JSONObject validateRequestBody(String logtrcmsg, String jsonData, String txncode)
			throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		JSONObject resp = null;
		if (jsonData != null && !jsonData.trim().equals("")) {
			try {
				/*
				 * Validate the Json for this post structure
				 * 
				 * {
				 * 
				 * "meta": { "ver": "Version of the request template", "ts":
				 * "timestamp in Unix timestamp format",
				 * "txncode":"unique transaction code provided by the lender" }, "data": {
				 * "key1":"value1", "key2": "value2", "keyN": "valueN" }, "hmac":
				 * "Encrypted SHA-256 Hash of the value of key 'data'"
				 * 
				 * }
				 */
				JSONObject data = new JSONObject(jsonData);
				if (data.has("meta")) {
					JSONObject meta = data.getJSONObject("meta");
					if (meta.has("ver")) {
						boolean isVersionValid = RequestDataValidations.getInstance()
								.isVersionValid(meta.getString("ver"));
						if (!isVersionValid) {
							throw new InvalidParamException(InvalidParamException.ErrorCodes.E009,
									InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E009));
						}
					} else {
						throw new InvalidParamException(InvalidParamException.ErrorCodes.E008,
								InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E008));
					}
					if (meta.has("ts")) {
						boolean isTSValid = RequestDataValidations.getInstance().isTSValid(meta.getString("ts"));
						if (!isTSValid) {
							throw new InvalidParamException(InvalidParamException.ErrorCodes.E010,
									InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E010));
						}
					} else {
						throw new InvalidParamException(InvalidParamException.ErrorCodes.E010,
								InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E010));
					}
					if (meta.has("txncode")) {
						boolean isTxncodeValid = RequestDataValidations.getInstance()
								.isTxncodeValid(meta.getString("txncode"));
						if (!isTxncodeValid) {
							throw new InvalidParamException(InvalidParamException.ErrorCodes.E034,
									InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E034));
						}
					} else {
						throw new InvalidParamException(InvalidParamException.ErrorCodes.E034,
								InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E034));
					}

				} else {
					throw new InvalidParamException(InvalidParamException.ErrorCodes.E041,
							InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E041));
				}
				if (data.has("hmac")) {
					boolean isHmacValid = RequestDataValidations.getInstance().isHmacValid(data.getString("hmac"));
					if (!isHmacValid) {
						throw new InvalidParamException(InvalidParamException.ErrorCodes.E042,
								InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E042));
					}

				} else {
					throw new InvalidParamException(InvalidParamException.ErrorCodes.E042,
							InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E042));
				}
				if (data.has("data")) {

					JSONObject transData = data.getJSONObject("data");
					if (!transData.has("name") && !transData.has("address")) {
						throw new InvalidParamException(InvalidParamException.ErrorCodes.E043,
								InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E043));
					}
				} else {
					throw new InvalidParamException(InvalidParamException.ErrorCodes.E043,
							InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E043));
				}
			} catch (InvalidParamException invExp) {
				resp = CommonUtils.getInstance().getPlatformResponseObject(invExp.getErrorCode(), new JSONObject(),
						applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
						applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
				log.info(logtrcmsg + ": Request processed with response:" + resp);
				return resp;

			} catch (Exception invExp) {
				resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E004,
						new JSONObject(), applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
						applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
				log.info(logtrcmsg + ": Request processed with response:" + resp);
				return resp;
			}

		} else {
			resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E018,
					new JSONObject(), applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
			log.info(logtrcmsg + ": Request processed with response:" + resp);
		}

		return resp;
	}

	/**
	 * This method is responsible to validate Input specific to Transliteration and
	 * return response based on validation
	 * 
	 * @param logtrcmsg
	 * @param address
	 * @param txncode
	 * @return
	 * @throws SignatureException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public JSONObject validateTransliterationInput(String logtrcmsg, String name, String address, String srclang,
			String lang, String txncode) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		JSONObject resp = null;
		boolean isValid = false;
		JSONObject data = new JSONObject();
		try {
			isValid = RequestDataValidations.getInstance().validateTransliterationInput(name, address, srclang, lang);

		} catch (InvalidParamException invExp) {
			resp = CommonUtils.getInstance().getPlatformResponseObject(invExp.getErrorCode(), data,
					applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
			log.info(logtrcmsg + ": Request processed with response:" + resp);
			return resp;
		}

		if (!isValid) {
			resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E028, data,
					applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
			log.info(logtrcmsg + ": Request processed with response:" + resp);
		}
		return resp;
	}

	/**
	 * This method will perform transliteration based on src , destination lang for
	 * name and address field
	 *
	 */
	@Timed(value = "TransliterationService.transliterate", description = "Time to complete the query")
	public JSONObject performTransliteration(String logtrcmsg, String txncode, String srclang, String destlang,
			String name, String address) {
		JSONObject resp = null;
		JSONObject tempdata = new JSONObject();
		boolean isValid = false;
		log.info(logtrcmsg + ": Request Recieved with srclang:" + srclang + "\tdestLang:" + destlang + "\tname:" + name
				+ "\taddress:" + address);
		JSONObject data = null;
		try {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
			String url = applicationConfig.getTranslitUrl();
			String userName = applicationConfig.getTranslitUser();
			String password = applicationConfig.getTranslitPassword();
			RestTemplate restTemplate = restTemplateBuilder.basicAuthentication(userName, password).build();
			JSONObject inputData = new JSONObject();
			if (name != null) {
				inputData.put("name", name);
			}
			if (address != null) {
				inputData.put("address", address);
			}
			Map<String, Object> tempObj = Helper.getInstance().getValueMapTransliteration(inputData, logtrcmsg,
					txncode);
			Map<String, String> dataheaders = new HashMap<String, String>();

			dataheaders.put("src-lang", getTranslitrationSupLang(srclang));
			dataheaders.put("dest-lang",  getTranslitrationSupLang(destlang));
			data = Helper.getInstance().performPostMethod(logtrcmsg, url, userName, password, dataheaders, tempObj);
		} catch (InvalidParamException exp) {
			resp = CommonUtils.getInstance().getPlatformResponseObject(exp.getErrorCode(), tempdata,
					applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
			return resp;
		} catch (Exception exp) {
			resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E031, tempdata,
					applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
			return resp;
		}
		if (data == null) {
			// Internal error
			resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E025, tempdata,
					applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
		} else {
            if(data.has("success")) {
            	try {
            		// Removing the success parent attribute to send only data 
            		JSONObject newdata = data.getJSONObject("success");
            		data = newdata;
            	}catch(JSONException jsonExp) {
            		// Just log the exception to debug later
            		log.info(logtrcmsg + "Could not get Json Object from " + data);
            	}
            	
            }
			log.debug(logtrcmsg + ": Recieved Transliterated Data : " + data);

			resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E000, data,
					applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);

		}

		log.info(logtrcmsg + ": Request processed with response:" + resp);
		return resp;
	}

	private PrivateKey loadPrivateKey() {

		try {
			if (privateKey == null) {
				privateKey = RSA.readPEMPrivateKey(new File(applicationConfig.getPrivatekey()));
			}
			// privKey = privatekey;
		} catch (Exception exp) {

		}
		return privateKey;
	}

	private String getTranslitrationSupLang(String lang) {
		switch (lang) {
		case "hi":
			return lang;

		case "tn":
			return "ta";

		case "en":
			return lang;

		default:
			return lang;
		}
	}

}

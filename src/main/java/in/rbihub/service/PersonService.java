package in.rbihub.service;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import in.rbihub.config.ApplicationConfig;
import in.rbihub.error.InvalidParamException;
import in.rbihub.transformation.Transformation;
import in.rbihub.utils.CommonUtils;
import in.rbihub.utils.Helper;
import in.rbihub.utils.ICommonMethods;
import in.rbihub.utils.PlatformConstants;
import in.rbihub.utils.RSA;
import in.rbihub.validation.RequestDataValidations;

import java.io.File;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PersonService {

	private static final Logger log = LogManager.getLogger(PersonService.class);

	@Autowired
	private ApplicationConfig applicationConfig;

	@Autowired
	private Transformation transformation;

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
			String apikey, String consent, String consentId,  String timestamp)
			throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		JSONObject resp = null;
		boolean isValid = false;
		JSONObject data = new JSONObject();
		try {
			isValid = RequestDataValidations.getInstance().validateCommonURLParams(version, apikey, lang, txncode,
					consent, consentId,timestamp);

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
		} else if (!consent.equalsIgnoreCase("y")) {
			resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E030, data,
					applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
			log.info(logtrcmsg + ": Request processed with response:" + resp);
		} else if (consent.equalsIgnoreCase("y")) {
			log.info(logtrcmsg + ": Consent provided with ID :" + consentId);
		}

		return resp;
	}

	/**
	 * This method with retrieve Owner data from MP LRS and construct response based
	 * on data received
	 */
	@Timed(value = "PersonService.retrieve.MP", description = "Time to complete the query")
	public JSONObject retrieveMPPerson(String logtrcmsg, String lang, String txncode, String khasraId, Integer distId,
			String tehId) {
		JSONObject resp = null;
		JSONObject tempdata = new JSONObject();
		boolean isValid = false;
		log.info(
				logtrcmsg + ": Request Recieved with khasraId:" + khasraId + "\tdistId:" + distId + "\ttehId:" + tehId);
		try {
			isValid = RequestDataValidations.getInstance().validatePersonMP(khasraId, distId, tehId);
		} catch (InvalidParamException inpExcep) {
			resp = CommonUtils.getInstance().getPlatformResponseObject(inpExcep.getErrorCode(), tempdata,
					applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
			log.info(logtrcmsg + ": Request processed with response:" + resp);
			return resp;

		}
		if (!isValid) {
			resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E028, tempdata,
					applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
		} else {
			JSONObject data = null;
			try {
				data = retrieveMPOwnerData(lang, khasraId, distId, tehId, logtrcmsg,txncode);
			} catch (InvalidParamException exp) {
				resp = CommonUtils.getInstance().getPlatformResponseObject(exp.getErrorCode(), tempdata,
						applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
						applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
				return resp;
			}
			if (data == null) {
				// Internal error
				resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E025,
						tempdata, applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
						applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
			} else {

				log.debug(logtrcmsg + ": Recieved owners details are: " + data);

				resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E000, data,
						applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
						applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);

			}
		}
		log.info(logtrcmsg + ": Request processed with response:" + resp);
		return resp;
	}

	/**
	 * This method with retrieve Owner data from MP LRS api provided the
	 * khasaraId,distId and tehId
	 */
	public JSONObject retrieveMPOwnerData(String lang, String khasaraId, Integer distId, String tehId, String logtrcmsg, String txncode)
			throws InvalidParamException {
		try {
			JSONObject json = null;
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

			String uri = applicationConfig.getMpUrl() + applicationConfig.getMpGetOwner() + "?khasraId=" + khasaraId
					+ "&DistId=" + distId.intValue() + "&TehId=" + tehId;
			String userName = applicationConfig.getMpUser();
			String password = applicationConfig.getMpPassword();
			RestTemplate restTemplate = restTemplateBuilder.basicAuthentication(userName, password).build();

			/**
			 * Please note that record fetected will have UTF_8 char sets . So ensure that
			 * api call to data receiving objects are compatible to collect data
			 */
			StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(
					StandardCharsets.UTF_8);
			stringHttpMessageConverter.setWriteAcceptCharset(true);
			for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
				if (restTemplate.getMessageConverters().get(i) instanceof StringHttpMessageConverter) {
					restTemplate.getMessageConverters().remove(i);
					restTemplate.getMessageConverters().add(i, stringHttpMessageConverter);
					break;
				}
			}
			String response = restTemplate.getForObject(uri, String.class);
			log.info(logtrcmsg + "Receieved response :" + response);
			json = convertXMLToJSONObject(response, logtrcmsg);
			/**
			 * Check if we got owner details, else raise business exception as owner data not found
			 */
			if(json.has("message")) {
				throw new InvalidParamException(InvalidParamException.ErrorCodes.E504,
						InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E504));
			}
			json = convertHierShareToPerc(json, logtrcmsg);
			if (lang.equals(PlatformConstants.LANGISO_ENGLISH)) {

				if (json.has("ownerDetail")) {
					json = performTansliterationNew(logtrcmsg, "madyapradesh", json,txncode);
				}
			}
			json = transformation.transformMPL(json, logtrcmsg);
			return json;
		} catch (InvalidParamException invExp) {
			throw  invExp;
		} catch (RestClientException restExp) {
			log.info(logtrcmsg + "ERROR : Retrieve method with RestClient Exception");
			log.debug(restExp);
			throw new InvalidParamException(InvalidParamException.ErrorCodes.E031,
					InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E031));
		} catch (Exception e) {
			log.info(logtrcmsg + "ERROR : Retrieve method with Exception");
			log.debug(e);
			throw new InvalidParamException(InvalidParamException.ErrorCodes.E031,
					InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E031));
		}
	}

	/**
	 * This method will convert the ownerShare data to percent value
	 */
	public JSONObject convertHierShareToPerc(JSONObject json, String logtrcmsg) {
		JSONObject convertedJson = json;
		try {

			if (json.has("ownerDetail")) {
				JSONArray convertArray = new JSONArray();
				JSONArray tempOwnerDetail = json.getJSONArray("ownerDetail");
				boolean isShareFound = false;
				for (int i = 0; i < tempOwnerDetail.length(); i++) {
					JSONObject eachOwner = tempOwnerDetail.getJSONObject(i);

					if (eachOwner.has("ownerShare")) {
						String newShareValueInPerc = null;
						String share = eachOwner.getString("ownerShare");
						try {
							float sharfloat = Float.parseFloat(share);
							if (sharfloat <= 1) {
								newShareValueInPerc = sharfloat * 100+"";
								eachOwner.put("ownerShare", newShareValueInPerc);
								isShareFound = true;
							}
						} catch (Exception ex) {
							log.info(logtrcmsg + "ERROR : ownerShare is not in integer format " + share);
						}
						convertArray.put(eachOwner);
					}
				}

				if (isShareFound) {
					json.put("ownerDetail", convertArray);
					convertedJson = json;
					log.info(logtrcmsg + "Owner share convertion data :" + json);
				}
			}

		} catch (Exception exp) {
			log.info(logtrcmsg + "ERROR : Converting  Owners share " + exp.getMessage());
		}

		return convertedJson;
	}

	/**
	 * Convert the XML Response to JSON Object
	 * 
	 * @param xmlData
	 * @param lang
	 * @param logtrcmsg
	 * @return
	 */
	public JSONObject convertXMLToJSONObject(String xmlData, String logtrcmsg) {
		JSONObject json = null;
		try {
			XmlMapper xmlMapper = new XmlMapper();
			JsonNode node = xmlMapper.readTree(xmlData.getBytes());
			ObjectMapper mapper = new ObjectMapper();
			String resjson = mapper.writeValueAsString(node);
			log.debug(logtrcmsg + "Receieved json response :" + resjson);
			if (node.has("message")) {
				if (node.get("message").asText().equalsIgnoreCase("Data found successfully")) {
					JsonNode ownernode = node.get("ownerDetails");
					String ownerjson = mapper.writeValueAsString(ownernode);
					json = new JSONObject(ownerjson);
					JSONArray ownderDetail = null;
					try {
						ownderDetail = (JSONArray) json.getJSONArray("ownerDetail");

					} catch (Exception exp) {
						ownderDetail = new JSONArray();
						ownderDetail.put(json.getJSONObject("ownerDetail"));
					}
					// JSONArray ownderDetail = (JSONArray) json.getJSONArray("ownerDetail");
					log.debug(logtrcmsg + "Succcess : Owner/s Info found Data : " + json);
					// ownderDetail can be array
					JSONArray tempOwnerDetail = new JSONArray();
					if (json.has("ownerDetail")) {
						for (int i = 0; i < ownderDetail.length(); i++) {
							JSONObject eachOwner = (JSONObject) ownderDetail.get(i);
							eachOwner.put("flnel", eachOwner.get("ownerName"));
							eachOwner.put("flne", "");
							tempOwnerDetail.put(eachOwner);
						}
						json.put("ownerDetail", tempOwnerDetail);
					}
				} else {
					json = new JSONObject();
					json.put("message", node.get("message").asText());
				}
			}
		} catch (Exception e) {
			log.info(logtrcmsg + "ERROR : Retrieve method with Exception");
			log.debug(e);
		}
		return json;

	}

	/**
	 * This method performs the transliteration for the data object recieved
	 * 
	 * @param logtrcmsg
	 * @param state
	 * @param data
	 * @return
	 */
	private JSONObject performTansliterationNew(String logtrcmsg, String state, JSONObject data, String txncode)
			throws InvalidParamException {
		log.info(logtrcmsg + "PerformTansliteration input:" + data);
		JSONObject ownderDetail = null;
		JSONObject tempdata = data;
		JSONArray ownerDetailArray = null;
		if (data.has("ownerDetail")) {
			try {
				JSONArray tempOwnerDetailArray = new JSONArray();
				ownerDetailArray = (JSONArray) data.get("ownerDetail");
				boolean ownerFound = false;
				for (int i = 0; i < ownerDetailArray.length(); i++) {
					ownderDetail = ownerDetailArray.getJSONObject(i);
					if (ownderDetail.has("ownerName")) {
						ownerFound = true;
						ownderDetail.put("name", ownderDetail.get("ownerName"));
						tempOwnerDetailArray.put(ownderDetail);
						// data.put("ownerDetail", ownderDetail);
					} else {
						tempOwnerDetailArray = ownerDetailArray;
					}
				}
				if (ownerFound) {
					data.put("ownerDetail", tempOwnerDetailArray);
					String url = applicationConfig.getTranslitUrl();
					String userName = applicationConfig.getTranslitUser();
					String password = applicationConfig.getTranslitPassword();
					String srclang = getTransliterationSrc(state);
					Map<String, String> dataheaders = new HashMap<String, String>();
					dataheaders.put("src-lang", srclang);
					dataheaders.put("dest-lang", "en");
					JSONObject eachOwner = null;
					JSONArray tempNewOwnerDetailArray = new JSONArray();
					for (int i = 0; i < tempOwnerDetailArray.length(); i++) {
						eachOwner = tempOwnerDetailArray.getJSONObject(i);
						JSONObject inputData = new JSONObject();
						if (eachOwner.has("name")) {
							inputData.put("name", eachOwner.getString("name"));
						}
						if (eachOwner.has("address")) {
							inputData.put("address", eachOwner.getString("address"));
						}
						Map<String, Object> tempObj = Helper.getInstance().getValueMapTransliteration(inputData,
								logtrcmsg, txncode);
						JSONObject resp = Helper.getInstance().performPostMethod(logtrcmsg, url, userName, password,
								dataheaders, tempObj);
						if (resp != null) {

							if (resp.has("success")) {
								JSONObject actData = resp.getJSONObject("success");
								if (actData.has("name")) {
									eachOwner.put("flne", actData.get("name"));
									eachOwner.remove("name");
								}
								if (actData.has("address")) {
									eachOwner.put("address", actData.get("address"));
								}
								tempNewOwnerDetailArray.put(eachOwner);
							}
						}
						if (resp == null) {
							// Internal error Need to decide if this needs to be thrown
//							resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E025, tempdata,
//									applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
//									applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
						}
					}
					if (tempNewOwnerDetailArray.length() > 0) {
						tempdata.put("ownerDetail", tempNewOwnerDetailArray);
						log.info(logtrcmsg + "PerformTansliteration output :" + tempdata);
					}
				}
			} catch (InvalidParamException exp) {
				throw exp;
			} catch (Exception exp) {
				// Internal error Need to decide if this needs to be thrown
				log.info(logtrcmsg + "Error ocured PerformTansliteration :" + exp.getMessage());

			}

		}

		return tempdata;
	}

	/**
	 * This method performs the transliteration for the data object recieved
	 * 
	 * @param logtrcmsg
	 * @param state
	 * @param data
	 * @return
	 */
	private JSONObject performTansliteration(String logtrcmsg, String state, JSONObject data) {
		log.info(logtrcmsg + "PerformTansliteration input:" + data);
		JSONObject ownderDetail = null;
		JSONObject tempdata = data;
		JSONArray ownerDetailArray = null;
		if (data.has("ownerDetail")) {
			try {
				JSONArray tempOwnerDetailArray = new JSONArray();
				ownerDetailArray = (JSONArray) data.get("ownerDetail");
				boolean ownerFound = false;
				for (int i = 0; i < ownerDetailArray.length(); i++) {
					ownderDetail = ownerDetailArray.getJSONObject(i);
					if (ownderDetail.has("ownerName")) {
						ownerFound = true;
						ownderDetail.put("name", ownderDetail.get("ownerName"));
						tempOwnerDetailArray.put(ownderDetail);
						// data.put("ownerDetail", ownderDetail);
					} else {
						tempOwnerDetailArray = ownerDetailArray;
					}
				}
				if (ownerFound) {
					data.put("ownerDetail", tempOwnerDetailArray);
					RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
					String url = applicationConfig.getTranslitUrl();
					String srclang = getTransliterationSrc(state);
					url = url.replace("src_lang=hi", srclang);
					String userName = applicationConfig.getTranslitUser();
					String password = applicationConfig.getTranslitPassword();
					RestTemplate restTemplate = restTemplateBuilder.basicAuthentication(userName, password).build();
					Map<String, Object> tempObj = Helper.getInstance().getValueMapMP(data);
					Map<String, String> extraHeaders = null;
					JSONObject resp = Helper.getInstance().performPostMethod(logtrcmsg, url, userName, password,
							extraHeaders, tempObj);
					if (resp != null) {
						try {
							if (resp.has("success")) {
								JSONObject actData = resp.getJSONObject("success");
								if (actData.has("ownerDetail")) {
									JSONArray tempownerDetailArray = new JSONArray();
									ownerDetailArray = actData.getJSONArray("ownerDetail");
									for (int i = 0; i < ownerDetailArray.length(); i++) {
										JSONObject eachOwner = ownerDetailArray.getJSONObject(i);
										eachOwner.put("flne", eachOwner.get("name"));
										eachOwner.remove("name");
										tempownerDetailArray.put(eachOwner);
									}
									tempdata.put("ownerDetail", tempownerDetailArray);
									log.info(logtrcmsg + "PerformTansliteration output :" + tempdata);
								}
							}

						} catch (Exception exp) {

						}
					}
				}

			} catch (Exception exp) {
				log.info(logtrcmsg + "Error ocured PerformTansliteration :" + exp.getMessage());
			}
		}

		return tempdata;
	}

	/**
	 * This method with retrieve Owner data from MP LRS and construct response based
	 * on data received
	 */
	@Timed(value = "PersonService.retrieve.TN", description = "Time to complete the query")
	public JSONObject retrieveTNPerson(String logtrcmsg, String txncode, Integer distId, Integer talukcode,
			Integer villcode, Integer surveyno, String subdivno, Boolean lgd) {
		JSONObject resp = null;

		log.info(logtrcmsg + ": Request Recieved with distId:" + distId + "\ttalukcode:" + talukcode + "\tvillcode:"
				+ villcode + "\tsurveyno:" + surveyno + "\tsubdivno:" + subdivno + "\tlgd:" + lgd);

		boolean isValid = RequestDataValidations.getInstance().validatePersonTN(distId, talukcode, villcode, surveyno,
				subdivno, lgd);
		JSONObject tempdata = new JSONObject();
		if (!isValid) {
			resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E028, tempdata,
					applicationConfig.getSecretkey(), false, loadPrivateKey(), null,
					applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
		}
		/**
		 * Need to implement that logic to get owner data from TN LRS
		 */
		log.info(logtrcmsg + ": Request processed with response:" + resp);
		return resp;
	}

	/**
	 * This method prepares the Transliteration src lang to be set for
	 * Transliteration service
	 * 
	 * @param state
	 * @return
	 */
	private String getTransliterationSrc(String state) {
		switch (state) {
		case "madyapradesh":
			return "hi";
		case "tamilnadu":
			return "ta";
		default:
			return "hi";
		}
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

	/**
	 * This method is responsible to validate state and return response based on
	 * validation
	 * 
	 * @param logtrcmsg
	 * @param state
	 * @param txncode
	 * @return
	 * @throws SignatureException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public JSONObject validateState(String logtrcmsg, Integer state, String txncode)
			throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		JSONObject resp = null;
		boolean isValid = false;
		JSONObject data = new JSONObject();
		try {
			isValid = RequestDataValidations.getInstance().isValidState(state);

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
	 * This method is return failed response for non implemented state
	 * 
	 * @param logtrcmsg
	 * @param state
	 * @param txncode
	 * @return
	 * @throws SignatureException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public JSONObject failedResponseState(String logtrcmsg, Integer state, String txncode)
			throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		JSONObject resp = null;
		JSONObject data = new JSONObject();
		resp = CommonUtils.getInstance().getPlatformResponseObject(InvalidParamException.ErrorCodes.E039, data,
				applicationConfig.getSecretkey(), true, loadPrivateKey(), null,
				applicationConfig.getSigpassword().toCharArray(), txncode, logtrcmsg);
		log.info(logtrcmsg + ": Request processed with response:" + resp);
		return resp;
	}

}

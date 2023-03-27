package in.rbihub.controller;

import in.rbihub.service.PersonService;
import in.rbihub.service.TransliterationService;
import in.rbihub.utils.CommonUtils;
import in.rbihub.utils.ICommonMethods;
import in.rbihub.utils.PlatformConstants;
import io.swagger.annotations.ApiOperation;
import java.net.http.HttpHeaders;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {

	private PersonService personService = null;

	@Autowired
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	private TransliterationService transliterationService = null;

	@Autowired
	public void setTransliterationService(TransliterationService transliterationService) {
		this.transliterationService = transliterationService;
	}

	/**
	 * The controller method to get owners associated with the land from LRS system.
	 * For MP : The method requires khasraId, distId and TehId to get the owner
	 * record. For TN : The method requires distId,talukcode,villcode,surveyno,
	 * subdivno and lgd to get the owner record. version param is used to determine
	 * the version of the api lang value can be hi for hindi in which the name and
	 * address are recorded in local hindi language The lang can be set to en to
	 * indicate that transliteration service need to be used to convert name and
	 * address to English api_key , clientId along with txncode will be used to
	 * trace the transaction from clientId to particular transaction based on
	 * txncode Consent is the key parameter that determines if the consent is
	 * provided. It can take y/n If y is specified the consentId also needs to be
	 * provided for logging the consentId.
	 * 
	 * @PathVariable version
	 * @PathVariable lang
	 * @RequestHeader api_key
	 * @RequestHeader txncode
	 * @RequestHeader clientid
	 * @RequestHeader consent
	 * @RequestHeader consentId
	 * @RequestHeader khasraId
	 * @RequestHeader distId
	 * @RequestHeader tehId
	 * @RequestHeader talukcode
	 * @RequestHeader villcode
	 * @RequestHeader surveynyo
	 * @RequestHeader subdivno
	 * @RequestHeader lgd
	 * @parm request
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 */
	@ApiOperation(value = "To get list of land owners", response = String.class, code = 200)
	@GetMapping("/landowner/{version}/{lang}")
	public String retrieveLandOwner(@PathVariable("version") String version, @PathVariable("lang") String lang,
			@RequestHeader("api_key") String licensekey, @RequestHeader("txncode") String txncode,
			@RequestHeader("state") Integer state, @RequestHeader("clientid") String clientid,
			@RequestHeader("consent") String consent, @RequestHeader("consentId") String consentId,
			@RequestHeader("ts") String timestamp,
			@RequestHeader(value = "khasraId", required = false) String khasraId,
			@RequestHeader("distId") Integer distId, @RequestHeader(value = "tehId", required = false) String tehId,
			@RequestHeader(value = "talukcode", required = false) Integer talukcode,
			@RequestHeader(value = "villcode", required = false) Integer villcode,
			@RequestHeader(value = "surveyno", required = false) Integer surveyno,
			@RequestHeader(value = "subdivno", required = false) String subdivno,
			@RequestHeader(value = "lgd", required = false) Boolean lgd, HttpServletRequest request)
			throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		String logtraceMsg = "[ srcIP : " + request.getRemoteAddr() + ", clientId : " + clientid + ", apikey : "
				+ licensekey + ", txncode : " + txncode + "] ";
		/**
		 * Common validation for the URI Parameters are done below for version, txncode,
		 * lang, api-key and consent
		 */
		JSONObject response = null;
		response = personService.validateCommonURIParams(logtraceMsg, version, txncode, lang, licensekey, consent,
				consentId,  timestamp);
		if (response != null) {
			return response.toString();
		}
		response = personService.validateState(logtraceMsg, state, txncode);
		if (response != null) {
			return response.toString();
		}
		switch (state) {
		case PlatformConstants.STATECODE_MP:
			response = personService.retrieveMPPerson(logtraceMsg, lang, txncode, khasraId, distId, tehId);
			break;
		case PlatformConstants.STATECODE_TN:
			response = personService.failedResponseState(logtraceMsg, state, txncode);
			break;
		case PlatformConstants.STATECODE_MH:
			response = personService.failedResponseState(logtraceMsg, state, txncode);
			break;
		case PlatformConstants.STATECODE_UP:
			response = personService.failedResponseState(logtraceMsg, state, txncode);
			break;
		case PlatformConstants.STATECODE_OR:
			response = personService.failedResponseState(logtraceMsg, state, txncode);
			break;
		default:
			response = personService.failedResponseState(logtraceMsg, state, txncode);
		}
		return response.toString();

	}

	/**
	 * The controller method to perform transliteration on name and address send in
	 * headers
	 * 
	 * @PathVariable version
	 * @PathVariable srclang
	 * @PathVariable destlang
	 * @RequestHeader api_key
	 * @RequestHeader txncode
	 * @RequestHeader clientid
	 * @RequestHeader name
	 * @RequestHeader address
	 * @RequestHeader srclang
	 * @parm request
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 */
	@ApiOperation(value = "To perform  transliteration for provided name and address", response = String.class, code = 200)
	@PostMapping("/transliteration/{version}/{srclang}/{destlang}")
	public String performTransliteration(@PathVariable("version") String version,
			@PathVariable("srclang") String srclang, @PathVariable("destlang") String lang,
			@RequestHeader("Content-Type") String contenttype, @RequestHeader("Content-Length") Integer contentLength,
			@RequestHeader("api_key") String licensekey, @RequestHeader("txncode") String txncode,
			@RequestHeader("clientid") String clientid, @RequestBody(required = true) String jsonRequest,
			HttpServletRequest request) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		String logtraceMsg = "[ srcIP : " + request.getRemoteAddr() + ", clientId : " + clientid + ", apikey : "
				+ licensekey + ", txncode : " + txncode + "] ";
		/**
		 * Common validation for the URI Parameters are done below for version, txncode,
		 * lang, api-key and consent
		 */
		JSONObject response = null;
		int recievedContentLegnth = 0;
		if (jsonRequest != null) {
			recievedContentLegnth = jsonRequest.length();
		}
		response = transliterationService.validateCommonURPOSTLParams(logtraceMsg, version, lang, licensekey, txncode,
				contenttype, contentLength, recievedContentLegnth);
		if (response != null) {
			return response.toString();
		}
		response = transliterationService.validateRequestBody(logtraceMsg, jsonRequest, txncode);
		if (response != null) {
			return response.toString();
		} else {
			String name = null;
			String address = null;
			try {
				JSONObject input = new JSONObject(jsonRequest);
				JSONObject inputData = input.getJSONObject("data");
				name = inputData.getString("name");
				address = inputData.getString("address");

			} catch (Exception exp) {
				// not logging the error as the basic validation is already done
			}
			response = transliterationService.validateTransliterationInput(logtraceMsg, name, address, srclang, lang,
					txncode);
			if (response != null) {
				return response.toString();
			}

			response = transliterationService.performTransliteration(logtraceMsg, txncode, srclang, lang, name,
					address);

		}

		return response.toString();
	}

//	/**
//	 * The controller method to get owners associated with the land from TN LRS
//	 * system The method requires distId ,talukcode , villcode , surveyno subdivno
//	 * and lgd to get the owner record version param is used to determine the
//	 * version of the api lang value can be hi for hindi in which the name and
//	 * address are recorded in local hindi language The lang can be set to en to
//	 * indicate that transliteration service need to be used to convert name and
//	 * address to english api_key , clientId along with txncode will beb used to
//	 * trace the transaction from clientId to perticular transaction based on
//	 * txncode Consent is the key parameter that determines if the consent is
//	 * provided. It can take y/n
//	 * 
//	 * @param version
//	 * @param lang
//	 * @param licensekey
//	 * @param txncode
//	 * @param clientid
//	 * @param consent
//	 * @param distId
//	 * @param talukcode
//	 * @param villcode
//	 * @param surveyno
//	 * @param subdivno
//	 * @param lgd
//	 * @param request
//	 * @return
//	 * @throws InvalidKeyException
//	 * @throws NoSuchAlgorithmException
//	 * @throws SignatureException
//	 */
//	@ApiOperation(value = "To get list of land owners", response = Response.class, code = 200)
//	@GetMapping("/landowner/{version}/{lang}/tn")
//	public String retrieveTNPerson(@PathVariable("version") String version, @PathVariable("lang") String lang,
//			@RequestHeader("api_key") String licensekey, @RequestHeader("txncode") String txncode,
//			@RequestHeader("clientid") String clientid, @RequestHeader("consent") String consent,
//			@RequestHeader("consentId") String consentId, @RequestHeader("distId") Integer distId,
//			@RequestHeader("talukcode") Integer talukcode, @RequestHeader("villcode") Integer villcode,
//			@RequestHeader("surveyno") Integer surveyno, @RequestHeader("subdivno") String subdivno,
//			@RequestHeader("lgd") Boolean lgd, HttpServletRequest request)
//			throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
//		String logtraceMsg = "[ srcIP : " + request.getRemoteAddr() + ", clientId : " + clientid + ", apikey : "
//				+ licensekey + ", txncode : " + txncode + "] ";
//		/**
//		 * Common validation for the URI Parameters are done below for version, txncode,
//		 * lang, api-key and consent
//		 */
//		JSONObject failedresponse = personService.validateCommonURIParams(logtraceMsg, version, txncode, lang,
//				licensekey, consent, consentId);
//		if (failedresponse != null) {
//			return failedresponse.toString();
//		}
//		JSONObject response = personService.retrieveTNPerson(logtraceMsg, txncode, distId, talukcode, villcode,
//				surveyno, subdivno, lgd);
//		return response.toString();
//
//	}

	@GetMapping("/")
	public String index() {

		return "Greetings from RBiH - microservice!";
	}
}

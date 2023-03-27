package in.rbihub.utils;

import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import in.rbihub.error.InvalidParamException;
import in.rbihub.service.PersonService;

public class Helper {

	private static Helper helper = new Helper();

	public static Helper getInstance() {
		return helper;
	}

	private RestTemplate restTemplate = new RestTemplate();
	private static final Logger log = LogManager.getLogger(Helper.class);

	/**
	 * Prepare Value Map for MP to call transliteration service
	 * 
	 * @param data
	 * @return
	 */
	public Map<String, Object> getValueMapMP(JSONObject data) {
		Map<String, Object> map = new HashMap<>();
		try {
			ArrayList<Map<String, Object>> owners = new ArrayList<Map<String, Object>>();
			JSONArray ownerArray = data.getJSONArray("ownerDetail");
			for (int i = 0; i < ownerArray.length(); i++) {
				JSONObject eachOwnerJson = ownerArray.getJSONObject(i);
				Map<String, Object> eachOwner = new HashMap<>();
				Iterator<String> it = eachOwnerJson.keys();
				while (it.hasNext()) {
					String key = it.next();
					eachOwner.put(key, eachOwnerJson.get(key));

				}
				owners.add(eachOwner);
			}
			map.put("ownerDetail", owners);
		} catch (Exception exp) {

		}
		return map;
	}

	/**
	 * Prepare Value Map for transliteration service
	 * 
	 * @param data
	 * @return
	 */
	public Map<String, Object> getValueMapTransliteration(JSONObject data, String logtrcmsg, String txncode)
			throws InvalidParamException {
		Map<String, Object> bodyData = new HashMap<>();
		try {
			Iterator<String> it = data.keys();
			while (it.hasNext()) {
				String key = it.next();
				bodyData.put(key, data.get(key));
			}
		} catch (Exception exp) {
			log.info(logtrcmsg + ": Error with data send :" + data + "\terrorMessage:" + exp.getMessage());
			throw new InvalidParamException(InvalidParamException.ErrorCodes.E025,
					InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E025));
		}
		return bodyData;

	}

	public JSONObject performPostMethod(String logtrcmsg, String uri, String user, String password,
			Map<String, String> extraheaders, Map<String, Object> data) throws InvalidParamException {
		JSONObject result = null;
		try {
			// create headers
			HttpHeaders headers = new HttpHeaders();
			// set `content-type` header
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			// set `accept` header
			headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			
			String aarequestEncodedString = Base64.getEncoder().encodeToString((user + ":" + password).getBytes());
			headers.set("Authorization", "Basic " + aarequestEncodedString);
			// Add any extra headers
			if (extraheaders != null) {
				for (Map.Entry<String, String> entry : extraheaders.entrySet()) {
					headers.set(entry.getKey(), entry.getValue());
				}
			}
			// build the request
			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(data, headers);
			// send POST request
			ResponseEntity<String> response = restTemplate.postForEntity(uri, entity, String.class);

			// check response
			if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
				log.debug(logtrcmsg + "Request Successful: " + response.toString());
				result = new JSONObject(response.getBody());
			} else {
				log.info(logtrcmsg + "Transliteration Service Request Failed " + response.getStatusCode());
				throw new InvalidParamException(InvalidParamException.ErrorCodes.E031,
						InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E031));
			}
		} catch (InvalidParamException invExp) {
			throw invExp;

		} catch (Exception e) {
			log.info(logtrcmsg + "Transliteration Service Request with error: " + e.getMessage());
			throw new InvalidParamException(InvalidParamException.ErrorCodes.E031,
					InvalidParamException.getErrorDescription(InvalidParamException.ErrorCodes.E031));
		}
		return result;
	}
}

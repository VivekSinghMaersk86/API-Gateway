package in.rbihub.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import org.apache.catalina.Globals;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Assert;

import in.rbihub.config.ApplicationConfig;
import in.rbihub.error.InvalidParamException;
import in.rbihub.utils.PlatformConstants;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonServiceTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ApplicationConfig applicationConfig;

	@Autowired
	private PersonService personService;

	@Value("${spring.application.apiUser}")
	private String apiUser;

	@Value("${spring.application.apiPassword}")
	private String apiPassword;

	@Test
	public void validate_convertXMLToJSONObject() throws Exception {

		String xmlData = "<response>\n" + "    <message>Data found successfully</message>\n"
				+ "    <khasraId>118040200107207000995</khasraId>\n" + "    <riCircle>03-कायथा</riCircle>\n"
				+ "    <ownerDetails>\n" + "        <ownerDetail>\n"
				+ "            <AadharTokenNo>01001187VKWJWPj37A8DTkQ9Hl6EdmtE9gZLhODSlfDmhDtfy3qYCD85SpzHPrXIGJ6Zohhk</AadharTokenNo>\n"
				+ "            <ownerName>प्रेमसिंह  </ownerName>\n" + "            <fatherName>भारतसिंह</fatherName>\n"
				+ "            <address>मालखेड़ा तराना उज्जैन मध्य प्रदेश </address>\n"
				+ "            <ownerShare>1</ownerShare>\n"
				+ "            <ownershiptype>भूमि स्वामी</ownershiptype>\n"
				+ "            <ownerCaste>सामान्य</ownerCaste>\n" + "            <columnno>5</columnno>\n"
				+ "        </ownerDetail>\n" + "    </ownerDetails>\n" + "</response>";
		JSONObject data = personService.convertXMLToJSONObject(xmlData, "test log");
		Assert.isTrue(data.has("ownerDetail"), "ownerDetail is found");
		JSONArray ownerDetail = data.getJSONArray("ownerDetail");
		JSONObject eachOwner = ownerDetail.getJSONObject(0);
		Assert.isTrue(eachOwner.has("ownerName"), "ownerName is found");
		Assert.isTrue(eachOwner.get("ownerName").toString().equals("प्रेमसिंह  "), "ownerName is matched");

	}

	@Test
	public void validate_convertXMLToJSONObjectWithMultipleOwners() throws Exception {

		String xmlData = "<response>\n" + "    <message>Data found successfully</message>\n"
				+ "    <khasraId>118040200107207000995</khasraId>\n" + "    <riCircle>03-कायथा</riCircle>\n"
				+ "    <ownerDetails>\n" + "        <ownerDetail>\n"
				+ "            <AadharTokenNo>01001187VKWJWPj37A8DTkQ9Hl6EdmtE9gZLhODSlfDmhDtfy3qYCD85SpzHPrXIGJ6Zohhk</AadharTokenNo>\n"
				+ "            <ownerName>प्रेमसिंह  </ownerName>\n" + "            <fatherName>भारतसिंह</fatherName>\n"
				+ "            <address>मालखेड़ा तराना उज्जैन मध्य प्रदेश </address>\n"
				+ "            <ownerShare>1</ownerShare>\n"
				+ "            <ownershiptype>भूमि स्वामी</ownershiptype>\n"
				+ "            <ownerCaste>सामान्य</ownerCaste>\n" + "            <columnno>5</columnno>\n"
				+ "        </ownerDetail>\n" + "        <ownerDetail>\n"
				+ "            <AadharTokenNo>34134134134fwefwef</AadharTokenNo>\n"
				+ "            <ownerName>प्रेम</ownerName>\n" + "            <fatherName>भारतसिंह</fatherName>\n"
				+ "            <address>मालखेड़ा तराना उज्जैन मध्य प्रदेश </address>\n"
				+ "            <ownerShare>1</ownerShare>\n"
				+ "            <ownershiptype>भूमि स्वामी</ownershiptype>\n"
				+ "            <ownerCaste>सामान्य</ownerCaste>\n" + "            <columnno>5</columnno>\n"
				+ "        </ownerDetail>\n" + "    </ownerDetails>\n" + "</response>";
		JSONObject data = personService.convertXMLToJSONObject(xmlData, "test log");
		Assert.isTrue(data.has("ownerDetail"), "ownerDetail is found");
		JSONArray ownerDetailArray = (JSONArray) data.getJSONArray("ownerDetail");

		Assert.isTrue(((JSONObject) ownerDetailArray.get(0)).has("ownerName"), "ownerName is found");
		Assert.isTrue(((JSONObject) ownerDetailArray.get(0)).get("ownerName").toString().equals("प्रेमसिंह  "),
				"ownerName is matched");

	}

	@Test
	public void validateCommonURIParamsWithRightValues() throws Exception {
		String logtrcmsg = "testlog";
		String version = "1.0";
		String txncode = "123112332";
		String lang = "en";
		String apikey = "234134134143wwer23123122";
		String consent = "y";
		String consentId = "dq32342";
		OffsetDateTime offsetDT = OffsetDateTime.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");		 
		String timestamp =fmt.format(offsetDT);
		JSONObject response = personService.validateCommonURIParams(logtrcmsg, version, txncode, lang, apikey, consent,
				consentId,timestamp);

		Assert.isTrue(response == null, "response should be null ");

	}

	@Test
	public void validateCommonURIParamsWithWrongValues() throws Exception {
		String logtrcmsg = "testlog";
		String version = "1.0";
		String txncode = "123112332";
		String lang = "en";
		String apikey = "23123";
		String consent = "y";
		String consentId = "dq32342";
		OffsetDateTime offsetDT = OffsetDateTime.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");		 
		String timestamp =fmt.format(offsetDT);
		JSONObject response = personService.validateCommonURIParams(logtrcmsg, version, txncode, lang, apikey, consent,
				consentId,timestamp);

		Assert.isTrue(response != null, "responseis not  null ");
		Assert.isTrue(response.has("result"), "result should be present  ");
		JSONObject result = response.getJSONObject("result");
		Assert.isTrue(result.has("status"), "status should be present  ");
		String status = result.getString("status");
		Assert.isTrue(status.equals("failed"), "failed should match ");
		Assert.isTrue(result.has("errcode"), "errorcode should be present  ");
		String errcode = result.getString("errcode");
		Assert.isTrue(errcode.equals("E032"), "Error code should match ");
	}

	@Test
	public void validateCommonURIParamsWithoutConsentId() throws Exception {
		String logtrcmsg = "testlog";
		String version = "1.0";
		String txncode = "123112332";
		String lang = "en";
		String apikey = "234134134143wwer23123122";
		String consent = "y";
		String consentId = "";
		OffsetDateTime offsetDT = OffsetDateTime.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");		 
		String timestamp =fmt.format(offsetDT);
		JSONObject response = personService.validateCommonURIParams(logtrcmsg, version, txncode, lang, apikey, consent,
				consentId,timestamp);

		Assert.isTrue(response != null, "response is not  null ");
		Assert.isTrue(response.has("result"), "result should be present  ");
		JSONObject result = response.getJSONObject("result");
		Assert.isTrue(result.has("status"), "status should be present  ");
		String status = result.getString("status");
		Assert.isTrue(status.equals("failed"), "failed should match ");
		Assert.isTrue(result.has("errcode"), "errorcode should be present  ");
		String errcode = result.getString("errcode");
		Assert.isTrue(errcode.equals("E036"), "Error code should match ");
	}
	@Test
	public void validateretrieveMPPersonWithInvalidData() throws Exception {
		String logtrcmsg = "testlog";
		String version = "1.0";
		String txncode = "123112332";
		String lang = "en";
		String apikey = "234134134143WWER23123122";
		Integer distId = 510;
		String tehId = "04";
		String khasraId = "3242452413";
		JSONObject response = personService.retrieveMPPerson(logtrcmsg, lang, txncode, khasraId, distId, tehId);
		Assert.isTrue(response != null, "response is not  null ");
		Assert.isTrue(response.has("result"), "result should be present  ");
		JSONObject result = response.getJSONObject("result");
		Assert.isTrue(result.has("status"), "status should be present  ");
		String status = result.getString("status");
		Assert.isTrue(status.equals("failed"), "failed should match ");
		Assert.isTrue(result.has("errcode"), "errorcode should be present  ");
		String errcode = result.getString("errcode");
		Assert.isTrue(errcode.equals("E501"), "Error code should match ");
		
	}
	@Test
	public void validateStateWithValidValues() throws Exception {
		String logtrcmsg = "testlog";
		 Integer state = new Integer(PlatformConstants.STATECODE_MP);
		String txncode = "123112332";
		
		JSONObject response = personService.validateState(logtrcmsg, state, txncode);
		Assert.isTrue(response == null, "response is   null ");
		
	}
	
	@Test
	public void validateStateWithInValidValues() throws Exception {
		String logtrcmsg = "testlog";
		 Integer state = new Integer("2");
		String txncode = "123112332";
		
		JSONObject response = personService.validateState(logtrcmsg, state, txncode);
		Assert.isTrue(response != null, "response is not  null ");
		Assert.isTrue(response.has("result"), "result should be present  ");
		JSONObject result = response.getJSONObject("result");
		Assert.isTrue(result.has("status"), "status should be present  ");
		String status = result.getString("status");
		Assert.isTrue(status.equals("failed"), "success should match ");
		Assert.isTrue(result.has("errcode"), "errorcode should be present  ");
		String errcode = result.getString("errcode");
		Assert.isTrue(errcode.equals("E039"), "Error code should match ");
	}
	

}
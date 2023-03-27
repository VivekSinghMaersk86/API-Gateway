package in.rbihub.validation;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import org.apache.catalina.Globals;
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
//@WebMvcTest(value = Controller.class, secure = false)
public class RequestDataValidationsTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ApplicationConfig applicationConfig;

	@Value("${spring.application.apiUser}")
	private String apiUser;

	@Value("${spring.application.apiPassword}")
	private String apiPassword;

	@Test
	public void validate_isVersionValid_withRightValue() throws Exception {
		String version = "1.0";
		Assert.isTrue(RequestDataValidations.getInstance().isVersionValid(version), "version 1.0 is valid");

	}

	@Test
	public void validate_isVersionValid_withWrongValue_and_ProvidesProperErrorcode() throws Exception {
		String version = "2.0";
		try {
			RequestDataValidations.getInstance().isVersionValid(version);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E009),
					"Error code for version check should match");
		}

	}
	@Test
	public void validate_isContenttypeValid_withRightValue() throws Exception {
		String contenttype = PlatformConstants.SUPPORTED_JSON_CONTENTTYPE;
		Assert.isTrue(RequestDataValidations.getInstance().isContenttypeValid(contenttype), PlatformConstants.SUPPORTED_JSON_CONTENTTYPE+" is valid");

		
	}

	@Test
	public void validate_isContenttypeValid_withWrongValue_and_ProvidesProperErrorcode() throws Exception {
		String contenttype ="application/xml";
		try {
			RequestDataValidations.getInstance().isContenttypeValid(contenttype);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E014),
					"Error code for invalid contenttype check should match");
		}
		

	}
	
	@Test
	public void validate_isContentLengthValid_withRightValue() throws Exception {
		Integer contentLen = new Integer(10);
		int actLen = 10;
		Assert.isTrue(RequestDataValidations.getInstance().isContentLengthValid(contentLen, actLen), " Content length is valid");

		
	}

	@Test
	public void validate_isContentLengthValid_withWrongValue_and_ProvidesProperErrorcode() throws Exception {
		Integer contentLen = new Integer(10);
		int actLen = 20;
		try {
			RequestDataValidations.getInstance().isContentLengthValid(contentLen, actLen);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E046),
					"Error code for content length mismatch ");
		}
		

	}
	
	@Test
	public void validate_isTSValid_withRightValue() throws Exception {
		OffsetDateTime offsetDT = OffsetDateTime.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");		 
		String timestamp =fmt.format(offsetDT);

		Assert.isTrue(RequestDataValidations.getInstance().isTSValid(timestamp), " timestamp is valid");


	}

	@Test
	public void validate_iisTSValid_withWrongValue_and_ProvidesProperErrorcode() throws Exception {
		String timestamp = "2023-01-03T21:10:23+000";
	
		try {
			RequestDataValidations.getInstance().isTSValid(timestamp);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E010),
					"Error code for timestamp check should match");
		}

	}
	
	@Test
	public void validate_iisTSValid_OLDTimestamp_and_ProvidesProperErrorcode() throws Exception {
		String timestamp = "2023-03-19T07:35:07+0530";
	
		try {
			RequestDataValidations.getInstance().isTSValid(timestamp);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E044),
					"Error code for timestamp check should match");
		}

	}
	
	@Test
	public void validate_iisTSValid_OLDTimestamp_Like1970_and_ProvidesProperErrorcode() throws Exception {
		String timestamp = "1970-03-19T07:35:07+0530";
	
		try {
			RequestDataValidations.getInstance().isTSValid(timestamp);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E044),
					"Error code for timestamp check should match");
		}

	}

	@Test
	public void validate_isAPIKeyValid_withRightValue() throws Exception {
		String apikey = "234134134143wwer23123122";
		Assert.isTrue(RequestDataValidations.getInstance().isAPIKeyValid(apikey), "apikey is valid");

	}

	@Test
	public void validate_isAPIKeyValid_withWrongValue_and_ProvidesProperErrorcode() throws Exception {
		String apikey = "dfaadsfa213123";
		try {
			RequestDataValidations.getInstance().isAPIKeyValid(apikey);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E032),
					"Error code for apikey check should match");
		}

	}

	@Test
	public void validate_isLangISOValid_withRightValue() throws Exception {
		String lang = PlatformConstants.LANGISO_TAMIL;
		Assert.isTrue(RequestDataValidations.getInstance().isISOLangValid(lang), "lang is valid");

	}

	@Test
	public void validate_isLangISOValid_withWrongValue_and_ProvidesProperErrorcode() throws Exception {
		String lang = "engg";
		try {
			RequestDataValidations.getInstance().isISOLangValid(lang);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E033),
					"Error code for lang check should match");
		}

	}

	@Test
	public void validate_isLangValid_withRightValue() throws Exception {
		Integer lang = PlatformConstants.LANGCODE_TAMIL;
		Assert.isTrue(RequestDataValidations.getInstance().isLangValid(lang), "lang is valid");

	}

	@Test
	public void validate_isLangValid_withWrongValue_and_ProvidesProperErrorcode() throws Exception {
		Integer lang = 33;
		try {
			RequestDataValidations.getInstance().isLangValid(lang);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E033),
					"Error code for lang check should match");
		}

	}

	@Test
	public void validate_isTxncodeValid_withRightValue() throws Exception {
		String txncode = "34134133";
		Assert.isTrue(RequestDataValidations.getInstance().isTxncodeValid(txncode), "txncode is valid");

	}

	@Test
	public void validate_isTxncodeValid_withWrongValue_and_ProvidesProperErrorcode() throws Exception {
		String txncode = "";
		try {
			RequestDataValidations.getInstance().isTxncodeValid(txncode);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E034),
					"Error code for txncode check should match");
		}

	}

	@Test
	public void validate_isConsentValid_withRightValue() throws Exception {
		String consent = "y";
		Assert.isTrue(RequestDataValidations.getInstance().isConsentValid(consent), "consent is valid");

	}

	@Test
	public void validate_isConsentValid_withWrongValue_and_ProvidesProperErrorcode() throws Exception {
		String consent = "d";
		try {
			RequestDataValidations.getInstance().isConsentValid(consent);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E035),
					"Error code for consent check should match");
		}

	}

	@Test
	public void validate_isConsentIDValid_withRightValue() throws Exception {
		String consent = "y";
		String consentId = "21313123";
		Assert.isTrue(RequestDataValidations.getInstance().isConsentIDValid(consent, consentId), "consentId is valid");

	}

	@Test
	public void validate_isConsentIDValid_withWrongValue_and_ProvidesProperErrorcode() throws Exception {
		String consent = "y";
		String consentId = "";
		try {
			RequestDataValidations.getInstance().isConsentIDValid(consent, consentId);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E036),
					"Error code for consentId check should match");
		}

	}

	@Test
	public void validate_isValidDistrict_withRightValue() throws Exception {
		Integer distId = new Integer("18");
		Assert.isTrue(RequestDataValidations.getInstance().isValidDistrict(distId), "distId is valid");

	}

	@Test
	public void validate_isValidDistrict_withWrongValue_and_ProvidesProperErrorcode() throws Exception {
		Integer distId = new Integer("12312");
		try {
			RequestDataValidations.getInstance().isValidDistrict(distId);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E501),
					"Error code for distId check should match");
		}

	}

	@Test
	public void validate_isValidTehId_withRightValue() throws Exception {
		String tehId = "04";
		Assert.isTrue(RequestDataValidations.getInstance().isValidTehId(tehId), "distId is valid");

	}

	@Test
	public void validate_isValidTehId_withWrongValue_and_ProvidesProperErrorcode() throws Exception {
		String tehId = "04DESS";
		try {
			RequestDataValidations.getInstance().isValidTehId(tehId);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E502),
					"Error code for tehId check should match");
		}

	}

	@Test
	public void validate_isValidKhasaraId_withRightValue() throws Exception {
		String khasraId = "8734823480";
		Assert.isTrue(RequestDataValidations.getInstance().isValidKhasaraId(khasraId), "khasraId is valid");

	}

	@Test
	public void validate_isValidKhasaraId_withWrongValue_and_ProvidesProperErrorcode() throws Exception {
		String khasraId = "04DESS";
		try {
			RequestDataValidations.getInstance().isValidKhasaraId(khasraId);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E503),
					"Error code for khasraId check should match");
		}

	}

	@Test
	public void validate_validateCommonURLParams_withRightValue() throws Exception {
		String version = "1.0";
		String apikey = "234134134143wwer23123122";
		// Integer lang = PlatformConstants.LANGCODE_ENGLISH;
		String lang = PlatformConstants.LANGISO_ENGLISH;
		String txncode = "132123";
		String consent = "y";
		String consentId = "e13e13";
		OffsetDateTime offsetDT = OffsetDateTime.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");		 
		String timestamp =fmt.format(offsetDT);
		Assert.isTrue(RequestDataValidations.getInstance().validateCommonURLParams(version, apikey, lang, txncode,
				consent, consentId,timestamp), "common uri params is valid");

	}

	@Test
	public void validate_validateCommonURLParams_withWrongValue_and_ProvidesProperErrorcode() throws Exception {

		String version = "2.0"; // wrong value
		String apikey = "3123123";
		// Integer lang = PlatformConstants.LANGCODE_ENGLISH;
		String lang = PlatformConstants.LANGISO_ENGLISH;
		String txncode = "132123";
		String consent = "y";
		String consentId = "e13e13";
		OffsetDateTime offsetDT = OffsetDateTime.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");		 
		String timestamp =fmt.format(offsetDT);
		try {
			RequestDataValidations.getInstance().validateCommonURLParams(version, apikey, lang, txncode, consent,
					consentId,timestamp);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E009),
					"Error code for common uri params -  version check should match");
		}

	}

	@Test
	public void validate_isValidName_WithRightValue() throws Exception {
		String name = "Testname";
		String lang = "en";
		Assert.isTrue(RequestDataValidations.getInstance().isValidName(name, lang), "name in english");

		name = "प्रेमसिंह भारतसिंह";
		lang = "hi";
		Assert.isTrue(RequestDataValidations.getInstance().isValidName(name, lang), "name in hindi");

		name = "நிஷாந்தி ஜெய்சுக்";
		lang = "tn";
		Assert.isTrue(RequestDataValidations.getInstance().isValidName(name, lang), "name in tamil");
	}
//  This test case is commented as the limit is removed for name and address field	
//	@Test
//	public void  validate_isValidName_WithLargenameValue() throws Exception{
//		String name = "you need to escape the backslash if you are creating your RegExp object from a string literasdsadsdsadsd";
//		String lang = "en";
//		try {RequestDataValidations.getInstance().isValidName(name,lang);
//		} catch (InvalidParamException exp) {
//			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E176),
//					"Error code for name");
//		}
//	}

	@Test
	public void validate_isValidName_SrcLangEnglishWithdifferenthindiString() throws Exception {
		String name = "प्रेमसिंह";
		String lang = "en";
		try {
			RequestDataValidations.getInstance().isValidName(name, lang);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E177), "Error code for name");
		}
	}

	@Test
	public void validate_isValidName_SrcLangEnglishWithdifferentTamilString() throws Exception {
		String name = "Prem நிஷாந்தி";
		String lang = "en";
		try {
			RequestDataValidations.getInstance().isValidName(name, lang);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E177), "Error code for name");
		}
	}

	@Test
	public void validate_isValidName_SrcLangHindiWithdifferentTamilString() throws Exception {
		String name = "நிஷாந்தி";
		String lang = "hi";
		try {
			RequestDataValidations.getInstance().isValidName(name, lang);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E178), "Error code for name");
		}
	}

	@Test
	public void validate_isValidName_SrcLangHindiWithdifferentEnglishString() throws Exception {
		String name = "test me";
		String lang = "hi";
		try {
			RequestDataValidations.getInstance().isValidName(name, lang);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E178), "Error code for name");
		}
	}

	@Test
	public void validate_isValidName_SrcLangTamilWithdifferentHindiString() throws Exception {
		String name = "प्रेमसिंह";
		String lang = "tn";
		try {
			RequestDataValidations.getInstance().isValidName(name, lang);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E179), "Error code for name");
		}
	}

	@Test
	public void validate_isValidName_SrcLangTamilWithdifferentEnglishString() throws Exception {
		String name = "test me";
		String lang = "tn";
		try {
			RequestDataValidations.getInstance().isValidName(name, lang);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E179), "Error code for name");
		}
	}

	@Test
	public void validate_isValidSrcAndDestLang_RightLang() throws Exception {
		String src = "en";
		String dest = "tn";
		Assert.isTrue(RequestDataValidations.getInstance().isValidSrcAndDestLang(src, dest),
				"right en to tn combination");
		Assert.isTrue(RequestDataValidations.getInstance().isValidSrcAndDestLang(dest, src),
				"right tn to en combination");
		src = "en";
		dest = "hi";
		Assert.isTrue(RequestDataValidations.getInstance().isValidSrcAndDestLang(src, dest),
				"right en to hi combination");
		Assert.isTrue(RequestDataValidations.getInstance().isValidSrcAndDestLang(dest, src),
				"right hi to en combination");
	}

	@Test
	public void validate_isValidSrcAndDestLang_WithInvalidSRC() throws Exception {
		String src = "eng1";
		String dest = "tn";
		try {
			RequestDataValidations.getInstance().isValidSrcAndDestLang(src, dest);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E180), "Error code for name");
		}
		src = "hi";
		dest = "tn";

		try {
			RequestDataValidations.getInstance().isValidSrcAndDestLang(src, dest);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E181), "Error code for name");
		}
	}

	@Test
	public void validate_isValidSrcAndDestLang_WithInvalidDest() throws Exception {
		String src = "tn";
		String dest = "sdas";

		try {
			RequestDataValidations.getInstance().isValidSrcAndDestLang(src, dest);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E033), "Error code for name");
		}
	}

	@Test
	public void validate_isValidSrcAndDestLang_WithSameSrcDest() throws Exception {
		String src = "tn";
		String dest = "tn";
		try {
			RequestDataValidations.getInstance().isValidSrcAndDestLang(src, dest);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E181), "Error code for name");
		}
	}
	
	@Test
	public void validate_isValidAddress_SrcLangEnglishWithdifferenthindiString() throws Exception {
		String address = "प्रेमसिंह";
		String lang = "en";
		try {
			RequestDataValidations.getInstance().isValidAddress(address, lang);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E183), "Error code for address");
		}
	}

	@Test
	public void validate_isValidAddress_SrcLangEnglishWithdifferentTamilString() throws Exception {
		String address = "Prem நிஷாந்தி";
		String lang = "en";
		try {
			RequestDataValidations.getInstance().isValidAddress(address, lang);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E183), "Error code for address");
		}
	}

	@Test
	public void validate_isValidAddress_SrcLangHindiWithdifferentTamilString() throws Exception {
		String address = "நிஷாந்தி";
		String lang = "hi";
		try {
			RequestDataValidations.getInstance().isValidAddress(address, lang);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E184), "Error code for name");
		}
	}

	@Test
	public void validate_isValidAddress_SrcLangHindiWithdifferentEnglishString() throws Exception {
		String address = "test me";
		String lang = "hi";
		try {
			RequestDataValidations.getInstance().isValidAddress(address, lang);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E184), "Error code for name");
		}
	}

	@Test
	public void validate_isValidAddress_SrcLangTamilWithdifferentHindiString() throws Exception {
		String address = "प्रेमसिंह";
		String lang = "tn";
		try {
			RequestDataValidations.getInstance().isValidAddress(address, lang);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E185), "Error code for name");
		}
	}

	@Test
	public void validate_isValidAddress_SrcLangTamilWithdifferentEnglishString() throws Exception {
		String address = "test me";
		String lang = "tn";
		try {
			RequestDataValidations.getInstance().isValidAddress(address, lang);
		} catch (InvalidParamException exp) {
			Assert.isTrue(exp.getErrorCode().equals(InvalidParamException.ErrorCodes.E185), "Error code for name");
		}
	}
	
}

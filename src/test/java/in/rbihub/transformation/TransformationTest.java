package in.rbihub.transformation;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

import in.rbihub.config.ApplicationConfig;
import in.rbihub.error.InvalidParamException;
import in.rbihub.validation.RequestDataValidations;

@SpringBootTest
@AutoConfigureMockMvc
public class TransformationTest {
	
	@Autowired
	private MockMvc mvc;

	
	@Autowired
	private Transformation transformation;


	@Test
	public void validate_init() throws Exception {
		String version = "1.0";
		Assert.isTrue(!transformation.getTransformationSpecMP().isEmpty(), "initialized properly");

	}

	@Test
	public void validate_transformMPL() throws Exception {
		String mpData = "{\n"
				+ "  \"ownerDetail\" : [ {\n"
				+ "    \"columnno\" : \"5\",\n"
				+ "    \"fatherName\" : \"भारतसिंह\",\n"
				+ "    \"ownershiptype\" : \"भूमि स्वामी\",\n"
				+ "    \"address\" : \"malkhena taraana ujjain madhya pradesh\",\n"
				+ "    \"ownerName\" : \"प्रेमसिंह  \",\n"
				+ "    \"ownerShare\" : \"1\",\n"
				+ "    \"ownerCaste\" : \"सामान्य\",\n"
				+ "    \"AadharTokenNo\" : \"01001187VKWJWPj37A8DTkQ9Hl6EdmtE9gZLhODSlfDmhDtfy3qYCD85SpzHPrXIGJ6Zohhk\",\n"
				+ "    \"flne\" : \"premsingh\",\n"
				+ "    \"flnel\" : \"प्रेमसिंह  \"\n"
				+ "  } ]\n"
				+ "}";
		JSONObject mpDataSimulated = new JSONObject(mpData);
		JSONObject transformedData = transformation.transformMPL(mpDataSimulated,"testlog");
		Assert.isTrue(transformedData.has("ownerDetail"), "ownerDetail is found");
		JSONArray ownerDetails = transformedData.getJSONArray("ownerDetail");
		JSONObject oneOwner = ownerDetails.getJSONObject(0);
		Assert.isTrue(oneOwner.has("fullname"), "fullname is found");
		Assert.isTrue(!oneOwner.has("ownerShare"), "ownerShare should not be found");
		Assert.isTrue(oneOwner.has("heirpcshr"), "heirpcshr should  be found");
	}
	
	@Test
	public void validate_transformMPLWithFewData() throws Exception {
		String mpData = "{\n"
				+ "  \"ownerDetail\" : [ {\n"
				+ "    \"columnno\" : \"5\",\n"
				+ "    \"ownerShare\" : \"1\",\n"
				+ "    \"ownerCaste\" : \"सामान्य\",\n"
				+ "    \"AadharTokenNo\" : \"01001187VKWJWPj37A8DTkQ9Hl6EdmtE9gZLhODSlfDmhDtfy3qYCD85SpzHPrXIGJ6Zohhk\",\n"
				+ "    \"flne\" : \"premsingh\",\n"
				+ "    \"flnel\" : \"प्रेमसिंह  \"\n"
				+ "  } ]\n"
				+ "}";
		JSONObject mpDataSimulated = new JSONObject(mpData);
		JSONObject transformedData = transformation.transformMPL(mpDataSimulated,"testlog");
		Assert.isTrue(transformedData.has("ownerDetail"), "ownerDetail is found");
		JSONArray ownerDetails = transformedData.getJSONArray("ownerDetail");
		JSONObject oneOwner = ownerDetails.getJSONObject(0);
		Assert.isTrue(oneOwner.has("fullname"), "flne is found");
		Assert.isTrue(!oneOwner.has("ownerShare"), "ownerShare should not be found");
		Assert.isTrue(oneOwner.has("heirpcshr"), "heirpcshr should  be found");
	}
	
	@Test
	public void validate_withWrongData() throws Exception {
		String mpData = "{\n"
				+ "  \"ownerDetailForMP{\" : [ {\n"
				+ "    \"columnno\" : \"5\",\n"
				+ "    \"fatherName\" : \"भारतसिंह\",\n"
				+ "    \"ownershiptype\" : \"भूमि स्वामी\",\n"
				+ "    \"address\" : \"malkhena taraana ujjain madhya pradesh\",\n"
				+ "    \"ownerName\" : \"प्रेमसिंह  \",\n"
				+ "    \"ownerShare\" : \"1\",\n"
				+ "    \"ownerCaste\" : \"सामान्य\",\n"
				+ "    \"AadharTokenNo\" : \"01001187VKWJWPj37A8DTkQ9Hl6EdmtE9gZLhODSlfDmhDtfy3qYCD85SpzHPrXIGJ6Zohhk\",\n"
				+ "    \"flne\" : \"premsingh\",\n"
				+ "    \"flnel\" : \"प्रेमसिंह  \"\n"
				+ "  } ]\n"
				+ "}";
		JSONObject mpDataSimulated = new JSONObject(mpData);
		JSONObject transformedData = transformation.transformMPL(mpDataSimulated,"testlog");
		Assert.isTrue(!transformedData.has("ownerDetail"), "ownerDetail is not  found");
	
	}
	
	@Test
	public void validate_transformMPLWithMultipleArrayData() throws Exception {
		String mpData = "{\n"
				+ "  \"ownerDetail\" : [ {\n"
				+ "    \"columnno\" : \"5\",\n"
				+ "    \"fatherName\" : \"भारतसिंह\",\n"
				+ "    \"ownershiptype\" : \"भूमि स्वामी\",\n"
				+ "    \"address\" : \"malkhena taraana ujjain madhya pradesh\",\n"
				+ "    \"ownerName\" : \"प्रेमसिंह  \",\n"
				+ "    \"ownerShare\" : \"1\",\n"
				+ "    \"ownerCaste\" : \"सामान्य\",\n"
				+ "    \"AadharTokenNo\" : \"01001187VKWJWPj37A8DTkQ9Hl6EdmtE9gZLhODSlfDmhDtfy3qYCD85SpzHPrXIGJ6Zohhk\",\n"
				+ "    \"flne\" : \"premsingh\",\n"
				+ "    \"flnel\" : \"प्रेमसिंह  \"\n"
				+ "  },{\n"
				+ "    \"columnno\" : \"2\",\n"
				+ "    \"fatherName\" : \"भारतसिंह\",\n"
				+ "    \"ownershiptype\" : \"भूमि स्वामी\",\n"
				+ "    \"address\" : \"malkhena taraana ujjain madhya pradesh\",\n"
				+ "    \"ownerName\" : \"प्रेमसिंह  \",\n"
				+ "    \"ownerShare\" : \"1\",\n"
				+ "    \"ownerCaste\" : \"सामान्य\",\n"
				+ "    \"AadharTokenNo\" : \"01001187VKWJWPj37A8DTkQ9Hl6EdmtE9gZLhODSlfDmhDtfy3qYCD85SpzHPrXIGJ6Zohhk\",\n"
				+ "    \"flne\" : \"premssingh1\",\n"
				+ "    \"flnel\" : \"प्रेमसिंह1\"\n"
				+ "  } ]\n"
				+ "}";
		JSONObject mpDataSimulated = new JSONObject(mpData);
		JSONObject transformedData = transformation.transformMPL(mpDataSimulated,"testlog");
		Assert.isTrue(transformedData.has("ownerDetail"), "ownerDetail is found");
		JSONArray ownerDetails = transformedData.getJSONArray("ownerDetail");
		Assert.isTrue(ownerDetails.length()>1, "should have more owners ");
		JSONObject oneOwner = ownerDetails.getJSONObject(0);
		
		Assert.isTrue(oneOwner.has("fullname"), "flne is found");
		Assert.isTrue(!oneOwner.has("ownerShare"), "ownerShare should not be found");
		Assert.isTrue(oneOwner.has("heirpcshr"), "heirpcshr should  be found");
		
	}

}

package in.rbihub.transformation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import in.rbihub.service.PersonService;

@Component
public class Transformation {

	private static final Logger log = LogManager.getLogger(Transformation.class);
	
	private static final String LRS_MP_SPEC_JSON = "LRS-MP-spec.json";
	public static void main(String[] args) {
		Transformation trans = new Transformation();
		trans.transforData();

	} 
	private List<Object> transformationSpecMP = null;
	/**
	 * Getter methods for transformation spec 0f MP
	 * @return
	 */
	public List<Object> getTransformationSpecMP() {
		return transformationSpecMP;
	}
	public Transformation() {
		initMPTranformation();
	} 
    private void initMPTranformation() {
    	if(transformationSpecMP ==null) {
    		String testjson = loadFileContent(LRS_MP_SPEC_JSON);
    		if(testjson ==null) {
    			log.info("LRS-MP-spec not found");
    			testjson = loadFileContent("src/main/resources/"+LRS_MP_SPEC_JSON);
    			
    		}
    		JsonElement specDataJson = JsonParser.parseString(testjson);
    		log.info("Spec json :" + specDataJson);
    		transformationSpecMP = JsonUtils.jsonToList(specDataJson.toString());
    	}
    	
    } 
	private String loadFileContent(String filepath) {
		String fileContent = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filepath));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			fileContent = sb.toString();
		} catch (Exception exp) {
			//exp.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception ex) {

				}
			}

		}
		return fileContent;

	}

	public void transforData() {
		String tansformfile = loadFileContent("src/main/resources/LRS-MP-spec.json");
		//String testjson = loadFileContent("src/main/resources/testData2.json");
		String testjson = loadFileContent("src/main/resources/testDatq3.json");
		jsonTransformComplete(testjson, tansformfile);
	}

	public JsonObject jsonTransformComplete(String jsonRawData, String jsonspecfirstleveltrans) {
		System.out.println("jsonTransformComplete raw json : " + jsonRawData);
		JsonElement inputDataJson = JsonParser.parseString(jsonRawData);
		Object inputJSON = JsonUtils.jsonToObject(inputDataJson.toString());
		List chainrSpecJSON = null;
		JsonElement specDataJson = null;
		Chainr chainr = null; 
		Object transformedOutput = null;

		JsonObject ownerDetails = null;

		String newspec = jsonspecfirstleveltrans;
		specDataJson = JsonParser.parseString(newspec);
		System.out.println("Spec json :" + specDataJson);
		chainrSpecJSON = JsonUtils.jsonToList(specDataJson.toString());

		chainr = Chainr.fromSpec(chainrSpecJSON);
		System.out.println("Input json :" + inputJSON);
		transformedOutput = chainr.transform(inputJSON);

		System.out.println("transformed output " + transformedOutput);
		JsonElement toJson = JsonParser.parseString(JsonUtils.toJsonString(transformedOutput));
		ownerDetails = toJson.getAsJsonObject();

		if (ownerDetails != null) {
			System.out.println(ownerDetails);
		}
		return ownerDetails;
	}
	/***
	 * Transformation for MP LRS to normalized based on CDS data
	 * @param jsonRawData
	 * @param logtraceMsg
	 * @return
	 */
	public JSONObject transformMPL(JSONObject jsonRawData,String logtraceMsg) {
		log.debug( logtraceMsg + "Json Data for transform  :" + jsonRawData);
		JsonElement inputDataJson = JsonParser.parseString(jsonRawData.toString());
		Object inputJSON = JsonUtils.jsonToObject(inputDataJson.toString());
		Chainr chainr = null;
		Object transformedOutput = null;
        JSONObject returnData = jsonRawData;
		JsonObject ownerDetails = null;
		if(transformationSpecMP ==null) {
			initMPTranformation();
		}
		chainr = Chainr.fromSpec(transformationSpecMP);
		transformedOutput = chainr.transform(inputJSON);
		log.debug( logtraceMsg + "Tranformed output  :" + transformedOutput);
		JsonElement toJson = JsonParser.parseString(JsonUtils.toJsonString(transformedOutput));
		if(toJson.isJsonNull()) {
			return jsonRawData;
		}
		ownerDetails = toJson.getAsJsonObject();
		if (ownerDetails != null) {
			log.debug(logtraceMsg +"Tranformed Data !! :" + ownerDetails);
			try {
				returnData = new JSONObject(ownerDetails.toString());
				log.info( logtraceMsg + "JSONObject data :" + returnData);
			}catch (Exception exp) {
				log.info( logtraceMsg + "Failed to convert to JSONObject  :" + ownerDetails.getAsString());
			}
			
		}
		return returnData;
	}

}

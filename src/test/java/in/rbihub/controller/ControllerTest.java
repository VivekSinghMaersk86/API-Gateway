package in.rbihub.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Base64;
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
import in.rbihub.config.ApplicationConfig;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(value = Controller.class, secure = false)
public class ControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ApplicationConfig applicationConfig;
	
	@Value("${spring.application.apiUser}")
    private String apiUser;

    @Value("${spring.application.apiPassword}")
    private String apiPassword;

	@Test
	public void getHello() throws Exception {
		String aarequestEncodedString = Base64.getEncoder().encodeToString((apiUser + ":" + apiPassword).getBytes());
		//headers.set("Authorization", "Basic " + aarequestEncodedString);
		 mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON).header("Authorization", "Basic " + aarequestEncodedString))
		 		.andExpect(status().isOk())
		 		.andExpect(content().string(equalTo("Greetings from RBiH - microservice!")));
	}
}

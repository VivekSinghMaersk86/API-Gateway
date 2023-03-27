package in.rbihub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import in.rbihub.config.ApplicationConfig;

@SpringBootTest
@AutoConfigureMockMvc
public class TransliterationTest {
	
	
	@Autowired
	private MockMvc mvc;

	@Autowired
	private ApplicationConfig applicationConfig;

	@Autowired
	private TransliterationService transliterationService;

	@Value("${spring.application.apiUser}")
	private String apiUser;

	@Value("${spring.application.apiPassword}")
	private String apiPassword;

}

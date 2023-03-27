package in.rbihub.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Value;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
//import javax.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "app")
@Configuration
@Data
@Validated
public class ApplicationConfig {
	// @NotBlank
	@Value("${app.mp.url}")
	private String mpUrl;

	// @NotBlank
	@Value("${app.mp.user}")
	private String mpUser;

	// @NotBlank
	@Value("${app.mp.password}")
	private String mpPassword;

	@Value("${app.mp.GetOwner}")
	private String mpGetOwner;

	@Value("${app.secretkey}")
	private String secretkey;

	public String getSecretkey() {
		return secretkey;
	}

	public String getPrivatekey() {
		return privatekey;
	}

	@Value("${app.privatekey}")
	private String privatekey;

	
	@Value("${app.publickeypath}")
	private String publickeypath;
	
	public String getPublickeypath() {
		return publickeypath;
	}

	@Value("${app.sigpassword}")
	private String sigpassword;

	// @NotBlank
	@Value("${app.transliteration.url}")
	private String translitUrl;

	// @NotBlank
	@Value("${app.transliteration.user}")
	private String translitUser;
	public String getTranslitUrl() {
		return translitUrl;
	}

	public String getTranslitUser() {
		return translitUser;
	}

	public String getTranslitPassword() {
		return translitPassword;
	}

	// @NotBlank
	@Value("${app.transliteration.password}")
	private String translitPassword;

	public String getSigpassword() {
		return sigpassword;
	}

	public String getMpGetOwner() {
		return mpGetOwner;
	}

	public String getMpPassword() {
		return mpPassword;
	}

	public String getMpUrl() {
		return mpUrl;
	}

	public String getMpUser() {
		return mpUser;
	}

	@Bean
	public TimedAspect timedAspect(MeterRegistry registry) {
		return new TimedAspect(registry);
	}

}
package in.rbihub;

import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import in.rbihub.config.PropertiesLogger;
import io.prometheus.client.Counter;

@SpringBootApplication
//@EnablePrometheusScraping
public class Application {
	static final Counter requests = Counter.build()
		     .name("requests_total").help("Total requests.").register();
	public static void main(String[] args) {
		//SpringApplication.run(Application.class, args);
		SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.addListeners(new PropertiesLogger());
        springApplication.run(args);
		requests.inc();
	}
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				//System.out.println(beanName);
			}
		};
	}


}

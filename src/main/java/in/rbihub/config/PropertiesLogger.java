package in.rbihub.config;


import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PropertiesLogger implements ApplicationListener<ApplicationPreparedEvent> {
	private static final Logger log = LogManager.getLogger(PropertiesLogger.class);

	private ConfigurableEnvironment environment;
	private boolean isFirstRun = true;


	@Override
	public void onApplicationEvent(ApplicationPreparedEvent event) {
		if (isFirstRun) {
			environment = event.getApplicationContext().getEnvironment();
			printProperties();
		}
		isFirstRun = false;
	}

	public void printProperties() {
		for (EnumerablePropertySource propertySource : findPropertiesPropertySources()) {
			log.info("******* " + propertySource.getName() + " *******");
			String[] propertyNames = propertySource.getPropertyNames();
			//for(int i=0; i< propertyNames.length; i++ ) {
			//	log.info("Dump\t"+propertyNames[i]+"="+environment.getProperty(propertyNames[i]));
			//}
			if (propertySource.containsProperty("app.propertiesdump")
					&& environment.getProperty("app.propertiesdump").equalsIgnoreCase("true")) {
				Arrays.sort(propertyNames);
				for (String propertyName : propertyNames) {
					String resolvedProperty = environment.getProperty(propertyName);
					String sourceProperty = propertySource.getProperty(propertyName).toString();
					if (resolvedProperty.equals(sourceProperty)) {
							log.info("{}={}", propertyName, resolvedProperty);
					} else {						
							log.info("{}={} OVERRIDDEN to {}", propertyName, sourceProperty, resolvedProperty);
					}
				}
			}
		}
	}

	private List<EnumerablePropertySource> findPropertiesPropertySources() {
		List<EnumerablePropertySource> propertiesPropertySources = new LinkedList<>();
		for (PropertySource<?> propertySource : environment.getPropertySources()) {
			if (propertySource instanceof EnumerablePropertySource) {
				propertiesPropertySources.add((EnumerablePropertySource) propertySource);
			}
		}
		return propertiesPropertySources;
	}
}

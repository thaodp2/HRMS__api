package com.minswap.hrms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextListener;

import com.minswap.hrms.exception.handler.RestTemplateErrorHandler;

/**
 * The Class EWcashbackserviceApplication.
 */
@SpringBootApplication
public class HRMSApplication {

	private static Logger logger = LoggerFactory.getLogger(HRMSApplication.class);

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
//		SpringApplication.run(HRMSApplication.class, args);
		SpringApplication app = new SpringApplication(HRMSApplication.class);
		Environment env = app.run(args).getEnvironment();
		logApplicationStartup(env);
	}
	@Autowired
	private RestTemplateBuilder restTemplateBuilder;
	/**
	 * Gets the rest template.
	 *
	 * @return the rest template
	 */
	@Bean(name = "restTemplate")
	RestTemplate getRestTemplate() {
		return restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(10)).setReadTimeout(Duration.ofSeconds(10))
				.errorHandler(new RestTemplateErrorHandler())
				.build();
	}
	@Bean public RequestContextListener requestContextListener(){
		return new RequestContextListener();
	}
	
	@Bean
	public javax.validation.Validator validator() {
		final LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
		factory.setValidationMessageSource(messageSource());
		return factory;
	}

	private static void logApplicationStartup(Environment env) {
		String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");
		String serverPort = env.getProperty("server.port");
		String contextPath = Optional
				.ofNullable(env.getProperty("server.servlet.context-path"))
				.filter(StringUtils::isNotBlank)
				.orElse("/");
		String hostAddress = "localhost";
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.warn("The host name could not be determined, using `localhost` as fallback");
		}
		logger.info("----------------------------------------------------------");
		logger.info("Application '{}' is running! Access URLs:",env.getProperty("spring.application.name"));
		logger.info("Local:  {}://localhost:{}{} ",
				protocol,
				serverPort,
				contextPath );
		logger.info("External: {}://{}:{}{} ",
				protocol,
				hostAddress,
				serverPort,
				contextPath);
		logger.info("Profile(s): {}",env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles());
		logger.info("----------------------------------------------------------");
	}

	/**
	 * Message source.
	 *
	 * @return the reloadable resource bundle message source
	 */
	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:/messages/message");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

}

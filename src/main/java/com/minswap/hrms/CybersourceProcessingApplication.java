package com.minswap.hrms;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextListener;

import com.minswap.hrms.exception.handler.RestTemplateErrorHandler;

/**
 * The Class EWcashbackserviceApplication.
 */
@SpringBootApplication
@ComponentScan("com.minswap.hrms")
public class CybersourceProcessingApplication {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(CybersourceProcessingApplication.class, args);
	}

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

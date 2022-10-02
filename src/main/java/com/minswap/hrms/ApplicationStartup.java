package com.minswap.hrms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
  private Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

  @Override
  public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
	  logger.info("ApplicationReadyEvent: application is up");
	    try {
	      // some code to call yourservice with property driven or constant inputs
	    } catch (Exception e) {
	      logger.error(e.toString(), e);
	    }
  }
}

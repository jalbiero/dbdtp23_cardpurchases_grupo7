package com.tpdbd.cardpurchases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import com.tpdbd.cardpurchases.services.TestDataGeneratorService;

@SpringBootApplication
public class CardpurchasesApplication {
	@Autowired private TestDataGeneratorService tdgService;
	@Autowired private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(CardpurchasesApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		if (environment.getProperty("application.testData.addAtStartup", Boolean.class, false)) {
			tdgService.generateData();
		}
	}

}

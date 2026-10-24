package com.veterinaria.consentidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ConsentidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsentidosApplication.class, args);
	}

}

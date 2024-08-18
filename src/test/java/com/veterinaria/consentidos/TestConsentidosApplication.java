package com.veterinaria.consentidos;

import org.springframework.boot.SpringApplication;

public class TestConsentidosApplication {

	public static void main(String[] args) {
		SpringApplication.from(ConsentidosApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

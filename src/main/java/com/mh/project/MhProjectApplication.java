package com.mh.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class MhProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(MhProjectApplication.class, args);
	}

}

package com.gfttraining;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class UserMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserMicroserviceApplication.class, args);
	}

}

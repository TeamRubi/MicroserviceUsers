package com.gfttraining.connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RetrieveInfoFromExternalMicroservice {

	private final int MAX_RETRIES = 3;

	public String getExternalInformation(String path) {
		RestTemplate restTemplate = new RestTemplate();
		int retryCount = 0;
		while (true) {
			try {
				ResponseEntity<String> responseEntity = restTemplate.getForEntity(path, String.class);
				String response = responseEntity.getBody();
				log.info("Response retrieved from the microservice");
				return response;
			} catch (Exception e) {
				if (++retryCount == MAX_RETRIES) {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't connect with the microservice");
				}
				log.error("Couldn't connect with the microservice. Retrying in 10 second...");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}			}
		}
	}

}

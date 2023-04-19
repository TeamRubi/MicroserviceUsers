package com.gfttraining.connection;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RetrieveInformationFromExternalMicroservice {

	private final int MAX_RETRIES = 3;

	public <T> T getExternalInformation(String path, ParameterizedTypeReference<T> responseType) {
		RestTemplate restTemplate = new RestTemplate();
		int retryCount = 0;
		while (true) {
			try {
				ResponseEntity<T> responseEntity = restTemplate.exchange(path, HttpMethod.GET, null, responseType);
				T response = responseEntity.getBody();
				log.info("Response retrieved from " + path);
				return response;
			} catch (ResourceAccessException e) {

				if (++retryCount == MAX_RETRIES) {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't connect with the microservice");
				}
				log.error("Couldn't connect with the microservice. Retrying in 10 second...");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}

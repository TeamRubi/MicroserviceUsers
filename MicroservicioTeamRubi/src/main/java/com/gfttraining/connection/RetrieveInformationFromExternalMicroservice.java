package com.gfttraining.connection;

import javax.persistence.EntityNotFoundException;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RetrieveInformationFromExternalMicroservice {

	final int MAX_RETRIES = 3;
	
	private RestTemplate restTemplate;

	public RetrieveInformationFromExternalMicroservice(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Retryable
	public <T> T getExternalInformation(String path, ParameterizedTypeReference<T> responseType) throws InterruptedException {
		try {
			ResponseEntity<T> responseEntity = restTemplate.exchange(path, HttpMethod.GET, null, responseType);
			T response = responseEntity.getBody();
			log.info("Response retrieved from " + path);
			return response;
		} catch (RestClientException e) {
			log.error("Couldn't connect with the microservice.");
			throw new RestClientException("Couldn't connect with the microservice");
		}
	}
}

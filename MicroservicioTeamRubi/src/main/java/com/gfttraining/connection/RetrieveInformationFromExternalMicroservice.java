package com.gfttraining.connection;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RetrieveInformationFromExternalMicroservice {
	
	private static final int MAX_RETRIES = 0;

	public <T> T getExternalInformation(String path) {
	    RestTemplate restTemplate = new RestTemplate();
	    int retryCount = 0;
	    while (true) {
	        try {
	            ResponseEntity<T> responseEntity = restTemplate.exchange(path, HttpMethod.GET, null, new ParameterizedTypeReference<T>() {});
	            HttpStatus statusCode = responseEntity.getStatusCode();
	            if (statusCode.is4xxClientError()) {
	                throw new ResponseStatusException(statusCode, "Resource not found: " + path);
	            } else if (statusCode.is5xxServerError()) {
	                throw new ResponseStatusException(statusCode, "Internal server error: " + path);
	            }
	            T response = responseEntity.getBody();
	            log.info("Response retrieved from " + path);
	            return response;
	        } catch (Exception e) {
	            if (++retryCount == MAX_RETRIES) {
	                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't connect with the microservice");
	            }
	            log.error("Couldn't connect with the microservice. Retrying in 10 seconds...");
	            try {
	                Thread.sleep(10000);
	            } catch (InterruptedException e1) {
	                e1.printStackTrace();
	            }
	        }
	    }
	}

    
}

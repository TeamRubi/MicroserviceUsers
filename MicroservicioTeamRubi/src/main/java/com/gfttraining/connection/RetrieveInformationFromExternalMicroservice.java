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
    public <T> T getExternalInformation(String path, ParameterizedTypeReference<T> responseType) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(path, HttpMethod.GET, null, responseType);
            T response = responseEntity.getBody();
            log.info("Response retrieved from the microservice");
            return response;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't connect with the microservice");
        }
    }
}

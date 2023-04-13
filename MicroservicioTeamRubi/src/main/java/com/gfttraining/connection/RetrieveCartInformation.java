package com.gfttraining.connection;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.gfttraining.Entity.CartEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RetrieveCartInformation {

	public static List<CartEntity> getCarts(int Id) {

		String path = "http://localhost:8081/carts/user/" + Id;
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<List<CartEntity>> responseEntity = restTemplate.exchange(path, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<CartEntity>>() {
					});
			List<CartEntity> carts = responseEntity.getBody();
			log.info("Carts retrieved from the Cart microservice");
			return carts;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't connect with the Cart microservice");
		}
	}
}

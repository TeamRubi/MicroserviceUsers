package com.gfttraining.connection;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class RetrieveInformationFromExternalMicroserviceTest {
	
	@Mock
	RestTemplate restTemplate;
	
	@Autowired
	@InjectMocks
	private RetrieveInformationFromExternalMicroservice retrieveInformation;

	@Test
	public void testGetExternalInformation_Fail() {

		String path = "http://localhost:8082/notWorkingEndpoint";
		String expectedResponse = "No response";

		ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.NOT_FOUND);

		when(restTemplate.exchange(path, HttpMethod.GET, null, 
			new ParameterizedTypeReference<String>() {}))
			.thenReturn(responseEntity);

		ResponseEntity<String> actualResponse = retrieveInformation.getExternalInformation(path, 
			new ParameterizedTypeReference<ResponseEntity<String>>() {});

		Assertions.assertEquals(404, actualResponse.getStatusCode());
		
	}
	
	@Test
	public void testGetExternalInformation_Success() {

		String path = "http://localhost:8082/cart/users/12";
		String expectedResponse = "Response";
		
		ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

		when(restTemplate.exchange(path, HttpMethod.GET, null, 
			new ParameterizedTypeReference<String>() {}))
			.thenReturn(responseEntity);

		String actualResponse = retrieveInformation.getExternalInformation(path, 
			new ParameterizedTypeReference<String>() {});

		Assertions.assertEquals(expectedResponse, actualResponse);
		
	}

}


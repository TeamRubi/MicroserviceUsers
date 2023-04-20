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
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class RetrieveInformationFromExternalMicroserviceTest {
	
	@Mock
	RestTemplate restTemplate;
	
	@Autowired
	@InjectMocks
	private RetrieveInformationFromExternalMicroservice retrieveInformation;

	@Test
	public void testGetExternalInformation_Fail() {
	    String path = "http://localhost:8082/inventedMicroservice/users/12";
	    String expectedErrorMessage = "Failed to retrieve external information";

	    when(restTemplate.exchange(path, HttpMethod.GET, null, 
	        new ParameterizedTypeReference<String>() {}))
        .thenThrow(new ResourceAccessException(expectedErrorMessage));

	    Assertions.assertThrows(ResponseStatusException.class, () -> {
	        retrieveInformation.getExternalInformation(path, new ParameterizedTypeReference<String>() {});
	    });
	}

	@Test
	public void testGetExternalInformation_Success() throws InterruptedException {

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


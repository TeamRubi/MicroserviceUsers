package com.gfttraining.connection;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.gfttraining.exception.HttpRequestFailedException;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.Duration;

@ExtendWith(MockitoExtension.class)
class RetrieveInformationFromExternalMicroserviceTest {
	
	@Mock
	WebClient webClient;

	@InjectMocks
	private RetrieveInformationFromExternalMicroservice retrieveInformation;

	WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
	WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
	WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

	String path = "http://localhost:8082/carts/user/12";
	ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {};

	@Test
	void testGetExternalInformation_Fail() throws URISyntaxException {

		when(webClient.method(HttpMethod.GET)).thenReturn(requestBodyUriSpecMock);
		when(requestBodyUriSpecMock.uri(path)).thenReturn(requestBodySpecMock);
		when(requestBodySpecMock.retrieve()).thenReturn(responseSpecMock);
		when(responseSpecMock.bodyToMono(responseType)).thenReturn(Mono.error(
				new WebClientRequestException(new RuntimeException("error"), HttpMethod.GET, new URI("someuri"), HttpHeaders.EMPTY)));

		Mono<String> result = retrieveInformation.getExternalInformation(path, responseType);

		StepVerifier.create(result)
				.expectErrorMatches(throwable -> throwable instanceof HttpRequestFailedException &&
						throwable.getMessage().contains("Retries exhausted")).verify();
	}

	@Test
	void testGetExternalInformation_Success() throws InterruptedException {

		String expectedResponse = "Response";

		when(webClient.method(HttpMethod.GET)).thenReturn(requestBodyUriSpecMock);
		when(requestBodyUriSpecMock.uri(path)).thenReturn(requestBodySpecMock);
		when(requestBodySpecMock.retrieve()).thenReturn(responseSpecMock);
		when(responseSpecMock.bodyToMono(responseType)).thenReturn(Mono.just(expectedResponse));

		Mono<String> response = retrieveInformation.getExternalInformation(path, responseType);
		StepVerifier.create(response).expectNext(expectedResponse).verifyComplete();
	}
}


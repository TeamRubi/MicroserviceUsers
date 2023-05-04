package com.gfttraining.connection;
import com.gfttraining.exception.HttpRequestFailedException;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;

@Slf4j
@Service
@AllArgsConstructor
public class RetrieveInformationFromExternalMicroservice {

	private final WebClient webClient;

	public <T> Mono<T> getExternalInformation(String path, ParameterizedTypeReference<T> responseType) {

		return webClient
				.method(HttpMethod.GET)
				.uri(path)
				.retrieve()
				.bodyToMono(responseType)
				.retryWhen(Retry.backoff(3, Duration.ofMillis(100))
						.doBeforeRetry( retrySignal -> log.warn("Couldn't connect with the microservice. Attempt {}. Retrying in some seconds...", retrySignal.totalRetries()+1))
						.filter(WebClientRequestException.class::isInstance)
						.onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
								new HttpRequestFailedException("Retries exhausted after " + retrySignal.totalRetries() + " attempts")));
	}

}

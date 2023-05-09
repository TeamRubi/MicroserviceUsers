package com.gfttraining.it;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.gfttraining.controller.UserController;
import com.gfttraining.dto.UserEntityDTO;
import com.gfttraining.entity.FavoriteProduct;
import com.gfttraining.exception.DuplicateEmailException;
import com.gfttraining.exception.DuplicateFavoriteException;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Fault;
import lombok.AllArgsConstructor;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfttraining.config.AppConfig;
import com.gfttraining.config.FeatureFlag;
import com.gfttraining.connection.RetrieveInformationFromExternalMicroservice;
import com.gfttraining.entity.UserEntity;
import com.gfttraining.service.UserService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.web.reactive.function.client.WebClient;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceTest_IT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	UserService userService;

	UserEntity userModel;

	ObjectMapper objectMapper;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private FeatureFlag featureFlag;

	@Autowired
	private WebTestClient webTestClient;

	@RegisterExtension
	static WireMockExtension cartWireMock = WireMockExtension.newInstance()
			.options(WireMockConfiguration.options().port(8082)).build();

	@RegisterExtension
	static WireMockExtension productWireMock = WireMockExtension.newInstance()
			.options(WireMockConfiguration.options().port(8081)).build();

	@Autowired
	RetrieveInformationFromExternalMicroservice retrieveInfo;

	String userPath;
	String favoritePath;
	String userCartsPath;

	@BeforeEach
	public void createUser() {
		userModel = new UserEntity("pepe@pepsacse.com", "Pepito", "Perez", "calle falsa", "SPAIN");
		userPath = appConfig.getUserPath();
		favoritePath = appConfig.getFavoritePath();
		userCartsPath = appConfig.getUserCartsPath();

	}

	@BeforeEach
	public void setUpCarrito() {

		objectMapper = new ObjectMapper();

	}

	@Test
	void createUserBasic_IT() throws Exception {

		String json = objectMapper.writeValueAsString(userModel);

		mockMvc.perform(post(userPath).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated());
	}

	@Test
	void createUserWithoutRequiredFields_IT() throws Exception {

		JsonNode jsonNode = objectMapper.createObjectNode().put("name", "John").putNull("lastName")
				.put("email", "john@example.com").put("address", "123 Main St");

		String jsonString = objectMapper.writeValueAsString(jsonNode);

		mockMvc.perform(post(userPath).contentType(MediaType.APPLICATION_JSON).content(jsonString))
				.andExpect(status().isBadRequest());

	}

	@Test
	void updateUserById_IT() throws Exception {

		JsonNode jsonNode = objectMapper.createObjectNode().put("name", "John").put("country", "SPAIN");
		String jsonString = objectMapper.writeValueAsString(jsonNode);

		userModel.setId(1);
		userService.createUser(userModel);

		mockMvc.perform(patch(userPath + "/1").contentType(MediaType.APPLICATION_JSON).content(jsonString))
				.andExpect(status().isCreated());

	}

	@Test
	void createUserWithRepeatedEmail_IT() throws Exception {

		userService.createUser(userModel);

		String json = objectMapper.writeValueAsString(userModel);

		MvcResult result = mockMvc.perform(post(userPath).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isConflict()).andReturn();

	}

	@Test
	void updateUserByIdWithRepeatedEmail_IT() throws Exception {

		userService.updateUserById(1, userModel);
		String json = objectMapper.writeValueAsString(userModel);

		mockMvc.perform(patch(userPath + "/2").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isConflict());
	}

	@Test
	void getUserByEmailWithEmailNotFound_IT() throws Exception {

		String email = "newnotexistingemail@gmail.com";
		mockMvc.perform(get(userPath + "/email/" + email)).andExpect(status().isNotFound());

	}

	@Test
	void addFavoriteProduct_IT() throws Exception {

		int userId = 1;
		int productId = 23;
		productWireMock.stubFor(WireMock.get(urlPathEqualTo("/products/id/" + productId)).willReturn(aResponse().withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("{ \"id\": 23, \"name\": \"Product Name\", \"description\": \"Product Description\" }")
		));

		webTestClient.post().uri(favoritePath + "/" + userId + "/" + productId).accept(MediaType.APPLICATION_JSON).exchange()
				.expectStatus().isCreated()
				.expectBody(UserEntity.class)
				.consumeWith(response -> {  UserEntity user = response.getResponseBody();
					assertThat(user).isNotNull();
					assertThat(user.getId()).isEqualTo(userId);
					assertThat(user.getFavorites()).map(FavoriteProduct::getProductId).contains(productId);
				});

	}

	@Test
	void addFavoriteProductWithExistingFavorite_IT() throws Exception {

		int userId = 1;
		int productId = 26;

		productWireMock.stubFor(WireMock.get(urlPathEqualTo("/products/id/" + productId)).willReturn(aResponse().withStatus(200)
				.withHeader("Content-Type", "application/json").withBody("[]")));

		webTestClient.post().uri(favoritePath + "/" + userId + "/" + productId).accept(MediaType.APPLICATION_JSON).exchange()
				 		.expectStatus().is2xxSuccessful()
						.expectBody(String.class);

		webTestClient.post().uri(favoritePath + "/" + userId + "/" + productId).accept(MediaType.APPLICATION_JSON).exchange()
						.expectStatus().is4xxClientError()
						.expectBody(String.class);
	}

	@Test
	void deleteFavoriteProduct_IT() throws Exception {
		int userId = 1;
		int productId = 25;
		userService.addFavoriteProduct(userId, productId).then().as(StepVerifier::create).expectComplete().verify();

		mockMvc.perform(delete(favoritePath + "/" + userId + "/" + productId)).andExpect(status().isNoContent());
	}

	@Test
	void deleteFavoriteProductWithNotExistingFavorite_IT() throws Exception {

		int userId = 1;
		int productId = 29;

		mockMvc.perform(delete(favoritePath + "/" + userId + "/" + productId)).andExpect(status().isNotFound());
	}

	@Test
	void deleteFavoriteProductFromAllUsers_IT() throws Exception {

		int userId = 1;
		int productId = 25;
		userService.addFavoriteProduct(userId, productId);

		mockMvc.perform(delete(favoritePath + "/product/" + productId)).andExpect(status().isNoContent());
	}

	@Test
	void deleteFavoriteProductFromAllUsers_WithNotExistingFavorites_IT() throws Exception {

		int productId = 99;
		userService.deleteFavoriteProductFromAllUsers(99);

		mockMvc.perform(delete(favoritePath + "/product/" + productId)).andExpect(status().isNotFound());
	}

	@Test
	void getUserPointsAndAvgSpent_IT() throws Exception {

		int userId = 1;
		featureFlag.setEnableUserExtraInfo(true);

		cartWireMock.stubFor(WireMock.get(urlEqualTo("/carts/user/" + userId))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
						.withBody("[]")));

		webTestClient.get().uri(userPath + "/" + userId).accept(MediaType.APPLICATION_JSON).exchange()
				.expectStatus().isOk()
				.expectBody(UserEntityDTO.class)
				.consumeWith(response -> {  UserEntityDTO user = response.getResponseBody();
					assertThat(user).isNotNull();
					assertThat(user.getId()).isEqualTo(userId);
					assertThat(user.getPoints()).isZero();
					assertThat(user.getAverageSpent()).isZero();
				});
	}


	@Test
	void getUserPointsAndAvgNotFound_IT() throws IOException, InterruptedException {

		int userId = 10000;

		featureFlag.setEnableUserExtraInfo(true);

		cartWireMock.stubFor(WireMock.get(urlPathEqualTo("/carts/user/" + userId)).willReturn(aResponse().withStatus(200)
				.withHeader("Content-Type", "application/json").withBody("[]")));

		webTestClient.get().uri(userPath + "/" + userId).accept(MediaType.APPLICATION_JSON).exchange()
				.expectStatus().isNotFound()
				.expectBody(String.class)
				.consumeWith(response-> {
					assertThat(response.getResponseBody()).contains("User with id: "+ userId + " not found");
				} );

	}

	@Test
	void shouldRetryThreeTimesAndSucceedOnThirdAttempt() {


		productWireMock.stubFor(WireMock.get(urlEqualTo("/external-service"))
				.inScenario("Connection retries")
				.whenScenarioStateIs(Scenario.STARTED)
				.willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER))
				.willSetStateTo("Connection failed 1"));

		productWireMock.stubFor(WireMock.get(urlEqualTo("/external-service"))
				.inScenario("Connection retries")
				.whenScenarioStateIs("Connection failed 1")
				.willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER))
				.willSetStateTo("Connection failed 2"));

		productWireMock.stubFor(WireMock.get(urlEqualTo("/external-service"))
				.inScenario("Connection retries")
				.whenScenarioStateIs("Connection failed 2")
				.willReturn(aResponse().withStatus(200).withBody("{\"result\":\"success\"}")));


		Mono<String> resultMono = retrieveInfo.getExternalInformation("http://localhost:" + 8081 + "/external-service",
				new ParameterizedTypeReference<String>() {});

		StepVerifier.create(resultMono).expectNext("{\"result\":\"success\"}").verifyComplete();

	}

	@Test
	void endToEndTest_e2e() throws Exception {

		String json = objectMapper.writeValueAsString(userModel);

		cartWireMock.stubFor(WireMock.get(urlEqualTo("/carts/user/1001"))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
						.withBody("[]")));

		webTestClient.get().uri("/users/1001").exchange().expectStatus().isNotFound();

		webTestClient.post().uri("/users").contentType(MediaType.APPLICATION_JSON).bodyValue(json).exchange()
				.expectStatus().isCreated();

		webTestClient.delete().uri("/users/1001").exchange().expectStatus().isNoContent();

		webTestClient.get().uri("/users/1001").exchange().expectStatus().isNotFound();

	}

}

package com.gfttraining.it;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
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



@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceTest_IT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	UserService userService;

	UserEntity userModel;

	WireMockServer wireMockServer;

	ObjectMapper objectMapper;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private FeatureFlag featureFlag;

	@RegisterExtension
	static WireMockExtension cartWireMock = WireMockExtension.newInstance().options(wireMockConfig().port(8082)).build();
	@RegisterExtension
	static WireMockExtension productWireMock = WireMockExtension.newInstance().options(wireMockConfig().port(8081)).build();

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

	@Mock
	WebClient webClient;

	@BeforeEach
	public void setUpCarrito() {

		objectMapper = new ObjectMapper();
		featureFlag.setEnableUserExtraInfo(true);

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
	public void updateUserById_IT() throws Exception {

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

		mockMvc.perform(post(userPath).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isConflict());

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

		ResponseEntity<String> responseEntity = new ResponseEntity<>("test", HttpStatus.OK);

		when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);

		productWireMock.stubFor(WireMock.get(urlPathEqualTo("/products/id/" + 23)).willReturn(aResponse().withStatus(201)));

		mockMvc.perform(post(favoritePath + "/" + userId + "/" + productId)).andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").value(userId))
				.andExpect(jsonPath("$.favorites[*].productId", hasItem(productId)));

	}

	@Test
	void addFavoriteProductWithExistingFavorite_IT() throws Exception {

		int userId = 1;
		int productId = 25;

		ResponseEntity<String> responseEntity = new ResponseEntity<>("test", HttpStatus.OK);

		when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);

		userService.addFavoriteProduct(userId, productId);

		mockMvc.perform(post(favoritePath + "/" + userId + "/" + productId)).andExpect(status().isConflict())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

	}

	@Test
	void deleteFavoriteProduct_IT() throws Exception {

		int userId = 1;
		int productId = 25;

		userService.addFavoriteProduct(userId, productId);

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
	public void getUserPointsAndAvgSpent_IT() throws Exception {

		cartWireMock.stubFor(WireMock.get(urlPathEqualTo("/carts/user/" + 12)).willReturn(aResponse().withStatus(200)));

		HttpClient httpClient = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8082/carts/user/" + 12)).GET().build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		assertThat(200).isEqualTo(response.statusCode());

	}

	@Test
	public void getUserPointsAndAvgNotFound_IT() throws IOException, InterruptedException {

		cartWireMock.stubFor(WireMock.get(urlPathEqualTo("/carts/user/" + 12)).willReturn(aResponse().withStatus(404)
				.withHeader("Content-Type", "application/json").withBody("\"User not found")));

		HttpClient httpClient = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8082/carts/user/" + 12)).GET().build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		assertThat(404).isEqualTo(response.statusCode());
		assertThat("\"User not found").isEqualTo(response.body());
	}

	@Test
	public void shouldRetryThreeTimesAndSucceedOnThirdAttempt() {

		RetrieveInformationFromExternalMicroservice retrieveInformationFromExternalMicroservice = new RetrieveInformationFromExternalMicroservice(
				webClient);

		stubFor(WireMock.get(urlEqualTo("/external-service")).inScenario("Connection retries")
				.whenScenarioStateIs(Scenario.STARTED).willReturn(aResponse().withStatus(500))
				.willSetStateTo("Connection failed 1"));

		stubFor(WireMock.get(urlEqualTo("/external-service")).inScenario("Connection retries")
				.whenScenarioStateIs("Connection failed 1").willReturn(aResponse().withStatus(500))
				.willSetStateTo("Connection failed 2"));

		stubFor(WireMock.get(urlEqualTo("/external-service")).inScenario("Connection retries")
				.whenScenarioStateIs("Connection failed 2")
				.willReturn(aResponse().withStatus(200).withBody("{\"result\":\"success\"}")));

		String result = "";
		try {
			result = retrieveInformationFromExternalMicroservice.getExternalInformation(
					"http://localhost:" + wireMockServer.port() + "/external-service",
					new ParameterizedTypeReference<String>() {
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertThat(result).isEqualTo("{\"result\":\"success\"}");
	}

	@Test
	void endToEndTest_e2e() throws Exception {

		String json = objectMapper.writeValueAsString(userModel);

		cartWireMock.stubFor(WireMock.get(urlEqualTo("/carts/user/1001"))
				.willReturn(aResponse().withStatus(200)));

		mockMvc.perform(get("/users/1001")).andExpect(status().isNotFound());

		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated());

		mockMvc.perform(delete("/users/1001")).andExpect(status().isNoContent());

		mockMvc.perform(get("/users/1001")).andExpect(status().isNotFound());

	}

}

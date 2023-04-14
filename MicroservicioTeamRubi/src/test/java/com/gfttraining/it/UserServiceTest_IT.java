package com.gfttraining.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfttraining.entity.UserEntity;
import com.gfttraining.controller.UserController;
import com.gfttraining.service.UserService;



import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.net.URI;



import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class UserServiceTest_IT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	UserService userService;

	UserEntity userModel;
	
	
	WireMockServer wireMockServer;
	

	@BeforeEach
	public void createUser() {
		userModel = new UserEntity("pepe@pepe.com", "Pepito", "Perez", "calle falsa", "SPAIN");
	}

	@Mock
	RestTemplate restTemplate;
	
	
	@BeforeEach
	public void setUpCarrito() {
		wireMockServer = new WireMockServer();
		wireMockServer.start();
	
    }
	
	@AfterClass
	public void tearDownCarrito() {
		wireMockServer.stop();
	}
	
	
	

	@Test 
	void createUserBasic_IT() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(userModel);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isCreated());
	}


	@Test
	void createUserWithoutRequiredFields_IT() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonNode = objectMapper.createObjectNode()
				.put("name", "John")
				.putNull("lastName")
				.put("email", "john@example.com")
				.put("address", "123 Main St");

		String jsonString = objectMapper.writeValueAsString(jsonNode);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString)).andExpect(status().isBadRequest());

	}

	@Test
	public void updateUserById_IT() throws Exception {

		mockMvc.perform(patch("/users/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"Pablo\", \"lastname\": \"Garcia\" }"))
		.andExpect(status().isCreated());

	}

	@Test 
	void createUserWithRepeatedEmail_IT() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		UserEntity existingUser = new UserEntity("pabloperez@gmail.com","Pablo", "Perez", "Avinguda Diagonal 5","SPAIN");

		userService.createUser(existingUser);

		UserEntity user = new UserEntity("pabloperez@gmail.com","Pablo", "Perez", "Avinguda Diagonal 5","SPAIN");
		String json = objectMapper.writeValueAsString(user);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isConflict());

	}

	@Test 
	void updateUserByIdWithRepeatedEmail_IT() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		UserEntity existingUser = new UserEntity("pablo@gmail.com","Pablo", "Perez", "Avinguda Diagonal 5","SPAIN");

		userService.updateUserById(1, existingUser);

		UserEntity user = new UserEntity("pablo@gmail.com","Pablo", "Perez", "Avinguda Diagonal 5","SPAIN");
		String json = objectMapper.writeValueAsString(user);

		mockMvc.perform(patch("/users/2")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isConflict());

	}

	@Test
	void getUserByEmailWithEmailNotFound_IT() throws Exception {

		String email = "newnotexistingemail@gmail.com";
		mockMvc.perform(get("/users/email/" + email))
		.andExpect(status().isNotFound());

	}

	@Test
	void addFavoriteProduct_IT() throws Exception {

		int userId = 1;
		int productId = 23;

		ResponseEntity<String> responseEntity = new ResponseEntity<String>("test", HttpStatus.OK);

		when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);

		mockMvc.perform(post("/favorite/" + userId + "/" + productId))
		.andExpect(status().isCreated())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.id").value(userId))
		.andExpect(jsonPath("$.favorites[*].productId", hasItem(productId)));

	}

	@Test
	void addFavoriteProductWithExistingFavorite_IT() throws Exception {

		int userId = 1;
		int productId = 25;

		ResponseEntity<String> responseEntity = new ResponseEntity<String>("test", HttpStatus.OK);

		when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);

		userService.addFavoriteProduct(userId, productId);

		mockMvc.perform(post("/favorite/" + userId + "/" + productId))
		.andExpect(status().isConflict())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON));

	}

	@Test
	void deleteFavoriteProduct_IT() throws Exception {

		int userId = 1;
		int productId = 25;

		userService.addFavoriteProduct(userId, productId);

		mockMvc.perform(delete("/favorite/" + userId + "/" + productId))
		.andExpect(status().isNoContent());
	}

	@Test
	void deleteFavoriteProductWithNotExistingFavorite_IT() throws Exception {

		int userId = 1;
		int productId = 29;

		mockMvc.perform(delete("/favorite/" + userId + "/" + productId))
		.andExpect(status().isNotFound());
	}

	@Test
	void deleteFavoriteProductFromAllUsers_IT() throws Exception {

		int userId = 1;
		int productId = 25;

		userService.addFavoriteProduct(userId, productId);

		mockMvc.perform(delete("/favorite/product/" + productId))
		.andExpect(status().isNoContent());
	}

	@Test
	void deleteFavoriteProductFromAllUsers_WithNotExistingFavorites_IT() throws Exception {

		int productId = 99;

		userService.deleteFavoriteProductFromAllUsers(99);

		mockMvc.perform(delete("/favorite/product/" + productId))
		.andExpect(status().isNotFound());
	}
	
	
	
	 @Test
	 public void getUserPointsAndAvg_IT () throws IOException, InterruptedException {
			
			stubFor(get(urlPathEqualTo("/carts/user/" + 12))
	                .willReturn(aResponse()
	                    .withStatus(200)
	                    .withHeader("Content-Type", "application/json")
	                    .withBody("{\"points\":100, \"averageSpent\":1260.0}")));
			
			HttpClient httpClient = HttpClient.newHttpClient();
			
			HttpRequest request = HttpRequest.newBuilder()
			        .uri(URI.create("http://localhost:" + wireMockServer.port() + "/carts/user/" + 12))
			        .GET()
			        .build();
			 HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			 assertThat(200).isEqualTo(response.statusCode());
			 assertThat("{\"points\":100, \"averageSpent\":1260.0}").isEqualTo(response.body());
	 }
	
	 @Test
	 public void getUserPointsAndAvgNotFound_IT () throws IOException, InterruptedException {
			
			stubFor(get(urlPathEqualTo("/carts/user/" + 12))
	                .willReturn(aResponse()
	                    .withStatus(404)
	                    .withHeader("Content-Type", "application/json")
	                    .withBody("\"User not found")));
			
			HttpClient httpClient = HttpClient.newHttpClient();
			
			HttpRequest request = HttpRequest.newBuilder()
			        .uri(URI.create("http://localhost:" + wireMockServer.port() + "/carts/user/" + 12))
			        .GET()
			        .build();
			 HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			 assertThat(404).isEqualTo(response.statusCode());
			 assertThat("\"User not found").isEqualTo(response.body());
	 }


}

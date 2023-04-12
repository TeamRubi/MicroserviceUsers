package com.gfttraining.it;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfttraining.Entity.UserEntity;
import com.gfttraining.controller.UserController;
import com.gfttraining.entity.User;
import com.gfttraining.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceTest_IT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	UserService userService;
	
	UserEntity userModel;
	
	@BeforeEach
	public void createUser() {
		userModel = new UserEntity("pepe@pepe.com", "Pepito", "Perez", "calle falsa", "SPAIN", "TRANSFERENCIA");
	}

	@Mock
	RestTemplate restTemplate;


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
		UserEntity existingUser = new UserEntity("pabloperez@gmail.com","Pablo", "Perez", "Avinguda Diagonal 5","SPAIN", "VISA");

		userService.createUser(existingUser);

		UserEntity user = new UserEntity("pabloperez@gmail.com","Pablo", "Perez", "Avinguda Diagonal 5","SPAIN", "VISA");
		String json = objectMapper.writeValueAsString(user);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isConflict());

	}

	//TODO test is not working due to the new table FavoriteProduct
	@Test 
	void updateUserByIdWithRepeatedEmail_IT() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		UserEntity existingUser = new UserEntity("pablo@gmail.com","Pablo", "Perez", "Avinguda Diagonal 5","SPAIN", "VISA");

		userService.updateUserById(1, existingUser);

		UserEntity user = new UserEntity("pablo@gmail.com","Pablo", "Perez", "Avinguda Diagonal 5","SPAIN", "VISA");
		String json = objectMapper.writeValueAsString(user);

		mockMvc.perform(put("/users/2")
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

		mockMvc.perform(post("/users/" + userId + "/" + productId))
		.andExpect(status().isCreated())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.id").value(userId))
		.andExpect(jsonPath("$.favorites[*].productId", hasItem(productId)));

	}

	@Test
	void addFavoriteProductWithExistingFavorite() throws Exception {

		int userId = 1;
		int productId = 25;

		ResponseEntity<String> responseEntity = new ResponseEntity<String>("test", HttpStatus.OK);

		when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);

		userService.addFavoriteProduct(userId, productId);

		mockMvc.perform(post("/users/" + userId + "/" + productId))
		.andExpect(status().isConflict())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON));

	}


}

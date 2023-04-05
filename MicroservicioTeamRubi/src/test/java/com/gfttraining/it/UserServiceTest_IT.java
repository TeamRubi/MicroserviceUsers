package com.gfttraining.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfttraining.service.UserService;
import com.gfttraining.userEntity.UserEntity;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceTest_IT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	UserService userService;


	@Test 
	void createUserBasic_IT() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		UserEntity user = new UserEntity("pepe@pepe.com", "Pepito", "Perez", "calle falsa", "SPAIN", "TRANSFERENCIA");
		String json = objectMapper.writeValueAsString(user);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isCreated());
	}


	@Test
	void createUserWithoutRequiredFields_IT() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		UserEntity user = new UserEntity("pepe@pepe.com", null, "Perez", "calle falsa", "SPAIN", "TRANSFERENCIA");
		String json = objectMapper.writeValueAsString(user);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isBadRequest());

	}

	@Test
	public void updateUserById_IT() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"Pablo\", \"lastName\": \"Garcia\" }"))
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
}

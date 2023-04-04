package com.gfttraining.it;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfttraining.controller.UserController;
import com.gfttraining.service.UserService;
import com.gfttraining.user.User;

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
		User user = new User("example@gmail.com", "Pablo", "Perez", "Avinguda Diagonal 5", "VISA");
		String json = objectMapper.writeValueAsString(user);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isCreated());
	}


	@Test
	void createUserWithoutRequiredFields_IT() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		User user = new User("Pablo", null, "Avinguda Diagonal 5", "VISA");
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
		User existingUser = new User("pabloperez@gmail.com","Pablo", "Perez", "Avinguda Diagonal 5", "VISA");

		userService.createUser(existingUser);

		User user = new User("pabloperez@gmail.com","Pablo", "Perez", "Avinguda Diagonal 5", "VISA");
		String json = objectMapper.writeValueAsString(user);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isConflict());

	}

	@Test 
	void updateUserByIdWithRepeatedEmail_IT() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		User existingUser = new User("pablo@gmail.com","Pablo", "Perez", "Avinguda Diagonal 5", "VISA");

		userService.updateUserById(1, existingUser);

		User user = new User("pablo@gmail.com","Pablo", "Perez", "Avinguda Diagonal 5", "VISA");
		String json = objectMapper.writeValueAsString(user);

		mockMvc.perform(put("/users/2")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isConflict());

	}
}

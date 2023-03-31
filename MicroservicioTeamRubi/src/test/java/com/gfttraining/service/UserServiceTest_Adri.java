package com.gfttraining.service;

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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
import com.gfttraining.user.User;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
class UserServiceTest_Adri {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	UserService userServiceMock;

	@Test
	void getAllUsers_Test() throws Exception {

		List<User> users = Arrays.asList(new User("Pedro", "Chapo", "Monzon", "VISA"));
		when(userServiceMock.findAll()).thenReturn(users);

		mockMvc.perform(MockMvcRequestBuilders.get("/users"))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is("Pedro")));

	}

	@Test 
	void createUserBasic_ITtest() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		User user = new User("Pablo", "Perez", "Avinguda Diagonal 5", "VISA");
		String json = objectMapper.writeValueAsString(user);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isCreated());
	}


	@Test
	void createUserWithoutRequiredFields_ITtest() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		User user = new User("Pablo", null, "Avinguda Diagonal 5", "VISA");
		String json = objectMapper.writeValueAsString(user);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isBadRequest());

	}

	@Test
	public void updateUserById_IT() throws Exception {

		User user = new User("Pedro", "Garcia", null, null);
		user.setId(1);

		when(userServiceMock.updateUserById(anyInt(), any(User.class))).thenReturn(user);

		mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"Pablo\", \"lastName\": \"Perez\" }"))
		.andExpect(status().isCreated());


	}




}

package com.gfttraining.service;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
=======
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
>>>>>>> Stashed changes
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
>>>>>>> Stashed changes

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
<<<<<<< Updated upstream
=======
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
<<<<<<< Updated upstream
import org.springframework.mock.web.MockHttpServletResponse;
=======
>>>>>>> Stashed changes
>>>>>>> Stashed changes
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

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

<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
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
=======
	@Test
	public void updateUserById_IT() throws Exception {

		User user = new User("Pedro", "Garcia", null, null);
		user.setId(1);

		when(userServiceMock.updateUserById(anyInt(), any(User.class))).thenReturn(user);

		mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"Pablo\", \"lastName\": \"Perez\" }"))
		.andExpect(status().isCreated());
>>>>>>> Stashed changes

	}



<<<<<<< Updated upstream
=======


>>>>>>> Stashed changes
>>>>>>> Stashed changes
}

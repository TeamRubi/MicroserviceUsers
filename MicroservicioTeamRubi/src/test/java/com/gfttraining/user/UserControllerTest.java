package com.gfttraining.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gfttraining.controller.UserController;
import com.gfttraining.service.UserService;

@WebMvcTest(UserController.class) 
class UserControllerTest {

	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UserService userService;
	
	
	@Test
	void deleteUserById_test() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}",4))
			   .andExpect(MockMvcResultMatchers.status().isNoContent());
		
		verify(userService).deleteUserById(4);
	}

}

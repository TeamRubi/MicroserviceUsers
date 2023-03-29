package com.gfttraining.service;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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

}

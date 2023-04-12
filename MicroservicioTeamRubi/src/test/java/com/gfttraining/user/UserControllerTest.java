package com.gfttraining.user;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gfttraining.Entity.UserEntity;
import com.gfttraining.controller.UserController;
import com.gfttraining.entity.User;
import com.gfttraining.service.UserService;

@WebMvcTest(UserController.class) 
class UserControllerTest {


	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;



	@Test
	void getUserById_test() throws Exception {
		UserEntity userTest1= new UserEntity("pepe@pepe.com", "Pepito", "Perez", "calle falsa", "SPAIN", "TRANSFERENCIA");

		Mockito
		.when(userService.findUserById(1))
		.thenReturn(userTest1);

		mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}",1))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("@.name", is("Erna")));

	}

	@Test
	void getUserByName_test() throws Exception {

		List <UserEntity> userListTest1 = new ArrayList<>();
		UserEntity userTest1 = new UserEntity();
		userTest1.setId(1);
		userTest1.setName("Erna");
		userListTest1.add(userTest1);
		
		Mockito
		.when(userService.findAllByName("Erna"))
		.thenReturn(userListTest1);

		mockMvc.perform(MockMvcRequestBuilders.get("/users/name/{name}","Erna"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("@[0].name", is("Erna")));

	}


	@Test
	void deleteUserById_test() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}",4))
		.andExpect(MockMvcResultMatchers.status().isNoContent());

		verify(userService).deleteUserById(4);
	}

}

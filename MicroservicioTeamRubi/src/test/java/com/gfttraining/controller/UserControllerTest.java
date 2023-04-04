package com.gfttraining.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import static org.mockito.ArgumentMatchers.anyList;
import org.springframework.http.ResponseEntity;

import com.gfttraining.service.UserService;
import com.gfttraining.user.User;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Mock
	UserService userService;

	@InjectMocks
	UserController userController;

	@Test
	void getAllUsers_test() throws Exception {

		List<User> users = Arrays.asList(new User("Pedro", "Chapo", "Monzon", "VISA"));
		when(userService.findAll()).thenReturn(users);

		List<User> existingUsers = userController.getAllUsers();
		assertThat(existingUsers).containsAll(users);

	}

	@Test
	void createUser_test() {
		User user = new User("Pepito", "Perez", "calle falsa", "TRANSFERENCIA");
		when(userService.createUser(user)).thenReturn(user);
		ResponseEntity<User> response = userController.createUser(user);

		assertThat(user).isEqualTo(response.getBody());
		assertThat(HttpStatus.CREATED).isEqualTo(response.getStatusCode());
	}


	@Test
	void updateUserById_test() {

		User user = new User("Pepito", "Perez", "calle falsa", "TRANSFERENCIA");

		when(userService.updateUserById(1, user)).thenReturn(user);

		ResponseEntity<User> response = userController.updateUserById(1, user);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(user);

	}

	@Test
	void getUserByEmail_test() {

		User user = new User("pedro@chapo.com", "Pedro", "Chapo", "Monzon", "VISA");
		String email = "pedro@chapo.com"; 

		when(userService.findUserByEmail(email)).thenReturn(user);

		ResponseEntity<User> response = userController.getUserByEmail(email);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(user);

	}

}

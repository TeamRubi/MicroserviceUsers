package com.gfttraining.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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
	void createUser_test() {
		User user = new User("Pepito", "Perez", "calle falsa", "TRANSFERENCIA");
		when(userService.createUser(user)).thenReturn(user);
		ResponseEntity<User> response = userController.createUser(user);

		assertThat(user).isEqualTo(response.getBody());
		assertThat(HttpStatus.CREATED).isEqualTo(response.getStatusCode());
	}

}

package com.gfttraining.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import static org.mockito.ArgumentMatchers.anyList;

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
		User createduser = userController.createUser(user);
		assertThat(user).isEqualTo(createduser);
	}
	
	@Test
	void createUsersByFile_Test() {

        byte[] content = "[{\"id\":1,\"name\":\"John\",\"lastname\":\"Connor\",\"address\":\"123 Some address\",\"paymentmethod\":\"VISA\"}]".getBytes();
        MultipartFile file = new MockMultipartFile("file", "users.json", MediaType.APPLICATION_JSON_VALUE, content);
        doNothing().when(userService).deleteAllUsers();

        ResponseEntity<Void> response = userController.saveAllImportedUsers(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).deleteAllUsers();
        verify(userService).saveAllUsers(anyList());
        
	}

}

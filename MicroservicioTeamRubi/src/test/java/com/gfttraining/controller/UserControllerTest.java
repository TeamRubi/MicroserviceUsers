package com.gfttraining.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.gfttraining.Entity.UserEntity;
import com.gfttraining.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Mock
	UserService userService;

	@InjectMocks
	UserController userController;

	@Test
	void getAllUsers_test() throws Exception {

		List<UserEntity> users = Arrays.asList(new UserEntity("pepe@pepe.com", "Pepito", "Perez", "calle falsa", "SPAIN", "TRANSFERENCIA"));
		when(userService.findAll()).thenReturn(users);

		List<UserEntity> existingUsers = userController.getAllUsers();
		assertThat(existingUsers).containsAll(users);

	}
	
	@Test
	void deleteAllUsers_test() throws Exception {

	    Mockito.doNothing().when(userService).deleteAllUsers();
	    userController.deleteAllUsers();
	    Mockito.verify(userService, Mockito.times(1)).deleteAllUsers();

	}
	
	@Test
	void importUsersByFile() throws Exception{
		
		MultipartFile file = Mockito.mock(MultipartFile.class);

	    Mockito.doNothing().when(userService).deleteAllUsers();
	    Mockito.doNothing().when(userService).saveAllUsers(Mockito.anyList());

	    byte[] content = "[{\"email\": \"user@gmail.com\", \"name\": \"pedro\", \"lastname\": \"soler\", \"address\": \"monzon\", \"paymentmethod\": \"VISA\"}, {\"email\": \"user@gmail.com\", \"name\": \"pedro\", \"lastname\": \"soler\", \"address\": \"monzon\", \"paymentmethod\": \"VISA\"}]".getBytes();
	    Mockito.when(file.getBytes()).thenReturn(content);

	    ResponseEntity<Void> response = userController.saveAllImportedUsers(file);

	    Mockito.verify(userService, Mockito.times(1)).deleteAllUsers();
	    Mockito.verify(userService, Mockito.times(1)).saveAllUsers(Mockito.anyList());
	    assertEquals(HttpStatus.CREATED, response.getStatusCode());

	}
	
	@Test
	public void testSaveAllImportedUsersWithError() throws IOException {
		
        MultipartFile file = new MockMultipartFile("file", new byte[0]);
        
        doThrow(new RuntimeException("Error al eliminar los usuarios")).when(userService).deleteAllUsers();
        
        ResponseEntity<Void> response = userController.saveAllImportedUsers(file);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        verify(userService, times(1)).deleteAllUsers();
        verifyNoMoreInteractions(userService);
	}

	@Test
	void createUser_test() {
		UserEntity user = new UserEntity("pepe@pepe.com", "Pepito", "Perez", "calle falsa", "SPAIN", "TRANSFERENCIA");
		when(userService.createUser(user)).thenReturn(user);
		ResponseEntity<UserEntity> response = userController.createUser(user);

		assertThat(user).isEqualTo(response.getBody());
		assertThat(HttpStatus.CREATED).isEqualTo(response.getStatusCode());
	}


	@Test
	void updateUserById_test() {

		UserEntity user = new UserEntity("pepe@pepe.com", "Pepito", "Perez", "calle falsa", "SPAIN", "TRANSFERENCIA");

		when(userService.updateUserById(1, user)).thenReturn(user);

		ResponseEntity<UserEntity> response = userController.updateUserById(1, user);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(user);

	}

	@Test
	void getUserByEmail_test() {

		UserEntity user = new UserEntity("pedro@chapo.com", "Pepito", "Perez", "calle falsa", "SPAIN", "TRANSFERENCIA");
		String email = "pedro@chapo.com"; 

		when(userService.findUserByEmail(email)).thenReturn(user);

		ResponseEntity<UserEntity> response = userController.getUserByEmail(email);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(user);

	}

}

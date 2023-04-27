package com.gfttraining.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.gfttraining.config.FeatureFlag;
import com.gfttraining.connection.RetrieveInformationFromExternalMicroservice;
import com.gfttraining.dto.UserEntityDTO;
import com.gfttraining.entity.FavoriteProduct;
import com.gfttraining.entity.UserEntity;
import com.gfttraining.service.UserService;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Mock
	private UserService userService;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private RetrieveInformationFromExternalMicroservice retrieveInfo;

	@Mock
	private FeatureFlag featureFlag;

	@InjectMocks
	UserController userController;

	UserEntity userModel;
	UserEntityDTO userModelDTO;

	@BeforeEach
	public void createUser() {
		userModel = new UserEntity("pepe@pepe.com", "Pepito", "Perez", "calle falsa", "SPAIN");
		userModelDTO = new UserEntityDTO(12, "pepe@pepe.com", "Pedro", "Chapo", "calle falsa", "SPAIN", "TRANSFER", BigDecimal.valueOf(0), 0, null);
	}
	
	
	@DisplayName("Return all of Users in database")
	@Test
	void getAllUsers_test() throws Exception {

		List<UserEntity> users = Arrays.asList(userModel);
		when(userService.findAll()).thenReturn(users);

		List<UserEntity> existingUsers = userController.getAllUsers();
		assertThat(existingUsers).containsAll(users);

	}
	
	@DisplayName("Delete all of Users in database")
	@Test
	void deleteAllUsers_test() throws Exception {

		Mockito.doNothing().when(userService).deleteAllUsers();
		userController.deleteAllUsers();
		Mockito.verify(userService, Mockito.times(1)).deleteAllUsers();

	}
	
	@DisplayName("Given a file,then import all of Users in file to the database")
	@Test
	void importUsersByFile() throws Exception{

		MultipartFile file = Mockito.mock(MultipartFile.class);

		//Mockito.doNothing().when(userService).deleteAllUsers();
		//Mockito.doNothing().when(userService).saveAllUsers(Mockito.anyList());

		when(userService.saveAllImportedUsers(file)).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

		//byte[] content = "[{\"email\": \"user@gmail.com\", \"name\": \"pedro\", \"lastname\": \"soler\", \"address\": \"monzon\", \"paymentmethod\": \"VISA\"}, {\"email\": \"user@gmail.com\", \"name\": \"pedro\", \"lastname\": \"soler\", \"address\": \"monzon\", \"paymentmethod\": \"VISA\"}]".getBytes();
		//Mockito.when(file.getBytes()).thenReturn(content);

		ResponseEntity<Void> response = userController.saveAllImportedUsers(file);

		Mockito.verify(userService, Mockito.times(1)).saveAllImportedUsers(file);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());

	}
	
	@DisplayName("Given a file,then import all of Users in file to the database but in the save method throw a exception")
	public void testSaveAllImportedUsersWithError() throws IOException {

		MultipartFile file = new MockMultipartFile("file", new byte[0]);

		doThrow(new RuntimeException("Error al eliminar los usuarios")).when(userService).deleteAllUsers();

		ResponseEntity<Void> response = userController.saveAllImportedUsers(file);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

		verify(userService, times(1)).deleteAllUsers();
		verifyNoMoreInteractions(userService);
	}

	@DisplayName("Given a fields,then a user is created and saved into the database")
	@Test
	void createUser_test() {

		when(userService.createUser(userModel)).thenReturn(userModel);
		ResponseEntity<UserEntity> response = userController.createUser(userModel);

		assertThat(userModel).isEqualTo(response.getBody());
		assertThat(HttpStatus.CREATED).isEqualTo(response.getStatusCode());
	}

	@DisplayName("Given a fields,then a user is updated and saved into the database")
	@Test
	void updateUserById_test() {

		when(userService.updateUserById(1, userModel)).thenReturn(userModel);

		ResponseEntity<UserEntity> response = userController.updateUserById(1, userModel);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(userModel);

	}
	
	@DisplayName("Given a email,then find a user in database, that matches with the email")
	@Test
	void getUserByEmail_test() {

		String email = "pedro@chapo.com"; 

		when(userService.findUserByEmail(email)).thenReturn(userModel);

		ResponseEntity<UserEntity> response = userController.getUserByEmail(email);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(userModel);

	}
	
	
	@DisplayName("Given a product,then the product is added into a user to show their favorite product")
	@Test
	void addFavoriteProduct_test() throws Exception {

		//mocking service
		int productId = 2;
		userModel.addFavorite(new FavoriteProduct(1,1,productId));

		when(userService.addFavoriteProduct(anyInt(), anyInt())).thenReturn(userModel);

		//mocking http request
		when(retrieveInfo.getExternalInformation(anyString(), any())).thenReturn("example");

		ResponseEntity<UserEntity> response = userController.addFavoriteProduct(1, productId);

		verify(userService, atLeastOnce()).addFavoriteProduct(1, productId);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(userModel);

	}
	
	@DisplayName("Given a not existing product,then the product is added into a user but throw an exception")
	@Test
	void addFavoriteProductWithNotExistingProduct_test() throws Exception {

		int productId = 200;

		doThrow(HttpClientErrorException.NotFound.class).when(retrieveInfo).getExternalInformation(anyString(), any());

		assertThatThrownBy(()-> userController.addFavoriteProduct(1, productId))
		.isInstanceOf(ResponseStatusException.class)
		.hasMessageContaining("product with id " + productId + " not found");

	}
	
	
	@DisplayName("Given a product,then the favorite producte from user is deleted")
	@Test
	void deleteFavoriteProduct_test() throws Exception {

		int productId = 2;

		ResponseEntity<Void> response = userController.deleteFavoriteProduct(1, productId);

		verify(userService, atLeastOnce()).deleteFavoriteProduct(1, productId);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

	}
	

	@DisplayName("Given a list of Users,then the favorite product is deleted from all Users in database")
	@Test
	void deleteFavoriteProductFromAllUsers_test() throws Exception {

		int productId = 2;

		ResponseEntity<Void> response = userController.deleteFavoriteProductFromAllUsers(productId);

		verify(userService, atLeastOnce()).deleteFavoriteProductFromAllUsers(productId);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

	}

	
	
	@DisplayName("Given an id,then find a user in database, that matches with the id")
	@Test
	void getUserById () throws InterruptedException {

		userModel.setId(1);

		when(featureFlag.isEnableUserExtraInfo()).thenReturn(false);
		when(userService.findUserById(1)).thenReturn(userModel);

		UserEntity user = (UserEntity) userController.getUserById(1);

		verify(userService, atLeastOnce()).findUserById(1);
		assertThat(user.getId()).isEqualTo(1);

	}
	
	@DisplayName("Given an id,then find a user in database and connect to the other microservice to return more information, that matches with the id")
	@Test
	void getUserByIdWithExtraInfo () throws InterruptedException {

		userModelDTO.setId(1);

		when(featureFlag.isEnableUserExtraInfo()).thenReturn(true);
		when(userService.getUserWithAvgSpentAndFidelityPoints(1)).thenReturn(userModelDTO);

		UserEntityDTO user = (UserEntityDTO) userController.getUserById(1);

		verify(userService, atLeastOnce()).getUserWithAvgSpentAndFidelityPoints(1);
		assertThat(user.getId()).isEqualTo(1);

	}


	@DisplayName("Given a name,then find a user in database, that matches with the name")
	@Test
	void getUserByName_test() {

		String name = "Pepito";

		List<UserEntity> users = new ArrayList<>();
		users.add(userModel);

		when(userService.findAllByName(name)).thenReturn(users);
		List<UserEntity> userResult = userController.getUserByName(name);

		assertThat(userResult).allSatisfy(user -> assertThat(user.getName()).isEqualTo(name));
		verify(userService, atLeastOnce()).findAllByName(name);

	}
	
	
	@DisplayName("Given a id,then find a user in database, that matches with the id and delete this user")
	@Test
	void deleteUserById_test() {

		int id = 1;

		doNothing().when(userService).deleteUserById(id);

		ResponseEntity<Void> response = userController.deleteUserById(id);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		verify(userService, atLeastOnce()).deleteUserById(id);

	}

}

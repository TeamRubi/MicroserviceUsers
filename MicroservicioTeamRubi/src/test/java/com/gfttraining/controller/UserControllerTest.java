package com.gfttraining.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.gfttraining.config.FeatureFlag;
import com.gfttraining.connection.RetrieveInformationFromExternalMicroservice;
import com.gfttraining.dto.UserEntityDTO;
import com.gfttraining.entity.FavoriteProduct;
import com.gfttraining.entity.UserEntity;
import com.gfttraining.service.UserService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Mock
	private UserService userService;

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
		userModelDTO = new UserEntityDTO(12L, "pepe@pepe.com", "Pedro", "Chapo", "calle falsa", "SPAIN", "TRANSFER", BigDecimal.valueOf(0), 0, null);
	}


	@DisplayName("GIVEN no information,WHEN the endpoint is called,THEN return a List of Users")
	@Test
	void getAllUsers_test() throws Exception {

		List<UserEntity> users = Arrays.asList(userModel);
		when(userService.findAll()).thenReturn(users);

		List<UserEntity> existingUsers = userController.getAllUsers();
		assertThat(existingUsers).containsAll(users);

	}

	@DisplayName("GIVEN no information,WHEN the endpoint is called,THEN delete a List of Users")
	@Test
	void deleteAllUsers_test() throws Exception {

		Mockito.doNothing().when(userService).deleteAllUsers();
		userController.deleteAllUsers();
		Mockito.verify(userService, Mockito.times(1)).deleteAllUsers();

	}

	@DisplayName("GIVEN a file,WHEN import all of Users, THEN save this users into the database")
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

	@DisplayName("GIVEN a fields,WHEN user is create , THEN save this user into the database")
	@Test
	void createUser_test() {

		when(userService.createUser(userModel)).thenReturn(userModel);
		ResponseEntity<UserEntity> response = userController.createUser(userModel);

		assertThat(userModel).isEqualTo(response.getBody());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);


	}

	@DisplayName("GIVEN a fields,WHEN user is update , THEN modify and save this user modifications into the database")
	@Test
	void updateUserById_test() {

		when(userService.updateUserById(1, userModel)).thenReturn(userModel);

		ResponseEntity<UserEntity> response = userController.updateUserById(1, userModel);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(userModel);

	}

	@DisplayName("GIVEN and email, WHEN the endpoint is called, THEN returns a UserModel to show a User that matches with the email")
	@Test
	void getUserByEmail_test() {

		String email = "pedro@chapo.com"; 

		when(userService.findUserByEmail(email)).thenReturn(userModel);

		ResponseEntity<UserEntity> response = userController.getUserByEmail(email);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(userModel);

	}

	@DisplayName("GIVEN and product, WHEN the endpoint is called, THEN the product is added into a user to show their favorite product")
	@Test
	void addFavoriteProduct_test() throws Exception {

		//mocking service
		int productId = 2;
		userModel.addFavorite(new FavoriteProduct(1,1,productId));
		when(userService.addFavoriteProduct(anyInt(), anyInt())).thenReturn(Mono.just(userModel));
		//mocking http request
		when(retrieveInfo.getExternalInformation(anyString(), any())).thenReturn(Mono.empty());

		ResponseEntity<UserEntity> response = userController.addFavoriteProduct(1, productId).toFuture().get();

		verify(userService, atLeastOnce()).addFavoriteProduct(1, productId);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(userModel);

	}

	@DisplayName("GIVEN a product, WHEN the endpoint is called, THEN throw an exception")
	@Test
	void addFavoriteProductWithNotExistingProduct_test() throws Exception {

		int productId = 200;
		when(retrieveInfo.getExternalInformation(anyString(), any())).thenReturn(Mono.error(WebClientResponseException
				.create(HttpStatus.NOT_FOUND.value(), "Not found", HttpHeaders.EMPTY, null, Charset.defaultCharset())));

		Mono<ResponseEntity<UserEntity>> result = userController.addFavoriteProduct(1,productId);

		StepVerifier.create(result)
				.expectErrorMatches(throwable-> throwable instanceof ResponseStatusException &&
						((ResponseStatusException) throwable).getStatus() == HttpStatus.NOT_FOUND &&
						Objects.requireNonNull(((ResponseStatusException) throwable).getMessage()).contains("Product with id " + productId + " not found" ))
				.verify();

	}

	@DisplayName("GIVEN a product, WHEN the endpoint is called, THEN the favorite producte from user is deleted")
	@Test
	void deleteFavoriteProduct_test() throws Exception {

		int productId = 2;

		ResponseEntity<Void> response = userController.deleteFavoriteProduct(1, productId);

		verify(userService, atLeastOnce()).deleteFavoriteProduct(1, productId);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

	}

	@DisplayName("GIVEN a list of Users, WHEN the endpoint is called, THEN the favorite producte from all of users is deleted")
	@Test
	void deleteFavoriteProductFromAllUsers_test() throws Exception {

		int productId = 2;

		ResponseEntity<Void> response = userController.deleteFavoriteProductFromAllUsers(productId);

		verify(userService, atLeastOnce()).deleteFavoriteProductFromAllUsers(productId);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

	}



	@DisplayName("GIVEN and id, WHEN the endpoint is called, THEN returns a UserModel to show a User that matches with the id")
	@Test
	void getUserById () throws InterruptedException {

		userModel.setId(1);

		when(featureFlag.isEnableUserExtraInfo()).thenReturn(false);
		when(userService.findUserById(1)).thenReturn(userModel);

		Mono<Object> user = userController.getUserById(1);

		verify(userService, atLeastOnce()).findUserById(1);
		StepVerifier.create(user).expectNext(userModelDTO);

	}

	@DisplayName("GIVEN and id, WHEN the endpoint is called and connect to the other microservice to return more information, THEN returns a UserModelDTO to show a User that matches with the id")
	@Test
	void getUserByIdWithExtraInfo() throws InterruptedException {

		userModelDTO.setId(1L);

		when(featureFlag.isEnableUserExtraInfo()).thenReturn(true);
		when(userService.getUserWithAvgSpentAndFidelityPoints(1)).thenReturn(Mono.just(userModelDTO));

		Mono<UserEntityDTO> user = userController.getUserById(1).map(obj -> (UserEntityDTO) obj);

		verify(userService, atLeastOnce()).getUserWithAvgSpentAndFidelityPoints(1);
		StepVerifier.create(user).expectNext(userModelDTO);

	}



	@DisplayName("GIVEN a name, WHEN the endpoint is called, THEN returns a UserModel to show a User that matches with the name")
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


	@DisplayName("GIVEN an id, WHEN the endpoint is called, THEN delete a User that matches with the id")
	@Test
	void deleteUserById_test() {

		int id = 1;

		doNothing().when(userService).deleteUserById(id);

		ResponseEntity<Void> response = userController.deleteUserById(id);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		verify(userService, atLeastOnce()).deleteUserById(id);

	}

}

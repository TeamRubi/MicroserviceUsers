package com.gfttraining.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

import javax.persistence.EntityNotFoundException;

import org.h2.engine.User;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfttraining.config.FeatureFlag;
import com.gfttraining.connection.RetrieveInformationFromExternalMicroservice;
import com.gfttraining.dto.UserEntityDTO;
import com.gfttraining.entity.CartEntity;
import com.gfttraining.entity.FavoriteProduct;
import com.gfttraining.entity.ProductEntity;
import com.gfttraining.entity.UserEntity;
import com.gfttraining.exception.DuplicateEmailException;
import com.gfttraining.exception.DuplicateFavoriteException;
import com.gfttraining.repository.FavoriteRepository;
import com.gfttraining.repository.UserRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private FavoriteRepository favoriteRepository;

	@Mock
	private FeatureFlag featureFlag;

	@Mock
	private RetrieveInformationFromExternalMicroservice retrieveInformationFromExternalMicroservice;

	@Mock
	private ModelMapper modelMapper;

	private String emailModel;
	private UserEntity userModel;
	private Optional<UserEntity> optionalUserModel;
	private CartEntity cartEntity;
	private ProductEntity product1point;
	private ProductEntity product3points;
	private ProductEntity product5points;
	private ProductEntity product10points;

	@BeforeEach
	public void createUser() {
		emailModel = "pedro@chapo.com";
		userModel = new UserEntity(emailModel, "Pedro", "Chapo", "calle falsa", "SPAIN");
		optionalUserModel = Optional.of(userModel);
		UserEntityDTO userModelDTO = new UserEntityDTO(12, emailModel, "Pedro", "Chapo", "calle falsa", "SPAIN", "TRANSFER", BigDecimal.valueOf(0), 0, null);
	}

	@BeforeEach
	public void createCartWithProducts() {

		cartEntity = new CartEntity();

		product1point = new ProductEntity(1, 2, "product", cartEntity.getId(), "patatas",
				BigDecimal.valueOf(10),2, BigDecimal.valueOf(20));

		product3points = new ProductEntity(1, 2, "product", cartEntity.getId(), "patatas",
				BigDecimal.valueOf(30),1, BigDecimal.valueOf(30));

		product5points = new ProductEntity(1, 2, "product", cartEntity.getId(), "patatas",
				BigDecimal.valueOf(50),1, BigDecimal.valueOf(50));

		product10points = new ProductEntity(1, 2, "product", cartEntity.getId(), "patatas",
				BigDecimal.valueOf(100),1, BigDecimal.valueOf(100));


		cartEntity = CartEntity.builder().userId(12)
				.createdAt(LocalDateTime.of(2022, 4, 11, 10, 30, 0))
				.updatedAt(LocalDateTime.of(2022, 4, 12, 10, 30, 0))
				.status("SUBMITTED").build();

	}

	@DisplayName("GIVEN an id, WHEN the endpoint is called, THEN returns a UserntityDTO")
	@Test
	void getUserById_test() {

		userModel.setId(1);
		userModel.setName("Erna");

		when(userRepository.findById(1)).thenReturn(Optional.of(userModel));

		UserEntity result = userService.findUserById(1);

		assertNotNull(result);
		assertEquals(userModel.getName(), result.getName());

		verify(userRepository, times(1)).findById(1);

	}

	@DisplayName("GIVEN a non existing id, WHEN the endpoint is called, THEN throws an exception")
	@Test
	void getUserByIdNotFound_test(){

		when(userRepository.findById(1234)).thenReturn((Optional.empty()));

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.findUserById(1234));
		assertEquals("Usuario con el id: " + 1234 + " no encontrado", exception.getMessage());

	}

	@DisplayName("GIVEN a name, WHEN the endpoint is called, THEN returns a list of UserEntity")
	@Test
	void getAllUsersByName_test(){

		List <UserEntity> userListTest1 = new ArrayList<>();
		UserEntity userTest1 = new UserEntity();
		userTest1.setId(1);
		userTest1.setName("Erna");
		userListTest1.add(userTest1);

		when(userRepository.findAllByName("Erna")).thenReturn((userListTest1));

		List<UserEntity> result = userService.findAllByName("Erna");

		assertNotNull(result);
		assertEquals(userListTest1.get(0).getName(), result.get(0).getName());

		verify(userRepository, times(1)).findAllByName("Erna");

	}

	@DisplayName("GIVEN a non existing name, WHEN the endpoint is called, THEN throws an exception")
	@Test
	void getAllUsersByNameNotFound_test(){

		List <UserEntity> userListTest1 = new ArrayList<>();
		String name = "Ernaaa";

		when(userRepository.findAllByName(name)).thenReturn((userListTest1));

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.findAllByName(name));
		assertEquals("Usuario con el nombre: " + name + " no encontrado", exception.getMessage());

	}

	@DisplayName("GIVEN no information, WHEN the endpoint is called, THEN returns a list of all users")
	@Test
	void getAllUsers() {
		List<UserEntity> expectedUsers = new ArrayList<>();
		expectedUsers.add(userModel);
		expectedUsers.add(userModel);

		when(userRepository.findAll()).thenReturn(expectedUsers);

		List<UserEntity> actualUsers = userService.findAll();
		assertEquals(expectedUsers, actualUsers);
	}

	@DisplayName("GIVEN a list of Users, WHEN the endpoint is called, THEN saves all users to DB")
	@Test
	void testSaveAllUsers() {

		List<UserEntity> usersList = new ArrayList<>();
		usersList.add(userModel);
		usersList.add(userModel);

		userService.saveAllUsers(usersList);
		verify(userRepository).saveAll(usersList);

	}

	@DisplayName("GIVEN no information, WHEN the endpoint is called, THEN deletes all users on DB")
	@Test
	void testDeleteAllUsers() {
		userService.deleteAllUsers();
		verify(userRepository).deleteAll();

	}

	@DisplayName("GIVEN an id, WHEN the endpoint is called, THEN deletes the user with that id to DB")
	@Test
	void deleteUserById_test() {
		userService.deleteUserById(1);
		verify(userRepository, times(1)).deleteById(1);
	}

	@DisplayName("GIVEN a non existing id, WHEN the endpoint is called, THEN throws an exception")
	@Test
	void deleteUserByIdNotFound_test() {

		doThrow(EmptyResultDataAccessException.class).when(userRepository).deleteById(1234);
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.deleteUserById(1234));

		assertEquals("No se ha podido eliminar el usuario con el id: " + 1234 + " de la base de datos",
				exception.getMessage());

	}

	@DisplayName("GIVEN a UserEntity, WHEN the endpoint is called, THEN adds a user to DB")
	@Test
	void createUser_test() {
		when(userRepository.save(userModel)).thenReturn(userModel);
		UserEntity createduser = userService.createUser(userModel);
		assertThat(userModel).isEqualTo(createduser);
	}

	@DisplayName("GIVEN a UserEntity and id, WHEN the endpoint is called, THEN updates the user with that id to DB")
	@Test
	void updateUserById_test() {

		userModel.setId(1);
		UserEntity updatedUser = new UserEntity();
		updatedUser.setName("Jose");

		when(userRepository.findById(1)).thenReturn(Optional.of(userModel));
		when(userRepository.existsByEmail(any())).thenReturn(false);

		userModel.setName(updatedUser.getName());
		when(userRepository.save(any())).thenReturn(userModel);

		UserEntity result = userService.updateUserById(1, updatedUser);

		verify(userRepository, times(1)).findById(1);
		verify(userRepository, times(1)).save(userModel);
		assertThat(updatedUser.getName()).isEqualTo(result.getName());
		assertThat(result.getLastName()).isNotNull();

	}

	@DisplayName("GIVEN a non existing id, WHEN the endpoint is called, THEN throws an exception")
	@Test
	void updateUserByIdNoValidId_test() {

		when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.updateUserById(1, userModel))
				.isInstanceOf(ResponseStatusException.class).hasMessageContaining("User not found");
	}

	@DisplayName("GIVEN an existing email, WHEN the endpoint is called, THEN throws an exception")
	@Test
	void createUserWithEmailThatAlreadyExists_test() {

		when(userRepository.existsByEmail(emailModel)).thenReturn(true);

		assertThatThrownBy(() -> userService.createUser(userModel)).isInstanceOf(DuplicateEmailException.class)
				.hasMessageContaining("email " + userModel.getEmail() + " is already in use");

	}

	@DisplayName("GIVEN an existing email, WHEN the endpoint is called, THEN throws an exception")
	@Test
	void updateUserByIdWithEmailThatAlreadyExists_test() {

		when(userRepository.existsByEmail(emailModel)).thenReturn(true);
		when(userRepository.findById(1)).thenReturn(optionalUserModel);

		assertThatThrownBy(() -> userService.updateUserById(1, userModel))
				.isInstanceOf(DuplicateEmailException.class)
				.hasMessageContaining("email " + userModel.getEmail() + " is already in use");

	}

	@DisplayName("GIVEN an email, WHEN the endpoint is called, THEN returns a UserEntity")
	@Test
	void getUserByEmailBasic_test() {

		when(userRepository.findByEmail(emailModel)).thenReturn(userModel);

		UserEntity foundUser = userService.findUserByEmail(emailModel);

		assertThat(foundUser).isEqualTo(userModel);
	}

	@Test
	@DisplayName("GIVEN a non existing email, WHEN the emails are repeated, THEN throws exception")
	void getUserByEmailWithEmailNotFound_test() {

		when(userRepository.findByEmail(emailModel)).thenReturn(null);

		assertThatThrownBy(() -> userService.findUserByEmail(emailModel))
				.isInstanceOf(ResponseStatusException.class)
				.hasMessageContaining("User with email " + emailModel + " not found");

	}

	@DisplayName("GIVEN a user id, WHEN the endpoint is called, THEN returns a UserEntityDTO with fidelityPoints")
	@Test
	void getUserPoints_test() throws Exception{

		List<CartEntity> carts = new ArrayList<>();
		List<ProductEntity> products = new ArrayList<>();
		cartEntity.setProducts(products);
		carts.add(cartEntity);

		when(retrieveInformationFromExternalMicroservice.getExternalInformation(anyString(), any())).thenReturn(Mono.just(carts));
		when(userRepository.findById(anyInt())).thenReturn(optionalUserModel);

		Mono<UserEntityDTO> result = userService.getUserWithAvgSpentAndFidelityPoints(12);

		StepVerifier.create(result).assertNext(user -> assertThat(user.getPoints()).isZero()).expectComplete().verify();
	}

	@DisplayName("GIVEN a user id, WHEN the endpoint is called, THEN returns a UserEntityDTO with  0 avgSpent")
	@Test
	void getAvgSpentIs0_test() {

		List<CartEntity> carts = new ArrayList<>();

		List<ProductEntity> products = new ArrayList<>();
		cartEntity.setProducts(products);

		carts.add(cartEntity);

		BigDecimal result= userService.calculateAvgSpent(carts);

		assertThat(result).isEqualTo(BigDecimal.valueOf(0));

	}

	@DisplayName("GIVEN a user id, WHEN the endpoint is called, THEN returns a UserEntityDTO with 1 fidelityPoints")
	@Test
	void getPoints_is_1_test(){

		List<CartEntity> carts = new ArrayList<>();
		List<ProductEntity> products = new ArrayList<>(Collections.singletonList(product1point));
		cartEntity.setProducts(products);

		carts.add(cartEntity);

		Integer result = userService.getPoints(carts);

		assertThat(result).isEqualTo(1);
	}

	@DisplayName("GIVEN a user id, WHEN the endpoint is called, THEN returns a UserEntityDTO with 3 fidelityPoints")
	@Test
	void getPoints_is_3_test(){

		List<CartEntity> carts = new ArrayList<>();
		List<ProductEntity> products = new ArrayList<>(Collections.singletonList(product3points));
		cartEntity.setProducts(products);
		carts.add(cartEntity);

		Integer result = userService.getPoints(carts);

		assertThat(result).isEqualTo(3);
	}

	@DisplayName("GIVEN a user id, WHEN the endpoint is called, THEN returns a UserEntityDTO with 5 fidelityPoints")
	@Test
	void getPoints_is_5_test(){

		List<CartEntity> carts = new ArrayList<>();
		List<ProductEntity> products = new ArrayList<>(Collections.singletonList(product5points));
		cartEntity.setProducts(products);
		carts.add(cartEntity);

		Integer result = userService.getPoints(carts);

		assertThat(result).isEqualTo(5);
	}

	@DisplayName("GIVEN a user id, WHEN the endpoint is called, THEN returns a UserEntityDTO with 10 fidelityPoints")
	@Test
	void getPoints_is_10_test(){

		List<CartEntity> carts = new ArrayList<>();
		List<ProductEntity> products = new ArrayList<>(Collections.singletonList(product10points));
		cartEntity.setProducts(products);
		carts.add(cartEntity);

		when(featureFlag.isEnablePromotion()).thenReturn(true);
		Integer result = userService.getPoints(carts);

		assertThat(result).isEqualTo(20);
	}


	@DisplayName("GIVEN a user id, WHEN the endpoint is called, THEN returns a UserEntityDTO with avgSpent")
	@Test
	void getAvgSpent_test() throws InterruptedException {

		List<CartEntity> carts = new ArrayList<>();
		List<ProductEntity> products = new ArrayList<>();
		products.add(product1point);
		products.add(product3points);
		cartEntity.setProducts(products);

		carts.add(cartEntity);

		when(retrieveInformationFromExternalMicroservice.getExternalInformation(anyString(), any())).thenReturn(Mono.just(carts));
		when(userRepository.findById(anyInt())).thenReturn(optionalUserModel);

		Mono<UserEntityDTO> result = userService.getUserWithAvgSpentAndFidelityPoints(12);

		StepVerifier.create(result)
				.assertNext(user -> assertThat(user.getAverageSpent()).isEqualTo(BigDecimal.valueOf(50/products.size())))
				.expectComplete().verify();

	}

	@DisplayName("GIVEN a user id and product id, WHEN the endpoint is called, THEN adds to the user the favorite product")
	@Test
	void addFavoriteProduct_test() throws ExecutionException, InterruptedException {

		FavoriteProduct favorite = new FavoriteProduct(1,5);
		userModel.addFavorite(favorite);

		when(userRepository.findById(anyInt())).thenReturn(Optional.of(userModel));
		when(favoriteRepository.existsByUserIdAndProductId(anyInt(), anyInt())).thenReturn(false);
		when(favoriteRepository.save(any(FavoriteProduct.class))).thenReturn(favorite);

		Mono<UserEntity> result = userService.addFavoriteProduct(1, 5);

		StepVerifier.create(result)
				.assertNext(user -> assertThat(user).isEqualTo(userModel))
				.expectComplete().verify();

		verify(favoriteRepository, atLeastOnce()).save(favorite);
		verify(userRepository, atLeastOnce()).findById(1);

	}

	@DisplayName("GIVEN a non existing user id and existing product id, WHEN the endpoint is called, THEN throws an exception")
	@Test
	void addFavoriteProductWithNotExistingUser_test() {

		int userId = 600;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		Mono<UserEntity> result = userService.addFavoriteProduct(userId, 5);

		StepVerifier.create(result)
				.expectErrorMatches(throwable -> throwable instanceof ResponseStatusException &&
						((ResponseStatusException) throwable).getStatus() == HttpStatus.NOT_FOUND &&
						Objects.requireNonNull(((ResponseStatusException) throwable).getMessage()).contains("User with id " + userId + " not found"))
				.verify();

	}

	@DisplayName("GIVEN a user id and an existing favorite product id, WHEN the endpoint is called, THEN throws an exception")
	@Test
	void addFavoriteProductWithExistingFavorite_test() {

		int userId = 60;
		int productId = 50;

		when(userRepository.findById(anyInt())).thenReturn(Optional.of(userModel));
		when(favoriteRepository.existsByUserIdAndProductId(anyInt(), anyInt())).thenReturn(true);

		Mono<UserEntity> result = userService.addFavoriteProduct(userId, productId);

		StepVerifier.create(result).expectErrorMatches(throwable -> throwable instanceof DuplicateFavoriteException &&
				throwable.getMessage().contains("Product with id " + productId + " is already favorite for user with id " + userId)).verify();

	}

	@DisplayName("GIVEN a user id and product id, WHEN the endpoint is called, THEN deletes to the user the favorite product id")
	@Test
	void deleteFavoriteProduct_test() {

		when(favoriteRepository.existsByUserIdAndProductId(anyInt(), anyInt())).thenReturn(true);
		userService.deleteFavoriteProduct(1, 5);

		verify(favoriteRepository, atLeastOnce()).existsByUserIdAndProductId(1, 5);
		verify(favoriteRepository, atLeastOnce()).deleteByUserIdAndProductId(1, 5);
	}

	@DisplayName("GIVEN a user id and non existing product id, WHEN the endpoint is called, THEN throws an exception")
	@Test
	void deleteFavoriteProductWithNotExistingFavorite_test() {

		int userId = 1;
		int productId = 5;

		when(favoriteRepository.existsByUserIdAndProductId(anyInt(), anyInt())).thenReturn(false);

		assertThatThrownBy(()-> userService.deleteFavoriteProduct(userId,productId))
				.isInstanceOf(EmptyResultDataAccessException.class)
				.hasMessageContaining("User with id " + userId + " does not have product with id " + productId + " as favorite");
	}

	@DisplayName("GIVEN a product id, WHEN the endpoint is called, THEN deletes to every user that favorite product id")
	@Test
	void deleteFavoriteProductFromAllUsers_test() {

		int productId = 5;

		when(favoriteRepository.existsByProductId(anyInt())).thenReturn(true);
		userService.deleteFavoriteProductFromAllUsers(productId);

		verify(favoriteRepository, atLeastOnce()).existsByProductId(productId);
		verify(favoriteRepository, atLeastOnce()).deleteByProductId(productId);
	}

	@DisplayName("GIVEN a non existing product id, WHEN the endpoint is called, THEN throws an exception")
	@Test
	void deleteFavoriteProductFromAllUsersWithNoProductInFavorites_test() {

		int productId = 5;
		when(favoriteRepository.existsByProductId(anyInt())).thenReturn(false);

		assertThatThrownBy(()-> userService.deleteFavoriteProductFromAllUsers(productId))
				.isInstanceOf(EmptyResultDataAccessException.class)
				.hasMessageContaining("Product " + productId + " is not in the favorites of any user");

	}

	@DisplayName("GIVEN a file of userEntities, WHEN the endpoint is called, THEN saves all users to DB")
    @Test
    void testSaveAllImportedUsers() throws Exception {
        List<UserEntity> users = Arrays.asList(userModel, userModel);
        byte[] content = new ObjectMapper().writeValueAsBytes(users);
        MockMultipartFile file = new MockMultipartFile("users.json", content);

        doNothing().when(userRepository).deleteAll();
        when(userRepository.saveAll(anyList())).thenReturn(users);

        ResponseEntity<Void> response = userService.saveAllImportedUsers(file);

        verify(userRepository, times(1)).deleteAll();
        verify(userRepository, times(1)).saveAll(users);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

	@DisplayName("GIVEN a file with wrong userEntities, WHEN the endpoint is called, THEN throws an exception")
	@Test
	void testSaveAllImportedUsersWithError() throws IOException {

		MultipartFile file = new MockMultipartFile("file", new byte[0]);

		doThrow(new RuntimeException("There has been an error at deleting users")).when(userRepository).deleteAll();

		assertThatThrownBy(()-> userService.saveAllImportedUsers(file))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("There has been an error saving users to database by file");

		verify(userRepository, times(1)).deleteAll();
		verifyNoMoreInteractions(userRepository);
	}


}

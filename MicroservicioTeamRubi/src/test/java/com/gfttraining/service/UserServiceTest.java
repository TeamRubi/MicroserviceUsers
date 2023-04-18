package com.gfttraining.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import com.gfttraining.config.FeatureFlag;
import com.gfttraining.connection.RetrieveInformationFromExternalMicroservice;
import com.gfttraining.dto.Mapper;
import com.gfttraining.dto.UserEntityDTO;
import com.gfttraining.entity.CartEntity;
import com.gfttraining.entity.FavoriteProduct;
import com.gfttraining.entity.ProductEntity;
import com.gfttraining.entity.UserEntity;
import com.gfttraining.exception.DuplicateEmailException;
import com.gfttraining.exception.DuplicateFavoriteException;
import com.gfttraining.repository.FavoriteRepository;
import com.gfttraining.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	@Autowired
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private FavoriteRepository favoriteRepository;

	@Autowired
	@Mock
	private FeatureFlag featureFlag;

	@Mock
	private RetrieveInformationFromExternalMicroservice retrieveInformationFromExternalMicroservice;

	@Autowired
	@Mock
	private Mapper mapper;

	@Mock
	private ModelMapper modelMapper;

	@Mock
	RestTemplate restTemplate;

	private String emailModel;
	private UserEntity userModel;
	private UserEntityDTO userModelDTO;
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
		userModelDTO = new UserEntityDTO(12, emailModel, "Pedro", "Chapo", "calle falsa", "SPAIN", "TRANSFER", BigDecimal.valueOf(0), 0, null);
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

	@Test
	void getUserByIdNotFound_test(){

		when(userRepository.findById(1234)).thenReturn((Optional.empty()));

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.findUserById(1234));

		assertEquals("Usuario con el id: " + 1234 + " no encontrado", exception.getMessage());

	}

	@Test
	void getBasicUserInfoById_test() {

		when(userRepository.findById(1)).thenReturn(optionalUserModel);

		UserEntity result = userService.getBasicUserInfoById(1);

		verify(userRepository, Mockito.times(1)).findById(1);
		assertEquals(userModel, result);
	}

	@Test
	void getBasicUserInfoByIdNotFound_test() {

		when(userRepository.findById(1234)).thenReturn((Optional.empty()));

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			userService.getBasicUserInfoById(1234);
		});

		assertEquals("Informacion basica del usuario con el id: " + 1234 + " no encontrado", exception.getMessage());
	}


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

	@Test
	void getAllUsersByNameNotFound_test(){

		List <UserEntity> userListTest1 = new ArrayList<>();
		String name = "Ernaaa";

		when(userRepository.findAllByName(name)).thenReturn((userListTest1));

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.findAllByName(name));

		assertEquals("Usuario con el nombre: " + name + " no encontrado", exception.getMessage());

	}

	@Test
	void getAllUsers() {
		List<UserEntity> expectedUsers = new ArrayList<>();
		expectedUsers.add(userModel);
		expectedUsers.add(userModel);

		when(userRepository.findAll()).thenReturn(expectedUsers);

		List<UserEntity> actualUsers = userService.findAll();

		assertEquals(expectedUsers, actualUsers);
	}

	@Test
	void testSaveAllUsers() {

		List<UserEntity> usersList = new ArrayList<>();
		usersList.add(userModel);
		usersList.add(userModel);

		userService.saveAllUsers(usersList);

		verify(userRepository).saveAll(usersList);

	}

	@Test
	void testDeleteAllUsers() {

		userService.deleteAllUsers();

		verify(userRepository).deleteAll();

	}


	@Test
	void deleteUserById_test() {

		userService.deleteUserById(1);

		verify(userRepository, times(1)).deleteById(1);
	}

	@Test
	void deleteUserByIdNotFound_test() {

		doThrow(EmptyResultDataAccessException.class).when(userRepository).deleteById(1234);

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.deleteUserById(1234));

		assertEquals("No se ha podido eliminar el usuario con el id: " + 1234 + " de la base de datos",
				exception.getMessage());

	}

	@Test
	void createUser_test() {
		when(userRepository.save(userModel)).thenReturn(userModel);
		UserEntity createduser = userService.createUser(userModel);
		assertThat(userModel).isEqualTo(createduser);
	}

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

	}

	@Test
	void updateUserByIdNoValidId_test() {

		when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.updateUserById(anyInt(), userModel))
		.isInstanceOf(ResponseStatusException.class).hasMessageContaining("User not found");

	}

	@Test
	void updateUserByIdWithNullValues_test() {

		userModel.setId(1);

		UserEntity updatedUser = new UserEntity();
		updatedUser.setName("Jose");

		when(userRepository.findById(1)).thenReturn(Optional.of(userModel));

		when(userRepository.existsByEmail(anyString())).thenReturn(false);

		when(userRepository.save(userModel)).thenReturn(userModel);


		UserEntity result = userService.updateUserById(1, updatedUser);

		assertThat(result.getLastname()).isNotEqualTo(null);

	}

	@Test
	void createUserWithEmailThatAlreadyExists_test() {

		when(userRepository.existsByEmail(emailModel)).thenReturn(true);

		assertThatThrownBy(() -> userService.createUser(userModel)).isInstanceOf(DuplicateEmailException.class)
		.hasMessageContaining("email " + userModel.getEmail() + " is already in use");

	}

	@Test
	void updateUserByIdWithEmailThatAlreadyExists_test() {

		Optional<UserEntity> newUser = Optional.of(userModel);

		when(userRepository.existsByEmail(emailModel)).thenReturn(true);
		when(userRepository.findById(1)).thenReturn(newUser);

		assertThatThrownBy(() -> userService.updateUserById(1, newUser.get()))
		.isInstanceOf(DuplicateEmailException.class)
		.hasMessageContaining("email " + newUser.get().getEmail() + " is already in use");

	}

	@Test
	@DisplayName("Given a user email, Then returns a user, When the emails match")
	void getUserByEmailBasic_test() {

		userService.createUser(userModel);
		when(userRepository.findByEmail(emailModel)).thenReturn(userModel);

		UserEntity foundUser = userService.findUserByEmail(emailModel);

		assertThat(foundUser).isEqualTo(userModel);
	}

	@Test
	@DisplayName("Given a user email, Then throws exception, When the emails are repeated")
	void getUserByEmailWithEmailNotFound_test() {

		when(userRepository.findByEmail(emailModel)).thenReturn(null);

		assertThatThrownBy(() -> userService.findUserByEmail(emailModel))
		.isInstanceOf(ResponseStatusException.class)
		.hasMessageContaining("User with email " + emailModel + " not found");

	}

	@Test
	void getUserPoints_test() throws Exception{

		List<CartEntity> carts = new ArrayList<>();

		carts.add(cartEntity);

		when(retrieveInformationFromExternalMicroservice.getExternalInformation("http://localhost:8082/carts/user/" + 12, new ParameterizedTypeReference<List<CartEntity>>() {
		})).thenReturn(carts);

		when(userRepository.findById(anyInt())).thenReturn(optionalUserModel);

		when(mapper.toUserWithAvgSpentAndFidelityPoints(userModel, BigDecimal.valueOf(20), 1)).thenReturn(userModelDTO);

		assertThat(0).isEqualTo(userService.getUserWithAvgSpentAndFidelityPoints(12).getPoints());
	}

	@Test
	void getAvgSpentIs0_test() {

		List<CartEntity> carts = new ArrayList<>();

		List<ProductEntity> products = new ArrayList<>();
		cartEntity.setProducts(products);

		carts.add(cartEntity);

		BigDecimal result=userService.calculateAvgSpent(carts);

		assertThat(BigDecimal.valueOf(0)).isEqualTo(result);

	}

	@Test
	void getPoints_is_1_test(){

		List<CartEntity> carts = new ArrayList<>();

		List<ProductEntity> products = new ArrayList<>(Arrays.asList(product1point));

		cartEntity.setProducts(products);

		carts.add(cartEntity);

		Integer result = userService.getPoints(carts);

		assertThat(1).isEqualTo(result);
	}

	@Test
	void getPoints_is_3_test(){

		List<CartEntity> carts = new ArrayList<>();

		List<ProductEntity> products = new ArrayList<>(Arrays.asList(product3points));

		cartEntity.setProducts(products);

		carts.add(cartEntity);

		Integer result = userService.getPoints(carts);

		assertThat(3).isEqualTo(result);
	}


	@Test
	void getPoints_is_5_test(){

		List<CartEntity> carts = new ArrayList<>();

		List<ProductEntity> products = new ArrayList<>(Arrays.asList(product5points));

		cartEntity.setProducts(products);

		carts.add(cartEntity);

		Integer result = userService.getPoints(carts);

		assertThat(5).isEqualTo(result);
	}




	@Test
	void getPoints_is_10_test(){

		List<CartEntity> carts = new ArrayList<>();

		List<ProductEntity> products = new ArrayList<>(Arrays.asList(product10points));

		cartEntity.setProducts(products);

		carts.add(cartEntity);

		Integer result = userService.getPoints(carts);

		assertThat(10).isEqualTo(result);
	}



	@Test
	void getAvgSpent_test() {

		List<CartEntity> carts = new ArrayList<>();
		List<ProductEntity> products = new ArrayList<>();
		cartEntity.setProducts(products);

		carts.add(cartEntity);

		when(retrieveInformationFromExternalMicroservice.getExternalInformation("http://localhost:8082/carts/user/" + 12, new ParameterizedTypeReference<List<CartEntity>>() {
		})).thenReturn(carts);

		when(userRepository.findById(anyInt())).thenReturn(optionalUserModel);

		when(mapper.toUserWithAvgSpentAndFidelityPoints(userModel, BigDecimal.valueOf(20), 1)).thenReturn(userModelDTO);

		assertThat(BigDecimal.valueOf(0)).isEqualTo(userService.getUserWithAvgSpentAndFidelityPoints(12).getAverageSpent());

	}

	@Test
	void addFavoriteProduct_test() {

		FavoriteProduct favorite = new FavoriteProduct(1,5);

		userModel.addFavorite(favorite);

		when(userRepository.findById(anyInt())).thenReturn(Optional.of(userModel));

		when(favoriteRepository.existsByUserIdAndProductId(anyInt(), anyInt())).thenReturn(false);

		when(favoriteRepository.save(any(FavoriteProduct.class))).thenReturn(favorite);

		UserEntity user = userService.addFavoriteProduct(1, 5);

		assertThat(user).isEqualTo(userModel);
		verify(favoriteRepository, atLeastOnce()).save(favorite);
		verify(userRepository, atLeastOnce()).findById(1);

	}

	@Test
	void addFavoriteProductWithNotExistingUser_test() {

		int userId = 600;
		when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

		assertThatThrownBy(()-> userService.addFavoriteProduct(userId,5))
		.isInstanceOf(ResponseStatusException.class)
		.hasMessageContaining("User with id " + userId + " not found");
	}


	@Test
	void addFavoriteProductWithExistingFavorite_test() {

		int userId = 60;
		int productId = 50;

		when(userRepository.findById(anyInt())).thenReturn(Optional.of(userModel));

		when(favoriteRepository.existsByUserIdAndProductId(anyInt(), anyInt())).thenReturn(true);

		assertThatThrownBy(()-> userService.addFavoriteProduct(userId,productId))
		.isInstanceOf(DuplicateFavoriteException.class)
		.hasMessageContaining("Product with id " + productId + " is already favorite for user with id " + userId);

	}

	@Test
	void deleteFavoriteProduct_test() {

		when(favoriteRepository.existsByUserIdAndProductId(anyInt(), anyInt())).thenReturn(true);

		userService.deleteFavoriteProduct(1, 5);

		verify(favoriteRepository, atLeastOnce()).existsByUserIdAndProductId(1, 5);
		verify(favoriteRepository, atLeastOnce()).deleteByUserIdAndProductId(1, 5);
	}

	@Test
	void deleteFavoriteProductWithNotExistingFavorite_test() {

		int userId = 1;
		int productId = 5;

		when(favoriteRepository.existsByUserIdAndProductId(anyInt(), anyInt())).thenReturn(false);

		assertThatThrownBy(()-> userService.deleteFavoriteProduct(userId,productId))
		.isInstanceOf(EmptyResultDataAccessException.class)
		.hasMessageContaining("User with id " + userId + " does not have product with id " + productId + " as favorite");
	}


	@Test
	void deleteFavoriteProductFromAllUsers_test() {

		int productId = 5;

		when(favoriteRepository.existsByProductId(anyInt())).thenReturn(true);

		userService.deleteFavoriteProductFromAllUsers(productId);

		verify(favoriteRepository, atLeastOnce()).existsByProductId(productId);
		verify(favoriteRepository, atLeastOnce()).deleteByProductId(productId);
	}

	@Test
	void deleteFavoriteProductFromAllUsersWithNoProductInFavorites_test() {

		int productId = 5;

		when(favoriteRepository.existsByProductId(anyInt())).thenReturn(false);

		assertThatThrownBy(()-> userService.deleteFavoriteProductFromAllUsers(productId))
		.isInstanceOf(EmptyResultDataAccessException.class)
		.hasMessageContaining("Product " + productId + " is not in the favorites of any user");

	}

	@Test
	void importUsersByFile() throws Exception{

		MultipartFile file = Mockito.mock(MultipartFile.class);

		Mockito.doNothing().when(userRepository).deleteAll();

		byte[] content = "[{\"email\": \"user@gmail.com\", \"name\": \"pedro\", \"lastname\": \"soler\", \"address\": \"monzon\", \"paymentmethod\": \"VISA\"}, {\"email\": \"user@gmail.com\", \"name\": \"pedro\", \"lastname\": \"soler\", \"address\": \"monzon\", \"paymentmethod\": \"VISA\"}]".getBytes();
		when(file.getBytes()).thenReturn(content);

		ResponseEntity<Void> response = userService.saveAllImportedUsers(file);

		verify(userRepository, Mockito.times(1)).deleteAll();
		assertEquals(HttpStatus.CREATED, response.getStatusCode());

	}

	@Test
	public void testSaveAllImportedUsersWithError() throws IOException {

		MultipartFile file = new MockMultipartFile("file", new byte[0]);

		List<UserEntity> users = Arrays.asList();

		doThrow(new RuntimeException("Error al eliminar los usuarios")).when(userRepository).deleteAll();

		ResponseEntity<Void> response = userService.saveAllImportedUsers(file);

		assertThatThrownBy(()-> userService.saveAllImportedUsers(file))
		.isInstanceOf(RuntimeException.class)
		.hasMessageContaining("Error al eliminar los usuarios");

		verify(userRepository, times(1)).deleteAll();
		verifyNoMoreInteractions(userRepository);
	}


}

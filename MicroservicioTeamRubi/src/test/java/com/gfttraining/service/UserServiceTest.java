package com.gfttraining.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.gfttraining.DTO.Mapper;
import com.gfttraining.DTO.UserEntityDTO;
import com.gfttraining.connection.RetrieveCartInformation;
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
	private UserRepository repository;

	@Mock
	private UserEntityDTO userEntityDTO;

	@Mock
	private RetrieveCartInformation retrieveCartInformation;
	
	@Autowired
	@Mock
	private Mapper mapper;
	
	@Mock
	RestTemplate restTemplate;

	UserEntity userModel;
	Optional<UserEntity> userModel2;

	@BeforeEach
	public void createUser() {
		userModel = new UserEntity(12, "pepe@pepe.com", "Pepito", "Perez", "calle falsa", "SPAIN", null, null);
		userModel2 = Optional
				.of(new UserEntity(12, "pepe@pepe.com", "Pepito", "Perez", "calle falsa", "SPAIN", null, null));
		userEntityDTO = new UserEntityDTO(12, "pepe@pepe.com", "Pepito", "Perez", "calle falsa", "SPAIN", "TRANSFERENCIA", BigDecimal.valueOf(0), 0, null);
	}

	@Mock
	private FavoriteRepository favoriteRepository;

	@Test
	void getUserById_test() {

		userModel.setId(1);
		userModel.setName("Erna");

		when(repository.findById(1)).thenReturn(Optional.of(userModel));

		UserEntity result = userService.findUserById(1);

		assertNotNull(result);
		assertEquals(userModel.getName(), result.getName());

		verify(repository, times(1)).findById(1);

	}

	@Test
	void getUserByIdNotFound_test(){

		when(repository.findById(1234)).thenReturn((Optional.empty()));

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			userService.findUserById(1234);
		});

		assertEquals("Usuario con el id: " + 1234 + " no encontrado", exception.getMessage());

	}

	@Test
	void getAllUsersByName_test(){

		List <UserEntity> userListTest1 = new ArrayList<>();
		UserEntity userTest1 = new UserEntity();
		userTest1.setId(1);
		userTest1.setName("Erna");
		userListTest1.add(userTest1);

		when(repository.findAllByName("Erna")).thenReturn((userListTest1));

		List<UserEntity> result = userService.findAllByName("Erna");

		assertNotNull(result);
		assertEquals(userListTest1.get(0).getName(), result.get(0).getName());

		verify(repository, times(1)).findAllByName("Erna");

	}

	@Test
	void getAllUsersByNameNotFound_test(){
		List <UserEntity> userListTest1 = new ArrayList<>();

		when(repository.findAllByName("Ernaaa")).thenReturn((userListTest1));

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			userService.findAllByName("Ernaaa");
		});

		assertEquals("Usuario con el nombre: " + "Ernaaa" + " no encontrado", exception.getMessage());

	}

	@Test
	void getAllUsers() {
		List<UserEntity> expectedUsers = new ArrayList<>();
		expectedUsers.add(userModel);
		expectedUsers.add(userModel);

		when(repository.findAll()).thenReturn(expectedUsers);

		List<UserEntity> actualUsers = userService.findAll();

		assertEquals(expectedUsers, actualUsers);
	}

	@Test
	void testSaveAllUsers() {

		List<UserEntity> usersList = new ArrayList<>();
		usersList.add(userModel);
		usersList.add(userModel);

		userService.saveAllUsers(usersList);

		verify(repository).saveAll(usersList);

	}

	@Test
	void testDeleteAllUsers() {

		userService.deleteAllUsers();

		verify(repository).deleteAll();

	}


	@Test
	void deleteUserById_test() {

		userService.deleteUserById(1);

		verify(repository, times(1)).deleteById(1);
	}

	@Test
	void deleteUserByIdNotFound_test() {

		doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(1234);

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			userService.deleteUserById(1234);
		});

		assertEquals("No se ha podido eliminar el usuario con el id: " + 1234 + " de la base de datos",
				exception.getMessage());

	}

	@Test
	void createUser_test() {
		when(repository.save(userModel)).thenReturn(userModel);
		UserEntity createduser = userService.createUser(userModel);
		assertThat(userModel).isEqualTo(createduser);
	}

	@Test
	void updateUserById_test() {

		userModel.setId(1);

		UserEntity updatedUser = new UserEntity();
		updatedUser.setName("Jose");

		when(repository.findById(1)).thenReturn(Optional.of(userModel));
		when(repository.save(userModel)).thenReturn(userModel);

		UserEntity result = userService.updateUserById(1, updatedUser);

		verify(repository, times(1)).findById(1);
		verify(repository, times(1)).save(userModel);
		assertThat(updatedUser.getName()).isEqualTo(result.getName());

	}

	@Test
	void updateUserByIdNoValidId_test() {

		when(repository.findById(anyInt())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.updateUserById(anyInt(), userModel))
		.isInstanceOf(ResponseStatusException.class).hasMessageContaining("User not found");

	}

	@Test
	void updateUserByIdWithNullValues_test() {

		userModel.setId(1);

		UserEntity updatedUser = new UserEntity();
		updatedUser.setName("Jose");

		when(repository.findById(1)).thenReturn(Optional.of(userModel));
		when(repository.save(userModel)).thenReturn(userModel);

		UserEntity result = userService.updateUserById(1, updatedUser);

		assertThat(result.getLastname()).isNotEqualTo(null);

	}

	@Test
	void createUserWithEmailThatAlreadyExists_test() {

		when(repository.existsByEmail("pepe@pepe.com")).thenReturn(true);

		assertThatThrownBy(() -> userService.createUser(userModel)).isInstanceOf(DuplicateEmailException.class)
		.hasMessageContaining("email " + userModel.getEmail() + " is already in use");

	}

	@Test
	void updateUserByIdWithEmailThatAlreadyExists_test() {

		Optional<UserEntity> newUser = Optional.of(userModel);

		when(repository.existsByEmail("pepe@pepe.com")).thenReturn(true);
		when(repository.findById(1)).thenReturn(newUser);

		assertThatThrownBy(() -> userService.updateUserById(1, newUser.get()))
		.isInstanceOf(DuplicateEmailException.class)
		.hasMessageContaining("email " + newUser.get().getEmail() + " is already in use");

	}

	@Test
	@DisplayName("Given a user email, Then returns a user, When the emails match")
	void getUserByEmailBasic_test() {

		userService.createUser(userModel);
		when(repository.findByEmail("pepe@pepe.com")).thenReturn(userModel);

		UserEntity foundUser = userService.findUserByEmail("pepe@pepe.com");
		assertThat(foundUser).isEqualTo(userModel);
	}

	@Test
	@DisplayName("Given a user email, Then throws exception, When the emails are repeated")
	void getUserByEmailWithEmailNotFound_test() {

		when(repository.findByEmail("pepe@pepe.com")).thenReturn(null);

		assertThatThrownBy(() -> userService.findUserByEmail("pepe@pepe.com"))
		.isInstanceOf(ResponseStatusException.class)
		.hasMessageContaining("User with email " + "pepe@pepe.com" + " not found");

	}

	@Test
	void getUserPointsis0_test() throws Exception{
		
		String path = "http://localhost:8082/carts/user/" + 12;

		List<ProductEntity> products =  Arrays.asList(new ProductEntity(1, 2, "product", UUID.fromString("7e2bb8f9-6bbc-4bc4-915f-f72cb21b035f"), "patatas", BigDecimal.valueOf(10),2, BigDecimal.valueOf(20)));

		List<CartEntity> carts = new ArrayList<>();

		CartEntity cartEntity = new CartEntity();
		cartEntity.setId(UUID.fromString("7e2bb8f9-6bbc-4bc4-915f-f72cb21b035f"));
		cartEntity.setUserId(12);
		cartEntity.setCreatedAt(LocalDateTime.of(2022, 4, 11, 10, 30, 0));
		cartEntity.setUpdatedAt(LocalDateTime.of(2022, 4, 12, 10, 30, 0));
		cartEntity.setStatus("SUBMITTED");
		cartEntity.setProducts(products);

		carts.add(cartEntity);
	
		when(retrieveCartInformation.getCarts(anyInt())).thenReturn(carts);
		
		when(repository.findById(anyInt())).thenReturn(userModel2);
		
		when(mapper.toUserWithAvgSpentAndFidelityPoints(userModel, BigDecimal.valueOf(20), 1)).thenReturn(userEntityDTO);
		
		assertEquals(0, userService.getUserWithAvgSpentAndFidelityPoints(anyInt()).getPoints());


	}


	@Test
	void addFavoriteProduct_test() {

		FavoriteProduct favorite = new FavoriteProduct(1,5);

		userModel.addFavorite(favorite);

		when(repository.findById(anyInt())).thenReturn(Optional.of(userModel));

		when(favoriteRepository.save(any(FavoriteProduct.class))).thenReturn(favorite);

		UserEntity user = userService.addFavoriteProduct(1, 5);

		assertThat(user).isEqualTo(userModel);
		verify(favoriteRepository, atLeastOnce()).save(favorite);
		verify(repository, atLeastOnce()).findById(1);

	}

	@Test
	void addFavoriteProductWithNotExistingUser_test() {

		int userId = 600;
		when(repository.findById(anyInt())).thenReturn(Optional.empty());

		assertThatThrownBy(()-> userService.addFavoriteProduct(userId,5))
		.isInstanceOf(ResponseStatusException.class)
		.hasMessageContaining("User with id " + userId + " not found");
	}


	@Test
	void addFavoriteProductWithExistingFavorite_test() {

		int userId = 60;
		int productId = 50;

		when(repository.findById(anyInt())).thenReturn(Optional.of(userModel));

		when(favoriteRepository.save(any(FavoriteProduct.class)))
		.thenThrow(new DataIntegrityViolationException("error"));

		assertThatThrownBy(()-> userService.addFavoriteProduct(userId,productId))
		.isInstanceOf(DuplicateFavoriteException.class)
		.hasMessageContaining("Product with id " + productId + " is already favorite for user with id " + userId);

	}


}

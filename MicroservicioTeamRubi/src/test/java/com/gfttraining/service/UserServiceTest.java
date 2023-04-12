package com.gfttraining.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;


import org.hibernate.exception.ConstraintViolationException;


import java.util.Arrays;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import com.gfttraining.controller.UserController;
import com.gfttraining.entity.FavoriteProduct;
import com.gfttraining.entity.User;
import com.gfttraining.exception.DuplicateEmailException;
import com.gfttraining.exception.DuplicateFavoriteException;
import com.gfttraining.repository.FavoriteRepository;
import com.gfttraining.repository.UserRepository;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	@Autowired
	private UserService userService;

	@Mock
	private UserRepository repository;

	@Mock
	private FavoriteRepository favoriteRepository;

	@Test
	void getUserById_test(){
		int id=1;
		User userTest1= new User();
		userTest1.setId(1);
		userTest1.setName("Erna");

		when(repository.findById(1)).thenReturn(Optional.of(userTest1));

		User result = userService.findUserById(id);

		assertNotNull(result);
		assertEquals(userTest1.getName(), result.getName());

		verify(repository, times(1)).findById(1);

	}


	@Test
	void getUserByIdNotFound_test(){
		int id=1234;

		when(repository.findById(id)).thenReturn((Optional.empty()));

		EntityNotFoundException exception= 
				assertThrows(
						EntityNotFoundException.class, 
						() -> {userService.findUserById(id);});

		assertEquals("Usuario con el id: "+id+" no encontrado", exception.getMessage());

	}


	@Test
	void getAllUsersByName_test(){
		String name="Erna";
		List <User> userListTest1 = new ArrayList<>();
		User userTest1 = new User();
		userTest1.setId(1);
		userTest1.setName("Erna");
		userListTest1.add(userTest1);

		when(repository.findAllByName("Erna")).thenReturn((userListTest1));

		List<User> result = userService.findAllByName(name);

		assertNotNull(result);
		assertEquals(userListTest1.get(0).getName(), result.get(0).getName());

		verify(repository, times(1)).findAllByName(name);

	}

	@Test
	void getAllUsersByNameNotFound_test(){
		String name="Ernaaa";
		List <User> userListTest1 = new ArrayList<>();

		when(repository.findAllByName(name)).thenReturn((userListTest1));

		EntityNotFoundException exception= 
				assertThrows(
						EntityNotFoundException.class, 
						() -> {userService.findAllByName(name);});

		assertEquals("Usuario con el nombre: "+name+" no encontrado", exception.getMessage());

	}

	@Test
	void deleteUserById_test(){
		int id=1;
		userService.deleteUserById(id);

		verify(repository, times(1)).deleteById(1);
	}

	@Test
	void deleteUserByIdNotFound_test(){
		int id=1234;

		doThrow(EmptyResultDataAccessException.class)
		.when(repository).deleteById(id);

		EntityNotFoundException exception= 
				assertThrows(
						EntityNotFoundException.class, 
						() -> {userService.deleteUserById(id);});

		assertEquals("No se ha podido eliminar el usuario con el id: "+id+" de la base de datos", exception.getMessage());

	}

	@Test
	void createUser_test() {
		User user = new User("Pepito", "Perez", "calle falsa", "TRANSFERENCIA");
		when(repository.save(user)).thenReturn(user);
		User createduser = userService.createUser(user);
		assertThat(user).isEqualTo(createduser);
	}

	@Test
	void updateUserById_test() {

		User existingUser = new User("Pepito", "Perez", "calle falsa", "TRANSFERENCIA");
		existingUser.setId(1);

		User updatedUser = new User();
		updatedUser.setName("Jose");

		when(repository.findById(1)).thenReturn(Optional.of(existingUser));
		when(repository.save(existingUser)).thenReturn(existingUser);

		User result = userService.updateUserById(1, updatedUser);

		verify(repository, times(1)).findById(1);
		verify(repository, times(1)).save(existingUser);
		assertThat(updatedUser.getName()).isEqualTo(result.getName());

	}

	@Test
	void updateUserByIdNoValidId_test() {

		User existingUser = new User("Pepito", "Perez", "calle falsa", "TRANSFERENCIA");

		when(repository.findById(anyInt())).thenReturn(Optional.empty());

		assertThatThrownBy(()-> userService.updateUserById(anyInt(), existingUser))
		.isInstanceOf(ResponseStatusException.class)
		.hasMessageContaining("User not found");


	}

	@Test
	void updateUserByIdWithNullValues_test() {

		User existingUser = new User("Pepito", "Perez", "calle falsa", "TRANSFERENCIA");
		existingUser.setId(1);

		User updatedUser = new User();
		updatedUser.setName("Jose");

		when(repository.findById(1)).thenReturn(Optional.of(existingUser));
		when(repository.save(existingUser)).thenReturn(existingUser);

		User result = userService.updateUserById(1, updatedUser);

		assertThat(result.getLastname()).isNotEqualTo(null);

	}



	@Test
	void createUserWithEmailThatAlreadyExists_test() {

		User newUser = new User("repeatedemail@gmail.com","Pepito", "Perez", "calle falsa");

		when(repository.existsByEmail("repeatedemail@gmail.com")).thenReturn(true);

		assertThatThrownBy(()-> userService.createUser(newUser))
		.isInstanceOf(DuplicateEmailException.class)
		.hasMessageContaining("email " + newUser.getEmail() + " is already in use");

	}

	@Test
	void updateUserByIdWithEmailThatAlreadyExists_test() {

		Optional<User> newUser = Optional.of(new User("repeatedemail@gmail.com","Pepito", "Perez", "calle falsa"));

		when(repository.existsByEmail("repeatedemail@gmail.com")).thenReturn(true);
		when(repository.findById(1)).thenReturn(newUser);

		assertThatThrownBy(()-> userService.updateUserById(1, newUser.get()))
		.isInstanceOf(DuplicateEmailException.class)
		.hasMessageContaining("email " + newUser.get().getEmail() + " is already in use");

	}

	@Test
	void getUserByEmailBasic_test() {

		User existingUser = new User("pedro@chapo.com", "Pedro", "Chapo", "calle falsa");
		String email = "pedro@chapo.com"; 

		userService.createUser(existingUser);
		when(repository.findByEmail(email)).thenReturn(existingUser);

		User foundUser = userService.findUserByEmail(email);
		assertThat(foundUser).isEqualTo(existingUser);
	}

	@Test
	void getUserByEmailWithEmailNotFound_test() {

		String email = "pedro@chapo.com"; 

		when(repository.findByEmail(email)).thenReturn(null);

		assertThatThrownBy(()-> userService.findUserByEmail(email))
		.isInstanceOf(ResponseStatusException.class)
		.hasMessageContaining("User with email " + email + " not found");

	}

	@Test
	void addFavoriteProduct_test() {

		FavoriteProduct favorite = new FavoriteProduct(1,5);
		User existingUser = new User("pablo@chapo.com", "pablo","chapo","calle falsa");

		existingUser.addFavorite(favorite);

		when(repository.findById(anyInt())).thenReturn(Optional.of(existingUser));

		when(favoriteRepository.save(any(FavoriteProduct.class))).thenReturn(favorite);

		User user = userService.addFavoriteProduct(1, 5);

		assertThat(user).isEqualTo(existingUser);
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
		User existingUser = new User("pablo@chapo.com", "pablo","chapo","calle falsa");

		when(repository.findById(anyInt())).thenReturn(Optional.of(existingUser));

		when(favoriteRepository.save(any(FavoriteProduct.class)))
		.thenThrow(new DataIntegrityViolationException("error"));

		assertThatThrownBy(()-> userService.addFavoriteProduct(userId,productId))
		.isInstanceOf(DuplicateFavoriteException.class)
		.hasMessageContaining("Product with id " + productId + " is already favorite for user with id " + userId);

	}


}
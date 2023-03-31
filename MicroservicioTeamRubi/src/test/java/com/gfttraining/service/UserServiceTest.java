package com.gfttraining.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import org.hibernate.exception.ConstraintViolationException;

import java.util.Optional;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.gfttraining.repository.UserRepository;
import com.gfttraining.user.User;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository repository;


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
	void getUserByName_test(){
		String name="Erna";
		User userTest1= new User();
		userTest1.setId(1);
		userTest1.setName("Erna");

		when(repository.findByName("Erna")).thenReturn((userTest1));

		User result = userService.findUserByName("Erna");

		assertNotNull(result);
		assertEquals(userTest1.getName(), result.getName());

		verify(repository, times(1)).findByName("Erna");

	}


	@Test
	void deleteUserById_test(){
		int id=1;
		userService.deleteUserById(id);

		verify(repository, times(1)).deleteById(1);
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

}

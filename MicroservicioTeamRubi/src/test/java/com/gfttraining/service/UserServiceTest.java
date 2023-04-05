package com.gfttraining.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.server.ResponseStatusException;

import com.gfttraining.exception.DuplicateEmailException;
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

		User userTest1= new User();
		userTest1.setId(1);
		userTest1.setName("Erna");

		when(repository.findById(1)).thenReturn(Optional.of(userTest1));

		User result = userService.findUserById(1);

		assertNotNull(result);
		assertEquals(userTest1.getName(), result.getName());

		verify(repository, times(1)).findById(1);

	}


	@Test
	void getUserByIdNotFound_test(){
		
		when(repository.findById(1234)).thenReturn((Optional.empty()));
		
		EntityNotFoundException exception= 
				assertThrows(
						EntityNotFoundException.class, 
						() -> {userService.findUserById(1234);});

		assertEquals("Usuario con el id: "+1234+" no encontrado", exception.getMessage());

	}


	@Test
	void getAllUsersByName_test(){
		
		List <User> userListTest1 = new ArrayList<>();
		User userTest1 = new User();
		userTest1.setId(1);
		userTest1.setName("Erna");
		userListTest1.add(userTest1);

		when(repository.findAllByName("Erna")).thenReturn((userListTest1));

		List<User> result = userService.findAllByName("Erna");

		assertNotNull(result);
		assertEquals(userListTest1.get(0).getName(), result.get(0).getName());

		verify(repository, times(1)).findAllByName("Erna");

	}

	@Test
	void getAllUsersByNameNotFound_test(){
		List <User> userListTest1 = new ArrayList<>();
		
		when(repository.findAllByName("Ernaaa")).thenReturn((userListTest1));
		
		EntityNotFoundException exception= 
				assertThrows(
						EntityNotFoundException.class, 
						() -> {userService.findAllByName("Ernaaa");});

		assertEquals("Usuario con el nombre: "+"Ernaaa"+" no encontrado", exception.getMessage());

	}
	
	@Test
	void getAllUsers() {
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User("John", "Doe", null, null));
        expectedUsers.add(new User("Jane", "Doe", null, null));
        
        when(repository.findAll()).thenReturn(expectedUsers);

        UserService userService = new UserService(repository);

        List<User> actualUsers = userService.findAll();

        assertEquals(expectedUsers, actualUsers);
	}
	
    @Test
    void testSaveAllUsers() {

        List<User> usersList = new ArrayList<>();
        usersList.add(new User("John", "Doe", null, null));
        usersList.add(new User("Jane", "Doe", null, null));

        UserService userService = new UserService(repository);

        userService.saveAllUsers(usersList);

        verify(repository).saveAll(usersList);
        
    }
    
    @Test
    void testDeleteAllUsers() {
    	
        UserService userService = new UserService(repository);

        userService.deleteAllUsers();

        verify(repository).deleteAll();
        
    }

	@Test
	void deleteUserById_test(){

		userService.deleteUserById(1);

		verify(repository, times(1)).deleteById(1);
	}

	@Test
	void deleteUserByIdNotFound_test(){

		doThrow(EmptyResultDataAccessException.class)
		.when(repository).deleteById(1234);

		EntityNotFoundException exception= 
				assertThrows(
						EntityNotFoundException.class, 
						() -> {userService.deleteUserById(1234);});

		assertEquals("No se ha podido eliminar el usuario con el id: "+1234+" de la base de datos", exception.getMessage());

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

		User newUser = new User("repeatedemail@gmail.com","Pepito", "Perez", "calle falsa", "TRANSFERENCIA");

		when(repository.existsByEmail("repeatedemail@gmail.com")).thenReturn(true);

		assertThatThrownBy(()-> userService.createUser(newUser))
		.isInstanceOf(DuplicateEmailException.class)
		.hasMessageContaining("email " + newUser.getEmail() + " is already in use");

	}

	@Test
	void updateUserByIdWithEmailThatAlreadyExists_test() {

		Optional<User> newUser = Optional.of(new User("repeatedemail@gmail.com","Pepito", "Perez", "calle falsa", "TRANSFERENCIA"));

		when(repository.existsByEmail("repeatedemail@gmail.com")).thenReturn(true);
		when(repository.findById(1)).thenReturn(newUser);

		assertThatThrownBy(()-> userService.updateUserById(1, newUser.get()))
		.isInstanceOf(DuplicateEmailException.class)
		.hasMessageContaining("email " + newUser.get().getEmail() + " is already in use");

	}

	@Test
	void getUserByEmailBasic_test() {

		User existingUser = new User("pedro@chapo.com", "Pedro", "Chapo", "calle falsa", "VISA");

		userService.createUser(existingUser);
		when(repository.findByEmail("pedro@chapo.com")).thenReturn(existingUser);

		User foundUser = userService.findUserByEmail("pedro@chapo.com");
		assertThat(foundUser).isEqualTo(existingUser);
	}

	@Test
	void getUserByEmailWithEmailNotFound_test() {

		when(repository.findByEmail("pedro@chapo.com")).thenReturn(null);

		assertThatThrownBy(()-> userService.findUserByEmail("pedro@chapo.com"))
		.isInstanceOf(ResponseStatusException.class)
		.hasMessageContaining("User with email " + "pedro@chapo.com" + " not found");

	}

}

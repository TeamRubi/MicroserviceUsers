package com.gfttraining.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

<<<<<<< HEAD
import org.hibernate.exception.ConstraintViolationException;
=======
import java.util.Optional;

>>>>>>> ListUser
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.gfttraining.repository.UserRepository;
import com.gfttraining.user.User;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository repository;

<<<<<<< HEAD

=======
	
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
	
>>>>>>> ListUser
	@Test
	void deleteUserById_test(){
		int id=1;
		userService.deleteUserById(id);
<<<<<<< HEAD
		verify(repository, times(1)).deleteById(1);;
=======
		verify(repository, times(1)).deleteById(1);

>>>>>>> ListUser
	}

	@Test
	void createUser_test() {
		User user = new User("Pepito", "Perez", "calle falsa", "TRANSFERENCIA");
		when(repository.save(user)).thenReturn(user);
		User createduser = userService.createUser(user);
		assertThat(user).isEqualTo(createduser);
	}



}

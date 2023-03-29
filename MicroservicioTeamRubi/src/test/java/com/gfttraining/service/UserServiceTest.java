package com.gfttraining.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.gfttraining.customer.User;
import com.gfttraining.repository.UserRepository;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository repository;

	@Test
	void deleteUserById_test(){
		int id=1;
		userService.deleteUserById(id);
		verify(repository, times(1)).deleteById(1);;

	}

	@Test
	void createUser_test() {
		User user = new User("Pepito", "Perez", "calle falsa", "TRANSFERENCIA");
		when(repository.save(user)).thenReturn(user);
		User createduser = userService.createUser(user);
		assertEquals(user, createduser);
	}

}

package com.gfttraining.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gfttraining.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DeleteUserbyIdTest {
	
	@InjectMocks
	private UserService userService;
	
	@Mock
	private UserRepository userRepository;

	@Test
	void testDeleteId(){
		int id=1;
		userService.deleteUserById(id);
		verify(userRepository, times(1)).deleteById(1);;
		
	}
	
	
	
}

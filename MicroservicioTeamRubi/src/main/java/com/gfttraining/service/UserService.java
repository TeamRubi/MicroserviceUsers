package com.gfttraining.service;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gfttraining.repository.UserRepository;
import com.gfttraining.user.User;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public List<User> findAll(){
		return userRepository.findAll();
	}
	public User findUserById(Integer id){
		Optional<User> user = userRepository.findById(id);
		return user.get();
	}

	public User findUserByName(String name){
		Optional<User> user = Optional.ofNullable(userRepository.findByName(name));
		return user.get();
	}

	public void deleteUserById(Integer id) {
		try {
			userRepository.deleteById(id);
		} catch(Exception e) {
			e.getMessage();
		}
	}

	public User createUser(User user) {
		return userRepository.save(user);
	}


}
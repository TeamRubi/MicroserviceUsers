package com.gfttraining.service;
import java.util.List;
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
	
	public void saveUser(User user) {
		userRepository.save(user);
	}
	public void saveAllUsers(List<User> usersList) {
		userRepository.saveAll(usersList);
	}
	public void deleteAllUsers() {
		userRepository.deleteAll();
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
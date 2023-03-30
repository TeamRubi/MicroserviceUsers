package com.gfttraining.service;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

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
		if(user.isEmpty()) {
			throw new EntityNotFoundException("Usuario con el id: "+id+" no encontrado");
		}
		return user.get();
	}
	
	public User findUserByName(String name){
		Optional<User> user = Optional.ofNullable(userRepository.findByName(name));
		if(user.isEmpty()) {
			throw new EntityNotFoundException("Usuario con el nombre: "+name+" no encontrado");
		}
		return user.get();
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
			throw new EntityNotFoundException("No se ha podido eliminar el usuario con el id: "+id+" de la base de datos");
		}
	}

	public User createUser(User user) {

		return userRepository.save(user);
	}


}
package com.gfttraining.service;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;


import org.slf4j.LoggerFactory;
import org.modelmapper.Condition;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gfttraining.UserMicroserviceApplication;
import com.gfttraining.repository.UserRepository;
import com.gfttraining.user.User;

import ch.qos.logback.classic.Logger;

@Service
public class UserService {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(UserMicroserviceApplication.class);


	private UserRepository userRepository;

	private ModelMapper modelMapper;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
		this.modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
	}

	public List<User> findAll(){
		LOGGER.info("Findig all users");
		return userRepository.findAll();
	}

	public User findUserById(Integer id){
		Optional<User> user = userRepository.findById(id);
		if(user.isEmpty()) {
			LOGGER.error("findUserById() -> no such user with the ID: " + id);
			throw new EntityNotFoundException("Usuario con el id: "+id+" no encontrado");
		}
		LOGGER.info("Found user by ID");
		return user.get();
	}

	public User findUserByName(String name){
		Optional<User> user = Optional.ofNullable(userRepository.findByName(name));
		if(user.isEmpty()) {
			LOGGER.error("findUserByName() -> no such user with the name: " + name);
			throw new EntityNotFoundException("Usuario con el nombre: "+name+" no encontrado");
		}
		LOGGER.info("Found user by Name");
		return user.get();

	}

	public void saveUser(User user) {
		userRepository.save(user);
		LOGGER.info("Saved user to DB");
	}

	public void saveAllUsers(List<User> usersList) {
		userRepository.saveAll(usersList);
		LOGGER.info("Saved all users to DB");
	}

	public void deleteAllUsers() {
		userRepository.deleteAll();
		LOGGER.info("Deleted all users");
	}

	public void deleteUserById(Integer id) {
		try {
			userRepository.deleteById(id);
			LOGGER.info("Deleted user by ID");
		} catch(Exception e) {
			LOGGER.error("deleteUserById() -> coud not delete user with the ID: " + id);
			throw new EntityNotFoundException("No se ha podido eliminar el usuario con el id: "+id+" de la base de datos");
		}
	}

	public User createUser(User user) {
		return userRepository.save(user);
	}

	public User updateUserById(int id, User user) {

		User existingUser = userRepository.findById(id)
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		user.setId(existingUser.getId());
		modelMapper.map(user, existingUser);

		return userRepository.save(existingUser);

	}


}
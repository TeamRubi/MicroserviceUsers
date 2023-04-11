package com.gfttraining.service;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gfttraining.Entity.UserEntity;
import com.gfttraining.exception.DuplicateEmailException;
import com.gfttraining.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	private UserRepository userRepository;

	private ModelMapper modelMapper;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
		this.modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
	}

	public List<UserEntity> findAll(){
		log.info("Findig all users");
		return userRepository.findAll();
	}

	public UserEntity findUserById(Integer id){
		Optional<UserEntity> user = userRepository.findById(id);
		if(user.isEmpty()) {
			log.error("findUserById() -> no such user with the ID: " + id);
			throw new EntityNotFoundException("Usuario con el id: "+id+" no encontrado");
		}
		log.info("Found user by ID");
		return user.get();
	}


	public List<UserEntity> findAllByName(String name){
		List<UserEntity> users = userRepository.findAllByName(name);
		if(users.isEmpty()) {
			log.error("findUserByName() -> no such user with the name: " + name);
			throw new EntityNotFoundException("Usuario con el nombre: "+name+" no encontrado");
		}
		log.info("Found user by Name");
		return users;


	}

	public void saveAllUsers(List<UserEntity> usersList) {
		userRepository.saveAll(usersList);
		log.info("Saved all users to DB");
	}

	public void deleteAllUsers() {
		userRepository.deleteAll();
		log.info("Deleted all users");
	}

	public void deleteUserById(Integer id) {
		try {
			userRepository.deleteById(id);
			log.info("Deleted user by ID");
		} catch(Exception e) {
			log.error("deleteUserById() -> coud not delete user with the ID: " + id);
			throw new EntityNotFoundException("No se ha podido eliminar el usuario con el id: "+id+" de la base de datos");
		}
	}

	public UserEntity createUser(UserEntity user) {

		String email = user.getEmail();

		if(userRepository.existsByEmail(email)) {
			throw new DuplicateEmailException("The email " + email + " is already in use");
		}

		log.info("user " + user.getName() + " created");

		return userRepository.save(user);

	}

	public UserEntity updateUserById(int id, UserEntity user) {

		UserEntity existingUser = userRepository.findById(id)
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		if(userRepository.existsByEmail(user.getEmail())) {
			throw new DuplicateEmailException("The email " + user.getEmail() + " is already in use");
		}

		user.setId(existingUser.getId());
		modelMapper.map(user, existingUser);

		log.info("Updated user with id " + id);

		return userRepository.save(existingUser);

	}

	public UserEntity findUserByEmail(String email){

		Optional<UserEntity> user = Optional.ofNullable(userRepository.findByEmail(email));

		if(user.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with email " + email + " not found");
		}

		log.info("Found user with email " + email);

		return user.get();

	}



}
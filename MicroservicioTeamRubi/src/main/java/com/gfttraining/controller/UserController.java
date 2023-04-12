package com.gfttraining.controller;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfttraining.DTO.UserEntityDTO;
import com.gfttraining.Entity.UserEntity;
import com.gfttraining.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/users")
	public List<UserEntity> getAllUsers(){
		return userService.findAll();
	}

	@GetMapping("/users/name/{name}")
	public List<UserEntity> GetUserById(@PathVariable String name){
		return userService.findAllByName(name);
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUserById(@PathVariable int id){
		userService.deleteUserById(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/users")
	public ResponseEntity<UserEntity> createUser(@Valid @RequestBody UserEntity user) {
		return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
	}

	@PostMapping("/users/import")
	public ResponseEntity<Void> saveAllImportedUsers(@RequestParam("file") MultipartFile file) {
		try {
			deleteAllUsers();
			ObjectMapper objectMapper = new ObjectMapper();
			List<UserEntity> users = objectMapper.readValue(file.getBytes(), new TypeReference<List<UserEntity>>(){});
			userService.saveAllUsers(users);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void deleteAllUsers() {
		userService.deleteAllUsers();
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<UserEntity> updateUserById(@PathVariable int id, @RequestBody UserEntity user) {

		UserEntity updatedUser = userService.updateUserById(id,user);
		return new ResponseEntity<UserEntity>(updatedUser, HttpStatus.CREATED);
	}

	@GetMapping("/users/email/{email}")
	public ResponseEntity<UserEntity> getUserByEmail(@PathVariable String email){

		return new ResponseEntity<UserEntity>(userService.findUserByEmail(email), HttpStatus.OK);
	}
	
	@GetMapping("/users/{id}")
	public UserEntityDTO getUserWithAvgSpentAndFidelityPoints(@PathVariable int id) {
		return userService.getUserWithAvgSpentAndFidelityPoints(id);
	}
	
	public UserEntity getUserById(int id) {
		return userService.findUserById(id);
	}
	
}
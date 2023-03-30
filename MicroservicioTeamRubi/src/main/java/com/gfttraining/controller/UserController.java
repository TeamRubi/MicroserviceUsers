package com.gfttraining.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfttraining.service.UserService;
import com.gfttraining.user.User;
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public List<User> getAllUsers(){
		return userService.findAll();
	}
	
	@GetMapping("/users/{id}")
	public User GetUserById(@PathVariable int id){
		return userService.findUserById(id);
	}
	
	@GetMapping("/users/name/{name}")
	public User GetUserById(@PathVariable String name){
		return userService.findUserByName(name);
	}
	

	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUserById(@PathVariable int id){
		userService.deleteUserById(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/users")
	public User createUser(@RequestBody User user) {
		return userService.createUser(user);
	}
	
	@PostMapping("/users/import")
	public ResponseEntity<Void> saveAllImportedUsers(@RequestParam("file") MultipartFile file) {
	try {
	deleteAllUsers();
	ObjectMapper objectMapper = new ObjectMapper();
	List<User> users = objectMapper.readValue(file.getBytes(), new TypeReference<List<User>>(){});
	userService.saveAllUsers(users);
	return new ResponseEntity<>(HttpStatus.OK);
	} catch (Exception e) {
	e.printStackTrace();
	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	}

	public void deleteAllUsers() {
	userService.deleteAllUsers();
	}
	
}
package com.gfttraining.controller;
import java.io.IOException;
import java.net.HttpURLConnection;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfttraining.entity.User;
import com.gfttraining.exception.ExceptionResponse;
import com.gfttraining.repository.FavoriteRepository;
import com.gfttraining.repository.UserRepository;
import com.gfttraining.service.UserService;

@RestController
@Validated
public class UserController {

	private UserService userService;

	private RestTemplate restTemplate;

	public UserController(UserService userService, RestTemplate restTemplate) {
		this.userService = userService;
		this.restTemplate = restTemplate;
	}

	@GetMapping("/users")
	public List<User> getAllUsers(){
		return userService.findAll();
	}

	@GetMapping("/users/{id}")
	public User GetUserById(@PathVariable int id){
		return userService.findUserById(id);
	}

	@GetMapping("/users/name/{name}")
	public List<User> GetUserById(@PathVariable String name){
		return userService.findAllByName(name);
	}


	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUserById(@PathVariable int id){
		userService.deleteUserById(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
	}

	@PostMapping("/users/import")
	public ResponseEntity<Void> saveAllImportedUsers(@RequestParam("file") MultipartFile file) {
		try {
			deleteAllUsers();
			ObjectMapper objectMapper = new ObjectMapper();
			List<User> users = objectMapper.readValue(file.getBytes(), new TypeReference<List<User>>(){});
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


	@PatchMapping("/users/{id}")
	public ResponseEntity<User> updateUserById(@PathVariable int id, @RequestBody User user) {

		User updatedUser = userService.updateUserById(id,user);
		return new ResponseEntity<User>(updatedUser, HttpStatus.CREATED);
	}

	@GetMapping("/users/email/{email}")
	public ResponseEntity<User> getUserByEmail(@PathVariable String email){

		return new ResponseEntity<User>(userService.findUserByEmail(email), HttpStatus.OK);
	}

	@PostMapping("/users/{userId}/{productId}")
	public ResponseEntity<User> addFavoriteProduct(@PathVariable int userId, @PathVariable int productId) throws Exception  {

		if(productExists(productId)) {
			User updatedUser = userService.addFavoriteProduct(userId, productId);
			return new ResponseEntity<User>(updatedUser, HttpStatus.CREATED);
		}
		else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "product with id " + productId + " not found");
		}
	}


	private boolean productExists(int productId) {

		try {
			ResponseEntity<String> responseEntity = restTemplate.getForEntity(
					"http://localhost:8081/products/id/" + productId, String.class);
			return responseEntity.getStatusCode() == HttpStatus.OK;
		}
		catch(HttpClientErrorException.NotFound ex) {
			return false;
		}

	}



}
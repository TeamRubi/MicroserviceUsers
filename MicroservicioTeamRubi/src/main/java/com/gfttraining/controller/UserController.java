package com.gfttraining.controller;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.gfttraining.config.FeatureFlag;
import com.gfttraining.connection.RetrieveInformationFromExternalMicroservice;
import com.gfttraining.dto.UserEntityDTO;
import com.gfttraining.entity.CartEntity;
import com.gfttraining.entity.ProductEntity;
import com.gfttraining.entity.UserEntity;
import com.gfttraining.service.UserService;

@RestController
public class UserController {

	private UserService userService;

	private FeatureFlag featureFlag;

	private RetrieveInformationFromExternalMicroservice retrieveInfo;

	public UserController(UserService userService, FeatureFlag featureFlag, RetrieveInformationFromExternalMicroservice retrieveInfo) {
		this.userService = userService;
		this.featureFlag = featureFlag;
		this.retrieveInfo = retrieveInfo;
	}

	@GetMapping("/users")
	public List<UserEntity> getAllUsers(){
		return userService.findAll();
	}

	@GetMapping("/users/name/{name}")
	public List<UserEntity> getUserByName(@PathVariable String name) {
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
		return userService.saveAllImportedUsers(file);
	}

	public void deleteAllUsers() {
		userService.deleteAllUsers();
	}

	@PatchMapping("/users/{id}")
	public ResponseEntity<UserEntity> updateUserById(@PathVariable int id, @RequestBody UserEntity user) {
		return new ResponseEntity<>(userService.updateUserById(id,user), HttpStatus.CREATED);
	}

	@GetMapping("/users/email/{email}")
	public ResponseEntity<UserEntity> getUserByEmail(@PathVariable String email){

		return new ResponseEntity<>(userService.findUserByEmail(email), HttpStatus.OK);
	}

	@GetMapping("/users/{id}")
	public Object getUserById(@PathVariable int id) throws InterruptedException {
		if(featureFlag.isEnableUserExtraInfo()) {
			return userService.getUserWithAvgSpentAndFidelityPoints(id);
		}
		else {
			return userService.findUserById(id);

		}
	}

	@PostMapping("/favorite/{userId}/{productId}")
	public ResponseEntity<UserEntity> addFavoriteProduct(@PathVariable int userId, @PathVariable int productId) throws Exception  {

		if(productExists(productId)) {
			return new ResponseEntity<>(userService.addFavoriteProduct(userId, productId), HttpStatus.CREATED);
		}
		else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "product with id " + productId + " not found");
		}
	}

	@DeleteMapping("/favorite/{userId}/{productId}")
	public ResponseEntity<Void> deleteFavoriteProduct(@PathVariable int userId, @PathVariable int productId) throws Exception  {

		userService.deleteFavoriteProduct(userId, productId);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);

	}

	@DeleteMapping("/favorite/product/{productId}")
	public ResponseEntity<Void> deleteFavoriteProductFromAllUsers(@PathVariable int productId) throws Exception  {

		userService.deleteFavoriteProductFromAllUsers(productId);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}


	private boolean productExists(int productId) throws InterruptedException {
		try {
			retrieveInfo.getExternalInformation("http://localhost:8081/products/id/" + productId, new ParameterizedTypeReference<String>() {});
			return true;
		}
		catch(HttpClientErrorException.NotFound ex) {
			return false;
		}
	}

}
package com.gfttraining.service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfttraining.config.FeatureFlag;
import com.gfttraining.connection.RetrieveInfoFromExternalMicroservice;
import com.gfttraining.dto.UserEntityDTO;
import com.gfttraining.entity.CartEntity;
import com.gfttraining.entity.FavoriteProduct;
import com.gfttraining.entity.ProductEntity;
import com.gfttraining.entity.UserEntity;
import com.gfttraining.exception.DuplicateEmailException;
import com.gfttraining.exception.DuplicateFavoriteException;
import com.gfttraining.repository.FavoriteRepository;
import com.gfttraining.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	private UserRepository userRepository;

	private FavoriteRepository favoriteRepository;

	private ModelMapper modelMapper;

	private RetrieveInfoFromExternalMicroservice retrieveInformationFromExternalMicroservice;

	private FeatureFlag featureFlag;

	public UserService(UserRepository userRepository, FavoriteRepository favoriteRepository, ModelMapper modelMapper, 
			RetrieveInfoFromExternalMicroservice retrieveInformationFromExternalMicroservice, FeatureFlag featureFlag) {
		this.userRepository = userRepository;
		this.favoriteRepository = favoriteRepository;
		this.modelMapper = modelMapper;
		this.retrieveInformationFromExternalMicroservice = retrieveInformationFromExternalMicroservice;
		this.featureFlag = featureFlag;
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

	public UserEntityDTO getUserWithAvgSpentAndFidelityPoints(int id){

		List<CartEntity> carts = retrieveInformationFromExternalMicroservice.getExternalInformation("http://localhost:8082/carts/user/" + id,
				new ParameterizedTypeReference<List<CartEntity>>() {});

		UserEntityDTO userDTO = new UserEntityDTO();
		modelMapper.map(findUserById(id), userDTO);

		userDTO.setAverageSpent(calculateAvgSpent(carts));
		userDTO.setPoints(getPoints(carts));
		log.info("Returning a UserEntityDTO with fidelityPoints and avgSpent");
		return userDTO;
	}

	public BigDecimal calculateAvgSpent(List<CartEntity> carts) {

		BigDecimal totalSpent = BigDecimal.ZERO;
		int itemsBought = 0;

		for (CartEntity cartEntity : carts) {
			List<ProductEntity> products = cartEntity.getProducts();
			for (ProductEntity productEntity : products) {
				totalSpent = totalSpent.add(productEntity.getTotalPrize());
				itemsBought++;
			}
		}
		if (totalSpent != BigDecimal.valueOf(0)) {
			return totalSpent.divide(BigDecimal.valueOf(itemsBought));
		}
		return BigDecimal.valueOf(0);
	}

	public Integer getPoints(List<CartEntity> carts) {
		int points = 0;
		for (CartEntity cartEntity : carts) {
			for (ProductEntity productEntity : cartEntity.getProducts()) {
				BigDecimal sumSpent = productEntity.getTotalPrize();
				int basePoints = 0;
				if (sumSpent.compareTo(new BigDecimal("20")) >= 0 && sumSpent.compareTo(new BigDecimal("29.99")) <= 0) {
					basePoints +=1;
				}
				else if (sumSpent.compareTo(new BigDecimal("30")) >= 0 && sumSpent.compareTo(new BigDecimal("49.99")) <= 0) {
					basePoints +=3;
				}
				else if (sumSpent.compareTo(new BigDecimal("50")) >= 0 && sumSpent.compareTo(new BigDecimal("99.99")) <= 0) {
					basePoints +=5;    
				}
				else if (sumSpent.compareTo(new BigDecimal("100")) >= 0 ) {
					basePoints +=10;
				}

				if(featureFlag.isEnablePromotion()) {
					basePoints *= 2;
				}

				points += basePoints;
			}
		}
		return points;
	}

	public UserEntity addFavoriteProduct(int userId, int productId) {

		UserEntity existingUser = userRepository.findById(userId)
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId + " not found"));

		FavoriteProduct favorite = new FavoriteProduct(userId, productId);

		if(!favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
			favoriteRepository.save(favorite);
			log.info("Favorite product saved on database");
		}
		else {
			throw new DuplicateFavoriteException("Product with id " + productId + " is already favorite for user with id " + userId);
		}

		return existingUser;
	}

	@Transactional
	public void deleteFavoriteProduct(int userId, int productId) {

		if(favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
			favoriteRepository.deleteByUserIdAndProductId(userId, productId);
			log.info("Favorite product deleted on database");
		}
		else {
			throw new EmptyResultDataAccessException("User with id " + userId + " does not have product with id " + productId + " as favorite", 1);
		}
	}

	@Transactional
	public void deleteFavoriteProductFromAllUsers(int productId) {

		if(favoriteRepository.existsByProductId(productId)) {
			favoriteRepository.deleteByProductId(productId);
			log.info("Favorites of that product deleted on database");
		}
		else {
			throw new EmptyResultDataAccessException("Product " + productId + " is not in the favorites of any user", 1);
		}
	}

	public ResponseEntity<Void> saveAllImportedUsers(MultipartFile file) {
		try {
			userRepository.deleteAll();
			log.info("Deleted all users");
			ObjectMapper objectMapper = new ObjectMapper();
			List<UserEntity> users = objectMapper.readValue(file.getBytes(), new TypeReference<List<UserEntity>>(){});
			userRepository.saveAll(users);
			log.info("Saved all users to DB");
			log.info("Users saved on database by file");
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception e) {
			log.error("Error saving users to database by file");
			throw new RuntimeException("There has been an error saving users to database by file");
		}

	}


}
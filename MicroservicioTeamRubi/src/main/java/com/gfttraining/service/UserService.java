package com.gfttraining.service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gfttraining.DTO.Mapper;
import com.gfttraining.DTO.UserEntityDTO;
import com.gfttraining.connection.RetrieveCartInformation;
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

	@Autowired
	private Mapper mapper;

	@Autowired
	public UserService(UserRepository userRepository, FavoriteRepository favoriteRepository, ModelMapper modelMapper) {
		this.userRepository = userRepository;
		this.favoriteRepository = favoriteRepository;
		this.modelMapper = modelMapper;
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

		System.out.println(existingUser);
		System.out.println(user);

		System.out.println(modelMapper);

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

		List<CartEntity> carts = RetrieveCartInformation.getCarts(id);

		return mapper.toUserWithAvgSpentAndFidelityPoints(findUserById(id), calculateAvgSpent(carts), getPoints(carts));
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
		if (!carts.isEmpty()) {
			for (CartEntity cartEntity : carts) {
				List<ProductEntity> products = cartEntity.getProducts();
				for (ProductEntity productEntity : products) {
					BigDecimal sumSpent = productEntity.getTotalPrize();
					if (sumSpent.compareTo(new BigDecimal("20")) >= 0 && sumSpent.compareTo(new BigDecimal("29.99")) <= 0) {
						points +=1;
					}
					else if (sumSpent.compareTo(new BigDecimal("30")) >= 0 && sumSpent.compareTo(new BigDecimal("49.99")) <= 0) {
						points +=3;
					}
					else if (sumSpent.compareTo(new BigDecimal("50")) >= 0 && sumSpent.compareTo(new BigDecimal("99.99")) <= 0) {
						points +=5;    
					}
					else if (sumSpent.compareTo(new BigDecimal("100")) >= 0 ) {
						points +=10;
					}
				}

			}
		}
		return points;
	}



	public UserEntity addFavoriteProduct(int userId, int productId) {

		UserEntity existingUser = userRepository.findById(userId)
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId + " not found"));

		FavoriteProduct favorite = new FavoriteProduct(userId, productId);

		try {
			favoriteRepository.save(favorite);
		} catch(DataIntegrityViolationException ex) {
			throw new DuplicateFavoriteException("Product with id " + productId + " is already favorite for user with id " + userId);
		}

		return existingUser;
	}



}
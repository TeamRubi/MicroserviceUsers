package com.gfttraining.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gfttraining.user.User;

public interface UserRepository extends JpaRepository<User, Integer>{

	User findByName(String name);

	User findByEmail(String email);

	boolean existsByEmail(String email);

	List<User> findAllByName(String name);

}
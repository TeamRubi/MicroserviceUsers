package com.gfttraining.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gfttraining.user.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	User findByName(String name);

	boolean existsByEmail(String email);
}
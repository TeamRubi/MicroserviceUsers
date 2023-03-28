package com.gfttraining.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gfttraining.customer.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	
}
package com.gfttraining.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gfttraining.Entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer>{

	UserEntity findByName(String name);

	UserEntity findByEmail(String email);

	boolean existsByEmail(String email);

	List<UserEntity> findAllByName(String name);

}
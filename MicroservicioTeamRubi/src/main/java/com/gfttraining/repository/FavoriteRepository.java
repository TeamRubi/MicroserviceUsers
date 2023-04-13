package com.gfttraining.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gfttraining.entity.FavoriteProduct;

public interface FavoriteRepository extends JpaRepository<FavoriteProduct, Integer> {

	List<FavoriteProduct> findByUserId(Integer userId);

	boolean existsByUserIdAndProductId(Integer userId, Integer productId);

}

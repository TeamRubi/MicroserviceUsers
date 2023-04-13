package com.gfttraining.DTO;

import java.math.BigDecimal;
import java.util.Set;

import com.gfttraining.entity.FavoriteProduct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class UserEntityDTO {
	
	private int id;
	@NonNull
	private String email;
	@NonNull
	private String name;
	@NonNull
	private String lastname;
	@NonNull
	private String address;
	@NonNull
	private String country;
	private String paymentmethod;
	private BigDecimal averageSpent;
	private int points;
	private Set<FavoriteProduct> favorites;

}

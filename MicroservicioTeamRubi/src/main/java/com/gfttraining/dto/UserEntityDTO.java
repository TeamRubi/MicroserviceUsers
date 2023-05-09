package com.gfttraining.dto;

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

	private Long id;
	@NonNull
	private String email;
	@NonNull
	private String name;
	@NonNull
	private String lastName;
	@NonNull
	private String address;
	@NonNull
	private String country;
	private String paymentMethod;
	private BigDecimal averageSpent;
	private Integer points;
	private Set<FavoriteProduct> favorites;

}

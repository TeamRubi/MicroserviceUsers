package com.gfttraining.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntityDTO {
	
	private int id;
	private String email;
	private String name;
	private String lastname;
	private String address;
	private String country;
	private String paymentmethod;
	private BigDecimal averageSpent;
	private int points;

}

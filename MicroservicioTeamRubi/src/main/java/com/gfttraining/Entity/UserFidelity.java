package com.gfttraining.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserFidelity {
	
	private UserEntity user;
	private int points;

}

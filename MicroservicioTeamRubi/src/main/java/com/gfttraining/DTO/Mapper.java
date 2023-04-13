package com.gfttraining.DTO;

import java.math.BigDecimal;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.gfttraining.entity.UserEntity;

@Component
public class Mapper {

	private ModelMapper mapper;

	public Mapper() {
		this.mapper = new ModelMapper();
	}

	public UserEntityDTO toUserWithAvgSpentAndFidelityPoints(UserEntity user, BigDecimal spent, int points) {
		UserEntityDTO userDTO = mapper.map(user, UserEntityDTO.class);
		userDTO.setAverageSpent(spent);
		userDTO.setPoints(points);
		return userDTO;
	}

}

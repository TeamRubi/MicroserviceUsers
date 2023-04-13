package com.gfttraining.DTO;

import java.math.BigDecimal;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.gfttraining.entity.UserEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		log.info("Returning a UserEntityDTO with fidelityPoints and avgSpent");
		return userDTO;
	}

}

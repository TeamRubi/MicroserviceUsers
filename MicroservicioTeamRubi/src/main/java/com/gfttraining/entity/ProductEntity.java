package com.gfttraining.entity;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductEntity {

	private int id;
	private int catalogId;
	private String name;
	private UUID cartId;
	private String description;
	private BigDecimal price;
	private int quantity;
	private BigDecimal totalPrize;

}

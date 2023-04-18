package com.gfttraining.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CartEntity {

	@Builder.Default UUID id = UUID.randomUUID();
	private int userId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String status;
	private List<ProductEntity> products;

}

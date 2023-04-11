package com.gfttraining.Entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartEntity {
	
	private UUID id;
	private int userId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String status;
	private List<ProductEntity> products;

}

package com.gfttraining.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull(message = "name cannot be null")
	private String name;

	@NotNull(message = "lastname cannot be null")
	private String lastname;

	@NotNull(message = "address cannot be null")
	private String address;

	private String paymentmethod;

	public User(String name, String lastname, String address, String paymentmethod) {
		this.name = name;
		this.lastname = lastname;
		this.address = address;
		this.paymentmethod = paymentmethod;
	}

	public User() {}


}

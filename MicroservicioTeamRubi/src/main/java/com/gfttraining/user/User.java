package com.gfttraining.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Check;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name="user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;


	@Column(unique=true)
	@Email(message="please provide a valid email")
	@NotNull(message = "email cannot be null")
	private String email;

	@NotNull(message = "name cannot be null")
	private String name;

	@NotNull(message = "lastname cannot be null")
	private String lastname;

	@NotNull(message = "address cannot be null")
	private String address;

	private String paymentmethod;

	/*
	@Nullable
	private int productId;*/



	public User(String name, String lastname, String address, String paymentmethod) {
		this.name = name;
		this.lastname = lastname;
		this.address = address;
		this.paymentmethod = paymentmethod;
	}

	public User(String email, String name, String lastname, String address, String paymentmethod) {
		this.email = email;
		this.name = name;
		this.lastname = lastname;
		this.address = address;
		this.paymentmethod = paymentmethod;
	}


}

package com.gfttraining.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name="user")
public class UserEntity {

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
	
	@NotNull(message = "country cannot be null")
	private String country;

	private String paymentmethod;

	public UserEntity(String email,String name,String lastname,String address, String country, String paymentmethod) {
		this.email = email;
		this.name = name;
		this.lastname = lastname;
		this.address = address;
		this.country = country;
		this.paymentmethod = paymentmethod;
	}



}

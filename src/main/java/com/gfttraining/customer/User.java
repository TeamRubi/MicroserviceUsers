package com.gfttraining.customer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class User {

	@Id()
	@GeneratedValue()
	private int id;

	private String name;
	private String lastname;
	private String address;
	private String paymentmethod;

	public User(int id, String name, String lastname, String address, String paymentmethod) {
		this.id = id;
		this.name = name;
		this.lastname = lastname;
		this.address = address;
		this.paymentmethod = paymentmethod;
	}

	public User() {}


}

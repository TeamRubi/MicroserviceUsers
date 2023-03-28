package com.gfttraining.customer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPaymentmethod() {
		return paymentmethod;
	}

	public void setPaymentmethod(String paymentmethod) {
		this.paymentmethod = paymentmethod;
	}
	
	

}

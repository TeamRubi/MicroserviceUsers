package com.gfttraining.entity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Check;

import org.springframework.lang.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Data
@Entity
@Table(name="user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true)
	@Email(message = "please provide a valid email")
	@NotNull(message = "email cannot be null")
	@NonNull
	private String email;

	@NotNull(message = "name cannot be null")
	@NonNull
	private String name;

	@NotNull(message = "lastname cannot be null")
	@NonNull
	private String lastname;

	@NotNull(message = "address cannot be null")
	@NonNull
	private String address;

	@Nullable
	private String paymentmethod;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private Set<FavoriteProduct> favorites = new LinkedHashSet<FavoriteProduct>();


	public void addFavorite(FavoriteProduct product) {
		this.favorites.add(product);
	}



}

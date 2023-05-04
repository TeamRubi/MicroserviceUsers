package com.gfttraining.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.Nullable;


@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="user")
public class UserEntity {

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
	@Column(name = "lastname")
	private String lastName;

	@NotNull(message = "address cannot be null")
	@NonNull
	private String address;

	@NotNull(message = "country cannot be null")
	@NonNull
	private String country;

	@Nullable
	@Column(name = "paymentmethod")
	private String paymentMethod;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", referencedColumnName = "id")

	private Set<FavoriteProduct> favorites = new LinkedHashSet<>();


	public void addFavorite(FavoriteProduct product) {
		this.favorites.add(product);
	}



}

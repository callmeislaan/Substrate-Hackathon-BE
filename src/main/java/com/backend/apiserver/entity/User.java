package com.backend.apiserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = "username"),
				@UniqueConstraint(columnNames = "email")
		})
public class User extends BaseEntity {
	private String email;

	private String username;

	private String password;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToOne(
			mappedBy = "user",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE},
			optional = false
	)
	private Mentor mentor;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToOne(
			mappedBy = "user",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			optional = false
	)
	private UserDetail userDetail;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JoinColumn(name = "role_id")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Role role;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	private Set<MoneyExchangeHistory> moneyExchangeHistories = new HashSet<>();

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	private Set<Comment> comments = new HashSet<>();

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	private Set<MoneyInHistory> moneyInHistories = new HashSet<>();

	@Enumerated(EnumType.STRING)
	private Status status;
}


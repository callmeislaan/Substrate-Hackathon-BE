package com.backend.apiserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
@Entity
@Table(name = "mentor_requests",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"mentor_id", "request_id"}),
		}
)
public class MentorRequest extends BaseEntity {

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JoinColumn(name = "mentor_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Mentor mentor;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JoinColumn(name = "request_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Request request;

	@Enumerated(EnumType.STRING)
	private Status status;
}

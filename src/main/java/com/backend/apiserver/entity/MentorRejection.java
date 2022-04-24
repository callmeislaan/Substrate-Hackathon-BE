package com.backend.apiserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "mentor_rejections")
public class MentorRejection extends BaseEntity {

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
}

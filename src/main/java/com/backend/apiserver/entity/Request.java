package com.backend.apiserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "requests")
public class Request extends BaseEntity {

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JoinColumn(name = "user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	private LocalDateTime deadline;

	private String title;

	@Column(columnDefinition="TEXT")
	private String content;

	private int price;

	@Column(name = "start_doing_time")
	private LocalDateTime startDoingTime;

	@Column(name = "complete_time")
	private LocalDateTime completeTime;

	@Enumerated(EnumType.STRING)
	private Status status;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToMany
	@JoinTable(name = "request_skills",
			joinColumns = { @JoinColumn(name = "request_id") },
			inverseJoinColumns = { @JoinColumn(name = "skill_id") })
	private Set<Skill> skills = new HashSet<>();

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "request", fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	private Set<MentorRequest> mentorRequests = new HashSet<>();

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "request", fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	private Set<RequestAnnouncement> requestAnnouncements = new HashSet<>();

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "request", fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	private Set<RequestFollowing> requestFollowings = new HashSet<>();
}

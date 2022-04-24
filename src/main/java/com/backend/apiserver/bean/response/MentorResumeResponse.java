package com.backend.apiserver.bean.response;

import lombok.Data;

import java.util.List;

@Data
public class MentorResumeResponse {
	private String fullName;
	private String username;
	private String avatar;
	private long createdDate;
	private int price;
	private boolean isAnestMentor;
	private String job;
	private float averageRating;
	private int totalRating1;
	private int totalRating2;
	private int totalRating3;
	private int totalRating4;
	private int totalRating5;
	private int totalRequestReceive;
	private int totalHoursBeHired;
	private int totalRequestFinish;
	private String introduction;
	private String skillDescription;
	private List<MentorSkillResponse> mentorSkillResponses;
	private String service;
	private boolean isFollowing;
	private boolean gender;
}

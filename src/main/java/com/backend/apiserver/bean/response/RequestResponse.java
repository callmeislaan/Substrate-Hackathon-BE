package com.backend.apiserver.bean.response;

import lombok.Data;

import java.util.List;

@Data
public class RequestResponse {
	private Long id;
	private long deadline;
	private String title;
	private String content;
	private int price;
	private String status;
	private List<SkillResponse> skills;
	private UserProfileResponse mentorInfoResponse;
	private UserProfileResponse userInfoResponse;
	private long createdDate;
	private boolean isBookmarked;
	private boolean isReserved;
	private String confirmStatus;
	private long startDoingTime;
}

package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class MentorHiringResponse {
	private Long id;
	private UserInfoResponse mentorInfoResponse;
	private UserInfoResponse userInfoResponse;
	private String title;
	private int hours;
	private int price;
	private String note;
	private String Status;
}

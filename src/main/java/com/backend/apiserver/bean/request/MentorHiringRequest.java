package com.backend.apiserver.bean.request;

import lombok.Data;

@Data
public class MentorHiringRequest {
	private Long mentorId;
	private int hours;
	private String note;
	private String title;
}

package com.backend.apiserver.bean.request;

import lombok.Data;

@Data
public class HireAnestMentorRequest {
	private Long mentorId;
	private String title;
	private String note;
}

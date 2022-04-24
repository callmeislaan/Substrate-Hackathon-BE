package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class BriefMentorResponse {
	private Long id;
	private String name;
	private String username;
	private String email;
	private String job;
	private int price;
	private int totalRequestReceive;
	private int totalRequestFinish;
	private float rating;
	private int totalTime;
	private boolean isAnestMentor;
	private boolean gender;
}

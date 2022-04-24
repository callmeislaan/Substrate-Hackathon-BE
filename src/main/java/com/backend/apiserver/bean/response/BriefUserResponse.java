package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class BriefUserResponse {
	private Long id;
	private String email;
	private String phone;
	private String username;
	private String fullName;
	private String status;
	private long dateOfBirth;
	private boolean gender;
}

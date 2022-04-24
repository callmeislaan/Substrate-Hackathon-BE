package com.backend.apiserver.bean.request;

import lombok.Data;

@Data
public class UserProfileRequest {
	private String fullName;
	private long dateOfBirth;
	private boolean gender;
	private String phone;
}

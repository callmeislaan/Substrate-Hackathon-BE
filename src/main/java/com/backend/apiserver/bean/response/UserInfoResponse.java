package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class UserInfoResponse {
	private String avatar;
	private String username;
	private String fullName;
	private boolean gender;
}

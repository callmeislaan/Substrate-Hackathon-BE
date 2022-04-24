package com.backend.apiserver.mapper;

import com.backend.apiserver.bean.response.UserProfileResponse;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.entity.UserDetail;
import com.backend.apiserver.utils.DateTimeUtils;

public class UserMapper {
	public static UserProfileResponse userEntityToResponse(User user) {
		UserProfileResponse userProfileResponse = new UserProfileResponse();
		userProfileResponse.setId(user.getId());
		userProfileResponse.setUsername(user.getUsername());
		userProfileResponse.setEmail(user.getEmail());
		UserDetail userDetail = user.getUserDetail();
		userProfileResponse.setCreatedDate(DateTimeUtils.toCurrentTimeMillis(user.getCreatedDate()));
		userProfileResponse.setDateOfBirth(DateTimeUtils.toCurrentTimeMillis(userDetail.getDateOfBirth()));
		userProfileResponse.setAvatar(userDetail.getAvatar());
		userProfileResponse.setPhone(userDetail.getPhone());
		userProfileResponse.setFullName(userDetail.getFullName());
		userProfileResponse.setGender(userDetail.isGender());
		return userProfileResponse;
	}
}

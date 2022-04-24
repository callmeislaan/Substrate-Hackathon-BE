package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private String avatar;
    private String fullName;
    private long dateOfBirth;
    private boolean gender;
    private String phone;
    private long createdDate;
    private String email;
    private boolean isMentor;
}

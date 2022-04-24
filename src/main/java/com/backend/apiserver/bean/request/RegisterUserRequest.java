package com.backend.apiserver.bean.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class RegisterUserRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
    @NotBlank
    private String fullName;
    private long dateOfBirth;
    private boolean gender;
    @NotBlank
    private String phone;
}

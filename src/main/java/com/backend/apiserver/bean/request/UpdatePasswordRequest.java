package com.backend.apiserver.bean.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdatePasswordRequest {
    @NotNull
    private String oldPassword;
    @NotNull
    private String newPassword;
}

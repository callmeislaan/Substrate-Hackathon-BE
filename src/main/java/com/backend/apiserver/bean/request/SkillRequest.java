package com.backend.apiserver.bean.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SkillRequest {
    @NotNull
    private String name;
    private boolean status;
}

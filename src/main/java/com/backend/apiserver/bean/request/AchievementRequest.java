package com.backend.apiserver.bean.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AchievementRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}

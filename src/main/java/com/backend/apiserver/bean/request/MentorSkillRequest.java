package com.backend.apiserver.bean.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class MentorSkillRequest {
    @Min(1)
    private long id;
    @Min(1)
    @Max(10)
    private int value;
}

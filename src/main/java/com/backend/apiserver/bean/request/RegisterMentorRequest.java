package com.backend.apiserver.bean.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterMentorRequest {
    @NotBlank
    private String job;
    @NotBlank
    private String introduction;
    private String skillDescription;
    @NotEmpty
    private List<MentorSkillRequest> mentorSkills;
    @NotBlank
    private String service;
    @Min(10000)
    private int price;
    private List<AchievementRequest> achievements;
}

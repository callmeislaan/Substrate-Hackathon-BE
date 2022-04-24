package com.backend.apiserver.bean.response;

import lombok.Data;

import java.util.List;

@Data
public class MentorShortDescriptionResponse {
    private Long id;
    private String username;
    private String fullName;
    private boolean gender;
    private String avatar;
    private String job;
    private List<SkillResponse> listSkill;
    private boolean isAnestMentor;
    private float rating;
    private int price;
    private int totalRequestFinish;
    private boolean isFollowed;
    private boolean isHired;
}

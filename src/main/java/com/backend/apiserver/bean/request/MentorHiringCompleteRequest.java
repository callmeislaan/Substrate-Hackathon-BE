package com.backend.apiserver.bean.request;

import lombok.Data;

@Data
public class MentorHiringCompleteRequest {
    private Long hireMentorId;
    private int rating;
    private String comment;
}

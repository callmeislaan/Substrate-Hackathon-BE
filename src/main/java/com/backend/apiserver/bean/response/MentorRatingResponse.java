package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class MentorRatingResponse {
    private MentorShortDescriptionResponse mentorInfo;
    private float rating;
}

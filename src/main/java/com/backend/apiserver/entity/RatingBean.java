package com.backend.apiserver.entity;

import org.springframework.beans.factory.annotation.Value;

public interface RatingBean {
    @Value("#{target.user_id}")
    Long getUserId();
    @Value("#{target.mentor_id}")
    Long getMentorId();
    float getRating();
}

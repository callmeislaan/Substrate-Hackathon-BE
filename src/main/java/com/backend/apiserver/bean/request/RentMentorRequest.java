package com.backend.apiserver.bean.request;

import lombok.Data;

@Data
public class RentMentorRequest {
    private Long requestId;
    private Long mentorId;
}

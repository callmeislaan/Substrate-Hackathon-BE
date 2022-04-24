package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class MentorOverviewResponse {
    private int totalRequestReceive;
    private int totalRequestDeny;
    private int totalHoursBeHired;
    private float averageRating;
}

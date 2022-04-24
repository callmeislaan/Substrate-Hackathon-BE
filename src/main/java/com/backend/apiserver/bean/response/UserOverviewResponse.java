package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class UserOverviewResponse {
    private int totalRequestCreate;
    private int totalHoursHiredMentor;
    private int totalPeopleHired;
}

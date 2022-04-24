package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class IncomeReportResponse {
    private int dailyIncome;
    private int weeklyIncome;
    private int monthlyIncome;
}

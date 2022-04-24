package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class UserFinanceOverviewResponse {
    private int totalBudgetCurrent;
    private int totalBudgetIn;
    private int totalMoneyCurrent;
}

package com.backend.apiserver.bean.response;

import lombok.Data;

import java.util.List;

@Data
public class DashboardResponse {
    private int numberUsers;
    private int numberMentors;
    private int numberCreatedRequests;
    private String completedRates;
    private int totalTransactions;
    private OverviewChartResponse overviewCharts;
    private TopMentorResponse topMentor;
    private RequestStatisticResponse request;
    private List<MonthlyMoneyStatisticResponse> moneyIn;
    private List<MonthlyMoneyStatisticResponse> moneyOut;
    private List<MonthlyMoneyStatisticResponse> moneyExchange;
}

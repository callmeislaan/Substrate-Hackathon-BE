package com.backend.apiserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawInfoResponse {
    private int totalBudgetCurrent;
    private int totalBudgetIn;
    private int totalMoneyCurrent;
    private List<BankCardResponse> bankCards;
    private List<BankCardResponse> eWallets;
}

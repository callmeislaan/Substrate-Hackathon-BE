package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class TransactionHistoryResponse {
    private String transactionId;
    private long createdDate;
    private String paymentMethod;
    private int amount;
    private String status;
}

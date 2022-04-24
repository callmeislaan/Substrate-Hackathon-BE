package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class BankCardResponse {
    private Long id;
    private String holderName;
    private String accountNumber;
    private String bank;
    private String branch;
}

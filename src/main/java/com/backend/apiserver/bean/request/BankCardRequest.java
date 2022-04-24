package com.backend.apiserver.bean.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BankCardRequest {
    @NotBlank
    private String holderName;
    @NotBlank
    private String accountNumber;
    @NotBlank
    private String bank;
    @NotBlank
    private String branch;
}

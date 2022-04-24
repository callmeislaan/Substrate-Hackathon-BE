package com.backend.apiserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private int id;
    private String methodName;
}

package com.backend.apiserver.bean.response;

import lombok.Data;

import java.util.List;

@Data
public class PaymentMethodResponse {
    List<PaymentResponse> paymentResponses;
}

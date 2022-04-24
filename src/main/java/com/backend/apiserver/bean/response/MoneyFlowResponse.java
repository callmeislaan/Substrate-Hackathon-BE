package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class MoneyFlowResponse {
	private Long transactionId;
	private long createdDate;
	private String paymentMethod;
	private int amount;
	private String status;
	private String username;
}

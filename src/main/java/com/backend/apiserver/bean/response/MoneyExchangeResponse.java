package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class MoneyExchangeResponse {
	private Long transactionId;
	private long createdDate;
	private int amount;
	private String status;
	private String sender;
	private String receiver;
}

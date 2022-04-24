package com.backend.apiserver.bean.request;

import lombok.Data;

@Data
public class MoneyDetailRequest {
	private String username;
	private int methodId;
	private int amount;
}

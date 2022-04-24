package com.backend.apiserver.bean.request;

import lombok.Data;

import java.util.List;

@Data
public class MoneyRequest {
	private List<MoneyDetailRequest> moneyDetailRequests;
}

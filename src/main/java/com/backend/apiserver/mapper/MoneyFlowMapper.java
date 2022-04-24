package com.backend.apiserver.mapper;

import com.backend.apiserver.bean.response.MoneyFlowResponse;
import com.backend.apiserver.entity.MoneyInHistory;
import com.backend.apiserver.utils.DateTimeUtils;

public class MoneyFlowMapper {
	public static MoneyFlowResponse moneyInToResponse(MoneyInHistory moneyInHistory, String username) {
		MoneyFlowResponse response = new MoneyFlowResponse();
		response.setAmount(moneyInHistory.getAmount());
		response.setCreatedDate(DateTimeUtils.toCurrentTimeMillis(moneyInHistory.getCreatedDate()));
		response.setPaymentMethod(moneyInHistory.getPaymentMethod().toString());
		response.setAmount(moneyInHistory.getAmount());
		response.setTransactionId(moneyInHistory.getId());
		response.setStatus(moneyInHistory.getStatus().toString());
		response.setUsername(username);
		return response;
	}
}

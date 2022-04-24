package com.backend.apiserver.mapper;

import com.backend.apiserver.entity.Mentor;
import com.backend.apiserver.entity.MoneyExchangeHistory;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;

public class MoneyExchangeHistoryMapper {
	public static MoneyExchangeHistory createMoneyExchangeHistoryEntity(User user, Mentor mentor, int amount) {
		MoneyExchangeHistory moneyExchangeHistory = new MoneyExchangeHistory();
		moneyExchangeHistory.setAmount(amount);
		moneyExchangeHistory.setUser(user);
		moneyExchangeHistory.setMentor(mentor);
		moneyExchangeHistory.setStatus(Status.ACTIVE);
		return moneyExchangeHistory;
	}
}

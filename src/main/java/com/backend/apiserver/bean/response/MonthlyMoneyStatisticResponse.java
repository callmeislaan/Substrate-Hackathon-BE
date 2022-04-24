package com.backend.apiserver.bean.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class MonthlyMoneyStatisticResponse implements Serializable {
	private String time;
	private int number;
}

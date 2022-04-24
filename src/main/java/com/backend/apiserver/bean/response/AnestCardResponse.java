package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class AnestCardResponse {
	private String serial;
	private String code;
	private String status;
	private int value;
}

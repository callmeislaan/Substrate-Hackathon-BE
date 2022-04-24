package com.backend.apiserver.bean.request;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class UseAnestCardRequest {
	@Pattern(regexp = "^(ANEST)[\\d]+$")
	private String serial;
	private String code;
}

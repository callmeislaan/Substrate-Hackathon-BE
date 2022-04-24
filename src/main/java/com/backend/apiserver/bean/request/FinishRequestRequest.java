package com.backend.apiserver.bean.request;

import lombok.Data;

@Data
public class FinishRequestRequest {
	private Long requestId;
	private int rating;
	private String comment;
}

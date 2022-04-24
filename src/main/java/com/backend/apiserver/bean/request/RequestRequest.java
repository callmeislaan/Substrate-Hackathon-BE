package com.backend.apiserver.bean.request;

import lombok.Data;

import java.util.List;

@Data
public class RequestRequest {
	private long deadline;
	private String title;
	private String content;
	private int price;
	private List<Long> skillIds;
}

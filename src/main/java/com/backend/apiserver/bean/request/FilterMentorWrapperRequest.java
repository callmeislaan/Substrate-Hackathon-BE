package com.backend.apiserver.bean.request;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class FilterMentorWrapperRequest {
	@Min(1)
	private int page = 1;
	@Min(1)
	private int size = 1;
	private String keyWord = "";
	private String sort = "average_rating";
	private String order = "desc";
	private FilterMentorRequest filter = new FilterMentorRequest();
}

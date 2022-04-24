package com.backend.apiserver.bean.request;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class FilterRequestWrapperRequest {
	@Min(1)
	private int page = 1;
	@Min(1)
	private int size = 1;
	private String keyWord = "";
	private String sort = "created_date";
	private String order = "desc";
	private FilterRequestRequest filter = new FilterRequestRequest();
}

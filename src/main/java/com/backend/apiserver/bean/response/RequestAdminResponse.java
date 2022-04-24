package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class RequestAdminResponse {
	private Long id;
	private String title;
	private String creator;
	private int price;
	private String status;
	private boolean isConflict;
}

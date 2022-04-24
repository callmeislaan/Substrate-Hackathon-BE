package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class CommentResponse {
	private Long id;
	private String fullName;
	private String avatar;
	private String content;
	private int rating;
	private long createdDate;
	private boolean gender;
}

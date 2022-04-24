package com.backend.apiserver.bean.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MentorSkillResponse {
	private Long id;
	private String name;
	private int value;
}

package com.backend.apiserver.bean.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TopMentorResponse implements Serializable {
	private List<String> mentorNames;
	private TopMentorDataResponse data;
}

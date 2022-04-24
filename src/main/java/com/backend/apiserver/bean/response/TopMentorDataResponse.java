package com.backend.apiserver.bean.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TopMentorDataResponse implements Serializable {
	List<Integer> finish;
	List<Integer> deny;
}

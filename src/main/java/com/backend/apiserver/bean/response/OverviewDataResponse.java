package com.backend.apiserver.bean.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OverviewDataResponse implements Serializable {
	private List<Integer> mentor;
	private List<Integer> request;
	private List<Integer> transaction;
}

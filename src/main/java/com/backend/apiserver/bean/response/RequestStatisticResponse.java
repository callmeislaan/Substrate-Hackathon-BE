package com.backend.apiserver.bean.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RequestStatisticResponse implements Serializable {
	private List<Integer> data;
}

package com.backend.apiserver.bean.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OverviewChartResponse implements Serializable {
	private List<String> timeSlices;
	private OverviewDataResponse data;
}

package com.backend.apiserver.bean.request;

import lombok.Data;

import javax.validation.constraints.Min;
import java.util.List;

@Data
public class FilterMentorRequest {
	private List<Long> skillIds;
	@Min(0)
	private int minPrice = 0;
	@Min(0)
	private int maxPrice = Integer.MAX_VALUE;
	@Min(0)
	private boolean anestMentor = true;
}

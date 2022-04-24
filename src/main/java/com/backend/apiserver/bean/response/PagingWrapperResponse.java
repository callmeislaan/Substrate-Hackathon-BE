package com.backend.apiserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagingWrapperResponse {
    private List<? extends Object> data;
    private long totalRecords;
}

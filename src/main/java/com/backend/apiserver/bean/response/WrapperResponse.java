package com.backend.apiserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class WrapperResponse {
    private Collection<? extends Object> data;
}

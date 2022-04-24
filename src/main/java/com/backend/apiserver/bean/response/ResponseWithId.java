package com.backend.apiserver.bean.response;

import lombok.Data;

@Data
public class ResponseWithId extends Response {
    private Long id;
    public ResponseWithId(ResponseMessage responseMessage, Long id, Object... params) {
        super(responseMessage, params);
        this.id = id;
    }
}

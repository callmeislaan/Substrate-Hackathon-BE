package com.backend.apiserver.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private String code;
    private String message;

    public Response(final ResponseMessage responseMessage, Object... params) {
        this.message = responseMessage.getMessage(params);
        this.code = responseMessage.getCode();
    }
}

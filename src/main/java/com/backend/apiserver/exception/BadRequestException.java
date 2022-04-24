package com.backend.apiserver.exception;

import com.backend.apiserver.bean.response.ResponseMessage;
import lombok.Getter;

import javax.servlet.ServletException;

@Getter
public class BadRequestException extends ServletException {
    private String code;

    public BadRequestException(final ResponseMessage responseMessage, Object... params) {
        super(responseMessage.getMessage(params));
        this.code = responseMessage.getCode();
    }
}

package com.backend.apiserver.exception.handler;

import com.backend.apiserver.bean.response.Response;
import com.backend.apiserver.bean.response.ResponseMessage;
import com.backend.apiserver.exception.BadRequestException;
import com.backend.apiserver.exception.InternalServerException;
import io.jsonwebtoken.ExpiredJwtException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Class define return httpStatus when exception is thrown
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Response exceptionHandler(Exception e) {
        LOG.warn(e.getMessage());
        return new Response(ResponseMessage.ConstraintViolationException);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseBody
    public Response tokenExpiredExceptionHandler(Exception e) {
        LOG.warn(e.getMessage());
        return new Response(ResponseMessage.TokenExpiredException);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InternalServerException.class)
    @ResponseBody
    public Response internalServerExceptionHandler(InternalServerException e) {
        LOG.warn(e.getMessage());
        return new Response(ResponseMessage.DefaultInternalServerMessageError);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseBody
    public Response optimisticLockExceptionHandler(ObjectOptimisticLockingFailureException e) {
        LOG.warn(e.getMessage());
        return new Response(ResponseMessage.AlreadyUpdatedByAnotherTransaction);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public Response badRequestExceptionHandler(BadRequestException e) throws Exception {
        LOG.warn(e.getMessage());
        return new Response(e.getCode(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Response methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) throws Exception {

        StringBuilder errorItemBuilder = new StringBuilder();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            String value = fieldError.getRejectedValue() == null ? "null" : fieldError.getRejectedValue().toString();

            errorItemBuilder.append(fieldError.getField()).append(", ");
        }

        String errorCode = ResponseMessage.InvalidAccessError.getCode();
        String errorMessage = ResponseMessage.InvalidAccessError.getMessage(errorItemBuilder.toString());

        return new Response(errorCode, errorMessage);
    }
}

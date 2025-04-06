package kr.hhplus.be.ecommerce.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> bindException(BindException e) {
        return ApiResponse.fail(
            HttpStatus.BAD_REQUEST.value(),
            e.getBindingResult().getAllErrors().get(0).getDefaultMessage()
        );
    }
}

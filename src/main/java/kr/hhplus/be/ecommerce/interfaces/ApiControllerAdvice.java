package kr.hhplus.be.ecommerce.interfaces;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> bindException(BindException e) {
        return ApiResponse.fail(
            HttpStatus.BAD_REQUEST.value(),
            e.getBindingResult().getAllErrors().get(0).getDefaultMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> illegalArgumentException(IllegalArgumentException e) {
        return ApiResponse.fail(
            HttpStatus.BAD_REQUEST.value(),
            e.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> illegalStateException(IllegalStateException e) {
        return ApiResponse.fail(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            e.getMessage()
        );
    }
}

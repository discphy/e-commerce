package kr.hhplus.be.ecommerce.client.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.*;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class ApiResponse<T> {

    private final int code;
    private final String message;
    private final T data;
}

package com.red.care.task.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final String message;

    public ApiException(String message) {
        this.message = message;
    }
}

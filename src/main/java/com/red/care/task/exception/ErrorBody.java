package com.red.care.task.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ErrorBody {

    private int httpStatus;
    private String errorMessage;
}

package com.red.care.task.exception;

import com.red.care.task.externalapi.errordecoder.GithubException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorBody> handleConstraintViolation(MissingServletRequestParameterException ex) {
        log.error("Missing Input Parameter", ex);
        ErrorBody errorBody = createErrorBody(HttpStatus.BAD_REQUEST, "Missing Input Parameter");
        return createErrorResponseEntity(HttpStatus.BAD_REQUEST, errorBody);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorBody> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("Parameter type mismatch", ex);
        ErrorBody errorBody = createErrorBody(HttpStatus.BAD_REQUEST, "Parameter type mismatch");
        return createErrorResponseEntity(HttpStatus.BAD_REQUEST, errorBody);
    }

    @ExceptionHandler(GithubException.GitHubRateLimitException.class)
    public ResponseEntity<ErrorBody> handleGithubRateLimitException(GithubException.GitHubRateLimitException ex) {
        log.error("Too many requests", ex);
        ErrorBody errorBody = createErrorBody(HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests to GitHub API");
        return createErrorResponseEntity(HttpStatus.TOO_MANY_REQUESTS, errorBody);
    }

    @ExceptionHandler(GithubException.GitHubClientException.class)
    public ResponseEntity<ErrorBody> handleGithubRateLimitException(GithubException.GitHubClientException ex) {
        log.error("Bad request", ex);
        ErrorBody errorBody = createErrorBody(HttpStatus.BAD_REQUEST, "Bad request to GitHub API");
        return createErrorResponseEntity(HttpStatus.BAD_REQUEST, errorBody);
    }

    @ExceptionHandler(GithubException.GitHubNotFoundException.class)
    public ResponseEntity<ErrorBody> handleGithubRateLimitException(GithubException.GitHubNotFoundException ex) {
        log.error("Not Found", ex);
        ErrorBody errorBody = createErrorBody(HttpStatus.NOT_FOUND, "Not founnd from GitHub API");
        return createErrorResponseEntity(HttpStatus.NOT_FOUND, errorBody);
    }

    @ExceptionHandler(GithubException.GitHubServerException.class)
    public ResponseEntity<ErrorBody> handleGithubRateLimitException(GithubException.GitHubServerException ex) {
        log.error("Internal Server Error", ex);
        ErrorBody errorBody = createErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error from GitHub API");
        return createErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, errorBody);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorBody> handleApiException(ApiException ex) {
        log.error("{}", ex.getMessage());
        final ErrorBody errorBody = createErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return createErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, errorBody);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleAnyException(Exception ex) {
        log.error("Internal server error occurred", ex);
        ErrorBody errorBody = createErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        return createErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, errorBody);
    }

    private ErrorBody createErrorBody(HttpStatus httpStatus, String message) {
        return ErrorBody.builder()
                .httpStatus(httpStatus.value())
                .errorMessage(message)
                .build();
    }

    private ResponseEntity<ErrorBody> createErrorResponseEntity(HttpStatus status, ErrorBody errorBody) {
        return ResponseEntity.status(status).body(errorBody);
    }
}

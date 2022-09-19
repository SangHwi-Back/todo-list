package com.todolist.exception;

import static com.todolist.exception.ExceptionType.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String LOG_FORMAT = "Exception Class : {}, Exception Point : {}, Message : {}";
    private static final int TRACE_POINT = 1;

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<RestResponse> globalExceptionResolver(GlobalException exception) {
        ExceptionType exceptionType = exception.getExceptionType();
        RestResponse restResponse = RestResponse.of(exceptionType.getMessage());
        return new ResponseEntity<>(restResponse, exceptionType.getHttpStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestResponse> converterExceptionResolver() {
        RestResponse restResponse = RestResponse.of(INVALID_TYPE.getMessage());
        return new ResponseEntity<>(restResponse, INVALID_TYPE.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse> MethodArgumentNotValidException(
        MethodArgumentNotValidException exception) {
        Map<String, String> map = new HashMap<>();

        for (FieldError error : exception.getFieldErrors()) {
            log.error("errorFieldName : {}, error message : {}", error.getField(), error.getDefaultMessage());
            map.put(error.getField(), error.getDefaultMessage());
        }

        RestResponse restResponse = RestResponse.of(map);
        return new ResponseEntity<>(restResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RestResponse> MethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException exception) {
        Map<String, String> map = new HashMap<>();

        map.put("errorFieldName", exception.getName());
        map.put("errorMessage", ExceptionType.CARD_TYPE_MISMATCH.getMessage());

        RestResponse restResponse = RestResponse.of(map);
        return new ResponseEntity<>(restResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundCardException.class)
    public ResponseEntity<RestResponse> NotFoundCardException(NotFoundCardException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("errorFieldName", exception.getErrorFieldName());
        map.put("errorMessage", exception.getExceptionType().getMessage());

        RestResponse restResponse = RestResponse.of(map);

        return new ResponseEntity<>(restResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestResponse> NotFoundCardException(RuntimeException exception) {
        exception.printStackTrace();
        StackTraceElement stackTraceElement = exception.getStackTrace()[TRACE_POINT];
        log.error(LOG_FORMAT,
            exception.getClass().getSimpleName(),
            stackTraceElement.getClassName() + " : " + stackTraceElement.getMethodName(),
            exception.getMessage());
        RestResponse restResponse = RestResponse.of(ExceptionType.INTERNAL_SERVER_ERROR.getMessage());

        return new ResponseEntity<>(restResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

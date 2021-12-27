package com.company.usertradersback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestControllerAdvice
public class ApiExceptionhandler {

//    //400에러 테스트!!
//    @ExceptionHandler(value = {RuntimeException.class,
//            ApiRequestException.class})
//    public ResponseEntity<?> handleApiRequestException(ApiRequestException e) {
//        // 1. 먼저 exception 정보를 포함한 페이로드를 생성합니다.
//        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
//        ApiExceptionInfo apiException = new ApiExceptionInfo(
//                e.getMessage(),
//                badRequest,
//                ZonedDateTime.now(ZoneId.of("Z"))
//        );
//        //2. ResponseEntity 객체로 반환합니다.
//        return new ResponseEntity<>(apiException, badRequest);
//    }
    //IllegalStateException
    @ExceptionHandler(value = {IllegalArgumentException.class,
            ApiIllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(ApiIllegalArgumentException e) {
        HttpStatus badRequest = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiExceptionInfo apiException = new ApiExceptionInfo(
                e.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, badRequest);
    }

    //NullPointerException
    @ExceptionHandler(value = {NullPointerException.class,
            ApiNullPointerException.class})
    public ResponseEntity<Object> handleApiNullPointerException(ApiNullPointerException e) {
        HttpStatus badRequest = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiExceptionInfo apiException = new ApiExceptionInfo(
                e.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, badRequest);
    }
}

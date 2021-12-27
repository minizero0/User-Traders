package com.company.usertradersback.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ApiExceptionInfo {
    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime timestamp;


    public ApiExceptionInfo(String message,
                            HttpStatus httpStatus,
                            ZonedDateTime timestamp) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
    }
}

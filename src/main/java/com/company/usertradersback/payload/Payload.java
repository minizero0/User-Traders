package com.company.usertradersback.payload;

import lombok.*;
import org.springframework.http.HttpStatus;

//고정 응답값 , 페이로드
@Getter
public class Payload {
    // 응답 메시지
    private String message;
    // 성공 여부
    private boolean isSuccess;
    // 통신 상태
    private HttpStatus httpStatus;

    //일반 builder
    @Builder
    public Payload(String message, boolean isSuccess, HttpStatus httpStatus) {
        this.message = message;
        this.isSuccess = isSuccess;
        this.httpStatus = httpStatus;
    }
}

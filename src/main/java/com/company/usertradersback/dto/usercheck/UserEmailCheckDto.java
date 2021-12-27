package com.company.usertradersback.dto.usercheck;

import com.company.usertradersback.payload.Payload;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// 회원가입시 해당 이메일 검사 UserEmailCheckDto
public class UserEmailCheckDto {
    // 고정 페이로드
    private Payload payload;
    //체크할 이메일
    private String email;
    //이메일 체크
    private String check;

    @Builder
    UserEmailCheckDto(Payload payload,String email,String check){
        this.payload = payload;
        this.email = email;
        this.check = check;
    }
}

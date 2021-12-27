package com.company.usertradersback.dto.usercheck;

import com.company.usertradersback.payload.Payload;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// 회원가입시 해당 닉네임 검사 UserNicknameCheckDto
public class UserNicknameCheckDto {
    // 고정 페이로드
    private Payload payload;
    //체크할 닉네임
    private String nickname;
    //닉네임 체크
    private String check;

    @Builder
    UserNicknameCheckDto(Payload payload,String nickname,String check){
        this.payload = payload;
        this.nickname =nickname;
        this.check = check;
    }
}
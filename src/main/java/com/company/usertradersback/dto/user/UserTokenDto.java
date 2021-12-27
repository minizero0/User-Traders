package com.company.usertradersback.dto.user;

import com.company.usertradersback.payload.Payload;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//로그인시 해당 회원에게 토큰값을 주는 UserTokenDto (responseDto)
public class UserTokenDto {

    // 고정 페이로드
    private Payload payload;

    // 로그인 토큰값
    private String token;

    // 로그인 성공후 해당 유저 이메일, 닉네임, 프로필 사진 반환
    private UserLoginDto user;

    private String name;


    @Builder
    public UserTokenDto(Payload payload,String token,UserLoginDto user,String name){
        this.payload = payload;
        this.token =token;
        this.user =user;
        this.name = name;
    }

}

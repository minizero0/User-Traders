package com.company.usertradersback.dto.user;

import com.company.usertradersback.payload.Payload;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//회원 토큰값 만료 여부 확인을 위한 UserValidDto (responseDto)
public class UserValidDto {
    // 고정 페이로드
    private Payload payload;

    // 토큰값 만료 여부
    private boolean valid;


    @Builder
    public UserValidDto(Payload payload,boolean valid){
        this.payload = payload;
        this.valid = valid;
    }
}

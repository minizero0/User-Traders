package com.company.usertradersback.dto.islogined;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
//해당 회원이 로그인한지 안한지를 확인하는 UserIsLoginedDto
public class UserIsLoginedDto {
    // 회원 고유번호
    private Integer id;

    // 로그인 상태 0:로그아웃,1:로그인
    private Integer status;

    // 로그인시간
    private LocalDateTime loginAt;

    // 로그아웃시간
    private LocalDateTime logoutAt;

}

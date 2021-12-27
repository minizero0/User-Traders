package com.company.usertradersback.dto.grades;

import com.company.usertradersback.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserGradesDto {

    // 평점 고유번호
    private Integer id;

    // 평점 주는 회원
    private UserEntity userSendId;

    // 평점 받는 회원
    private UserEntity userRecvId;

    // 평점 1~5점
    private Integer grade;

    //평정 등록 날짜
    private LocalDateTime createAt;

    @Builder
    public UserGradesDto(Integer id,
                            UserEntity userSendId,
                            UserEntity userRecvId,
                            Integer grade,
                         LocalDateTime createAt){
        this.id = id;
        this.userSendId = userSendId;
        this.userRecvId = userRecvId;
        this.grade =grade;
        this.createAt = createAt;
    }
}

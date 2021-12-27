package com.company.usertradersback.dto.user;

import com.company.usertradersback.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//간략한 회원 정보 UserConciseDto
public class UserConciseDto {

    private Integer id;

    private String nickName;

    private String imagePath;

    private String userGrade;

    @Builder
    public UserConciseDto(Integer id, String nickName, String imagePath,String userGrade) {
        this.id = id;
        this.nickName = nickName;
        this.imagePath = imagePath;
        this.userGrade = userGrade;
    }

    //게시물 조회에서 회원(user)의 특정 칼럼만 조회하기 위한, userEntity의 특정 데이터만 userConciseDto 로 변환
    public UserConciseDto convertEntityToDto(UserEntity userEntity,String userGrade) {
        return UserConciseDto.builder()
                .id(userEntity.getId())
                .nickName(userEntity.getNickname())
                .imagePath(userEntity.getImagePath())
                .userGrade(userGrade)
                .build();
    }

}

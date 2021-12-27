package com.company.usertradersback.dto.user;

import com.company.usertradersback.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserBoardResponseDto {

    private Integer id;

    private String nickName;

    private String imagePath;


    @Builder
    public UserBoardResponseDto(Integer id, String nickName, String imagePath) {
        this.id = id;
        this.nickName = nickName;
        this.imagePath = imagePath;
    }

    public UserBoardResponseDto convertEntityToDto(UserEntity userEntity) {
        return UserBoardResponseDto.builder()
                .id(userEntity.getId())
                .nickName(userEntity.getNickname())
                .imagePath(userEntity.getImagePath())
                .build();
    }


}

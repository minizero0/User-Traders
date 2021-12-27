package com.company.usertradersback.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginDto {

    @Builder
    public UserLoginDto(Integer userId, String email, String nickcname, String imagePath) {
        this.userId = userId;
        this.email = email;
        this.nickcname = nickcname;
        this.imagePath = imagePath;
    }

    private Integer userId;

    private String email;

    private String nickcname;

    private String imagePath;



}

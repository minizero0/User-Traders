package com.company.usertradersback.dto.department;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//유저-학과 학과 테이블 조회를 위한 UserDto(responseDto)
public class UserDepartmentDto {

    // 학과 고유 번호
    private Integer id;

    // 학과명
    private String name;

    @Builder
    public UserDepartmentDto(Integer id, String name){
        this.id = id;
        this.name = name;
    }
}

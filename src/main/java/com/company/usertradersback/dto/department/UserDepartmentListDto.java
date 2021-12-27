package com.company.usertradersback.dto.department;


import com.company.usertradersback.payload.Payload;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
//학과 테이블 전체 조회를 위한 UserDepartmentListDto(responseDto)
public class UserDepartmentListDto {

    //페이로드
    private Payload payload;

    //학과 전체 리스트
    private List<UserDepartmentDto> userDepartmentDtoList;

    @Builder
    public UserDepartmentListDto(Payload payload, List<UserDepartmentDto> userDepartmentDtoList){
        this.payload = payload;
        this.userDepartmentDtoList=userDepartmentDtoList;
    }
}
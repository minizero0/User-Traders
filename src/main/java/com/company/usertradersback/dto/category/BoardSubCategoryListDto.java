package com.company.usertradersback.dto.category;

import com.company.usertradersback.payload.Payload;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
// 모든 대분류 카테고리 반환을 위한 BoardSubCategoryListDto
public class BoardSubCategoryListDto {
    private Payload payload;

    private List<BoardSubCategoryDto> boardSubCategoryDtoList;

    @Builder
    public BoardSubCategoryListDto(Payload payload,List<BoardSubCategoryDto> boardSubCategoryDtoList){
        this.payload = payload;
        this.boardSubCategoryDtoList = boardSubCategoryDtoList;
    }
}

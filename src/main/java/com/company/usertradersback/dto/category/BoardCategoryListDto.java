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
// 모든 카테고리 List 반환을 위한 BoardCategoryListDto
public class BoardCategoryListDto {

    private Payload payload;

    private List<BoardCategoryDto> boardCategoryDtoList;

    @Builder
    public BoardCategoryListDto(Payload payload,List<BoardCategoryDto> boardCategoryDtoList){
        this.payload = payload;
        this.boardCategoryDtoList = boardCategoryDtoList;
    }

}
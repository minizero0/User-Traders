package com.company.usertradersback.dto.category;

import com.company.usertradersback.entity.BoardSubCategoryEntity;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
// 카테고리 조회를 위한 BoardCategoryDto
public class BoardCategoryDto {
    // 대분류 카테고리 고유번호
    private Integer id;

    // 서브 카테고리 고유번호
    private BoardSubCategoryEntity subCategoryId;

    // 카테고리명
    private String name;

    @Builder
    public BoardCategoryDto(Integer id, BoardSubCategoryEntity subCategoryId,
                            String name){
        this.id =  id;
        this.subCategoryId = subCategoryId;
        this.name = name;
    }

}

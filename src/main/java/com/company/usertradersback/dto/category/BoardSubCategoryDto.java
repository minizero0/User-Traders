package com.company.usertradersback.dto.category;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
// 서브 카테고리 조회를 위한 BoardSubCategoryDto
public class BoardSubCategoryDto {

    // 서브 카테고리 고유번호
    private Integer id;

    // 카테고리명
    private String name;

    @Builder
    public BoardSubCategoryDto(Integer id,String name){
        this.id = id ;
        this.name = name;
    }
}

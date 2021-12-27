package com.company.usertradersback.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "BoardSubCategory")
public class BoardSubCategoryEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 서브 카테고리 고유번호
    private Integer id;

    @Column(name = "name")
    // 카테고리명
    private String name;

    //빌더
    @Builder
    public BoardSubCategoryEntity(Integer id
            , String name
    ) {
        this.id = id;
        this.name = name;
    }

}

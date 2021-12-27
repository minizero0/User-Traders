package com.company.usertradersback.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "BoardCategory")
public class BoardCategoryEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 대분류 카테고리 고유번호
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subCategoryId")
    // 서브 카테고리 고유번호
    private BoardSubCategoryEntity subCategoryId;

    @Column(name = "name")
    // 카테고리명
    private String name;

    //빌더
    @Builder
    public BoardCategoryEntity(Integer id
            , BoardSubCategoryEntity subCategoryId,
                               String name
    ) {
        this.id = id;
        this.subCategoryId =subCategoryId;
        this.name = name;
    }
}

package com.company.usertradersback.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "Board")
public class BoardEntity{

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 게시물 고유 번호
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "userId")
    // 회원 고유 번호
    private UserEntity userId;

    @Column(name = "title")
    // 게시물 제목
    private String title;

    @Column(name = "content")
    // 게시물 내용
    private String content;

    @Column(name = "price")
    // 게시물 가격
    private String price;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    // 카테 고리 고유 번호
    private BoardCategoryEntity categoryId;

    @Column(name = "views")
    // 게시물 조회수
    private Integer views;

    @Column(name = "grade")
    // 게시물 등급 0:새상품,1:S급,2:A급,3:B급
    private Integer grade;

    @Column(name = "status")
    // 게시물 판매 상태 0:판매중,1:예약중,2:판매완료
    private Integer status;

    @Column(name = "createAt")
    // 게시물 등록 날짜
    private LocalDateTime createAt;

    @Column(name = "modifiedAt")
    // 게시물 수정 날짜
    private LocalDateTime modifiedAt;

    // 빌더
    @Builder
    public BoardEntity(Integer id,UserEntity userId, String title, String content,
                    String price, BoardCategoryEntity categoryId, Integer views,
                    Integer grade,Integer status,
                    LocalDateTime createAt, LocalDateTime modifiedAt) {

        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.price = price;
        this.categoryId = categoryId;
        this.views = views;
        this.grade = grade;
        this.status = status;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
    }
}
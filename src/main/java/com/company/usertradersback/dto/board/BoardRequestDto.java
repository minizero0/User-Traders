package com.company.usertradersback.dto.board;

import com.company.usertradersback.entity.BoardCategoryEntity;
import com.company.usertradersback.entity.BoardEntity;
import com.company.usertradersback.entity.UserEntity;
import com.company.usertradersback.payload.Payload;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
//게시물 저장을 위한 BoardRequestDto
public class BoardRequestDto {

    //고정 페이로드
    private Payload payload;

    // 게시물 고유번호
    private Integer id;

    // 회원 고유번호
    private UserEntity userId;

    // 게시물 제목
    private String title;

    // 게시물 내용
    private String content;

    // 게시물 가격
    private String price;

    // 카테고리 고유번호
    private BoardCategoryEntity categoryId;

    // 게시물 조회수
    private Integer views;

    // 게시물 등급 0:새상품,1:S급,2:A급,3:B급
    private Integer grade;

    // 게시물 판매상태 0:판매중,1:예약중,2:판매완료
    private Integer status;

    // 게시물 등록날짜
    private LocalDateTime createAt;

    // 게시물 수정날짜
    private LocalDateTime modifiedAt;

    // 빌더
    @Builder
    public BoardRequestDto(Payload payload, Integer id, UserEntity userId, String title, String content,
                           String price, BoardCategoryEntity categoryId, Integer views,
                           Integer grade, Integer status,
                           LocalDateTime createAt, LocalDateTime modifiedAt) {
        this.payload = payload;
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

    //게시물 글쓰기 저장을 위한 BoardDto를 BoardEntity로 변환.
    public BoardEntity convertDtoToEntity() {
        BoardEntity boardEntity = BoardEntity.builder()
                .id(id)
                .userId(userId)
                .title(title)
                .content(content)
                .price(price)
                .categoryId(categoryId)
                .views(views)
                .grade(grade)
                .status(status)
                .createAt(createAt)
                .modifiedAt(modifiedAt)
                .build();
        return boardEntity;
    }
}

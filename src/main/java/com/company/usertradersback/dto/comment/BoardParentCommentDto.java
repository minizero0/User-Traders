package com.company.usertradersback.dto.comment;

import com.company.usertradersback.entity.BoardEntity;
import com.company.usertradersback.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BoardParentCommentDto {
    // 부모댓글 고유번호
    private Integer id;

    // 회원 고유번호
    private UserEntity userId;

    // 게시물 고유번호
    private BoardEntity boardId;

    // 댓글 내용
    private String comment;

    // 댓글 등록날짜
    private LocalDateTime createAt;

    @Builder
    public BoardParentCommentDto(Integer id,UserEntity userId
            ,BoardEntity boardId, String comment,LocalDateTime createAt){

        this.id = id;
        this.userId = userId;
        this.boardId = boardId;
        this.comment = comment;
        this.createAt = createAt;
    }
}

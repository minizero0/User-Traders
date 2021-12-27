package com.company.usertradersback.dto.comment;

import com.company.usertradersback.dto.user.UserBoardResponseDto;
import com.company.usertradersback.entity.BoardChildCommentEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BoardChildCommentResponseDto {


    // 자식댓글 고유번호
    private Integer id;

    // 회원 고유번호
    private UserBoardResponseDto userId;

    // 댓글 내용
    private String comment;

    // 댓글 등록날짜
    private LocalDateTime createAt;

    @Builder
    public BoardChildCommentResponseDto(Integer id, UserBoardResponseDto userId, String comment, LocalDateTime createAt) {

        this.id = id;
        this.userId = userId;
        this.comment = comment;
        this.createAt = createAt;
    }

    public BoardChildCommentResponseDto convertEntityToDto(BoardChildCommentEntity boardChildCommentEntity
           ){
        return BoardChildCommentResponseDto.builder()
                .id(boardChildCommentEntity.getId())
                .userId(new UserBoardResponseDto().convertEntityToDto(boardChildCommentEntity.getUserId()))
                .comment(boardChildCommentEntity.getComment())
                .createAt(boardChildCommentEntity.getCreateAt())
                .build();
    }
}

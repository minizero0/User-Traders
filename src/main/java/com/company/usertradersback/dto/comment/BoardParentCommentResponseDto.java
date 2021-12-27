package com.company.usertradersback.dto.comment;

import com.company.usertradersback.dto.user.UserBoardResponseDto;
import com.company.usertradersback.entity.BoardParentCommentEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoardParentCommentResponseDto {

    // 부모댓글 고유번호
    private Integer id;

    // 회원 고유번호
    private UserBoardResponseDto userId;

    // 게시물 고유번호
    private Integer boardId;

    // 댓글 내용
    private String comment;

    // 댓글 등록날짜
    private LocalDateTime createAt;

    //대댓글
    private List<BoardChildCommentResponseDto> boardChildComment;

    @Builder
    public BoardParentCommentResponseDto(Integer id, UserBoardResponseDto userId, Integer boardId, String comment, LocalDateTime createAt
    , List<BoardChildCommentResponseDto> boardChildComment) {
        this.id = id;
        this.userId = userId;
        this.boardId = boardId;
        this.comment = comment;
        this.createAt = createAt;
        this.boardChildComment = boardChildComment;
    }
    //게시물 전체 조회를 위한, BoardEntity 를 BoardResponseDto 로 변환
    public BoardParentCommentResponseDto convertEntityToDto(BoardParentCommentEntity boardParentCommentEntity
                                                      ,List<BoardChildCommentResponseDto> boardChildComment
                                                      ) {

        return BoardParentCommentResponseDto.builder()
                .id(boardParentCommentEntity.getId())
                .userId(new UserBoardResponseDto().convertEntityToDto(boardParentCommentEntity.getUserId()))
                .boardId(boardParentCommentEntity.getBoardId().getId())
                .comment(boardParentCommentEntity.getComment())
                .createAt(boardParentCommentEntity.getCreateAt())
                .boardChildComment(boardChildComment)
                .build();
    }
}

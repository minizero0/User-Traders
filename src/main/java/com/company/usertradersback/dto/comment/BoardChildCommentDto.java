package com.company.usertradersback.dto.comment;

import com.company.usertradersback.entity.BoardParentCommentEntity;
import com.company.usertradersback.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BoardChildCommentDto {

    // 자식댓글 고유번호
    private Integer id;

    // 회원 고유번호
    private UserEntity userId;

    // 부모댓글 고유번호
    private BoardParentCommentEntity pcommentId;

    // 댓글 내용
    private String comment;

    // 댓글 등록날짜
    private LocalDateTime createAt;

    @Builder
    public BoardChildCommentDto(Integer id, UserEntity userId
            , BoardParentCommentEntity pcommentId, String comment,
                                   LocalDateTime createAt) {

        this.id = id;
        this.userId = userId;
        this.pcommentId = pcommentId;
        this.comment = comment;
        this.createAt = createAt;
    }
}

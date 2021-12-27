package com.company.usertradersback.dto.declaration;

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
public class BoardDeclarationDto {

    // 신고 고유번호
    private Integer id;

    // 게시물 고유번호
    private BoardEntity boardId;

    // 회원 고유번호
    private UserEntity userId;

    // 신고 내용
    private String content;

    // 기타 내용
    private String otherContent;

    // 신고 등록날짜
    private LocalDateTime createAt;

    @Builder
    public BoardDeclarationDto(Integer id, BoardEntity boardId,
                                  UserEntity userId, String content
            , String otherContent, LocalDateTime createAt) {

        this.id = id;
        this.boardId = boardId;
        this.userId = userId;
        this.content = content;
        this.otherContent = otherContent;
        this.createAt = createAt;
    }
}

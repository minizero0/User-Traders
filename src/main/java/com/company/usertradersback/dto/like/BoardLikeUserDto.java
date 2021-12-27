package com.company.usertradersback.dto.like;

import com.company.usertradersback.dto.board.BoardImageDto;
import com.company.usertradersback.entity.BoardEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@NoArgsConstructor
public class BoardLikeUserDto {

    // 좋아요 고유번호
    private Integer id;

    // 게시물 고유번호
    @ManyToOne
    @JoinColumn(name = "boardId")
    private BoardEntity boardId;

    private BoardImageDto boardImageDto;

    @Builder
    public BoardLikeUserDto(Integer id,BoardEntity boardId,BoardImageDto boardImageDto){
        this.id = id;
        this.boardId = boardId;
        this.boardImageDto = boardImageDto;
    }

}

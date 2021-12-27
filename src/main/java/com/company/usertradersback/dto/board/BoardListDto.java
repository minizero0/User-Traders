package com.company.usertradersback.dto.board;

import com.company.usertradersback.payload.Payload;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
// 모든 게시물 List 반환을 위한 BoardListDto
public class BoardListDto {


    private Payload payload;

    private List<BoardResponseDto> boardResponseDtoList;

    @Builder
    public BoardListDto(Payload payload, List<BoardResponseDto> boardResponseDtoList) {
        this.payload = payload;
        this.boardResponseDtoList = boardResponseDtoList;
    }

}

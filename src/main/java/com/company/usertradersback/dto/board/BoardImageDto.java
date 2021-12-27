package com.company.usertradersback.dto.board;


import com.company.usertradersback.entity.BoardImageEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BoardImageDto {

    private Integer id;

    private Integer boardId;

    private String path;

    private String type;

    private Long size;

    private LocalDateTime createAt;

    private LocalDateTime modifiedAt;

    @Builder
    public BoardImageDto(Integer id, Integer boardId, String path, String type, Long size, LocalDateTime createAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.boardId = boardId;
        this.path = path;
        this.type = type;
        this.size = size;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
    }

    public BoardImageDto convertEntityToDto(BoardImageEntity boardImageEntity){
        return BoardImageDto.builder()
                .id(boardImageEntity.getId())
                .boardId(boardImageEntity.getBoardId().getId())
                .path(boardImageEntity.getPath())
                .type(boardImageEntity.getType())
                .size(boardImageEntity.getSize())
                .createAt(boardImageEntity.getCreateAt())
                .modifiedAt(boardImageEntity.getModifiedAt())
                .build();
    }
}


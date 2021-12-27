package com.company.usertradersback.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "BoardImage")
public class BoardImageEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 게시물 이미지 고유번호
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "boardId")
    // 게시물 고유번호
    private BoardEntity boardId;

    @Column(name = "path")
    // 이미지 경로
    private String path;

    @Column(name = "type")
    // 이미지 타입 ex)image/jpeg
    private String type;

    @Column(name = "size")
    // 이미지 용량 단위 : bytes
    private Long size;

    @Column(name = "createAt")
    // 이미지 등록 날짜
    private LocalDateTime createAt;

    @Column(name = "modifiedAt")
    // 이미지 수정 날짜
    private LocalDateTime modifiedAt;

    //빌더
    @Builder
    public BoardImageEntity(Integer id, BoardEntity boardId,
                            String path, String type, Long size,
                            LocalDateTime createAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.boardId = boardId;
        this.path = path;
        this.type = type;
        this.size = size;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
    }

}

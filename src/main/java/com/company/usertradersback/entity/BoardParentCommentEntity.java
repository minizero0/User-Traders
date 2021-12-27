package com.company.usertradersback.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "BoardParentComment")
public class BoardParentCommentEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 부모댓글 고유번호
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "userId")
    // 회원 고유번호
    private UserEntity userId;

    @ManyToOne
    @JoinColumn(name = "boardId")
    // 게시물 고유번호
    private BoardEntity boardId;

    @Column(name = "comment")
    // 댓글 내용
    private String comment;

    @Column(name = "createAt")
    // 댓글 등록날짜
    private LocalDateTime createAt;

    @Builder
    public BoardParentCommentEntity(Integer id,UserEntity userId
    ,BoardEntity boardId, String comment,LocalDateTime createAt){

        this.id = id;
        this.userId = userId;
        this.boardId = boardId;
        this.comment = comment;
        this.createAt = createAt;
    }
}

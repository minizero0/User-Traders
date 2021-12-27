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
@Table(name = "BoardChildComment")
public class BoardChildCommentEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 자식댓글 고유번호
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "userId")
    // 회원 고유번호
    private UserEntity userId;

    @ManyToOne
    @JoinColumn(name = "pcommentId")
    // 부모댓글 고유번호
    private BoardParentCommentEntity pcommentId;

    @Column(name = "comment")
    // 댓글 내용
    private String comment;

    @Column(name = "createAt")
    // 댓글 등록날짜
    private LocalDateTime createAt;

    @Builder
    public BoardChildCommentEntity(Integer id, UserEntity userId
            , BoardParentCommentEntity pcommentId, String comment,
                                   LocalDateTime createAt) {

        this.id = id;
        this.userId = userId;
        this.pcommentId = pcommentId;
        this.comment = comment;
        this.createAt = createAt;
    }
}

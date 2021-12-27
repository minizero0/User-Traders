package com.company.usertradersback.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "BoardLikeUser")
public class BoardLikeUserEntity {
    @Id
    @JoinColumn(name = "userId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 좋아요 고유번호
    private Integer id;


    // 게시물 고유번호
    @ManyToOne
    @JoinColumn(name = "boardId")
    private BoardEntity boardId;

    // 회원 고유번호
    @ManyToOne
    @JoinColumn(name = "userId")
    private UserEntity userId;

    // 좋아요 등록날짜
    @JoinColumn(name = "createAt")
    private LocalDateTime createtAt;

    @Builder
    public BoardLikeUserEntity(Integer id,BoardEntity boardId,
                               UserEntity userId,LocalDateTime createtAt){
        this.id = id;
        this.boardId = boardId;
        this.userId = userId;
        this.createtAt = createtAt;
    }


}

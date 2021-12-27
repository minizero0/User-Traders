package com.company.usertradersback.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "BoardDeclaration")
public class BoardDeclarationEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 신고 고유번호
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "boardId")
    // 게시물 고유번호
    private BoardEntity boardId;

    @ManyToOne
    @JoinColumn(name = "userId")
    // 회원 고유번호
    private UserEntity userId;

    @Column(name = "content")
    // 신고 내용
    private String content;

    @Column(name = "otherContent")
    // 기타 내용
    private String otherContent;

    @CreatedDate
    @Column(name = "createAt")
    // 신고 등록날짜
    private LocalDateTime createAt;

    @Builder
    public BoardDeclarationEntity(Integer id, BoardEntity boardId,
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

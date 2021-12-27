package com.company.usertradersback.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Chatroom")
public class ChatRoomEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "roomId")
    private String roomId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyUserId")
    private UserEntity buyUserId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sellUserId")
    private UserEntity sellUserId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "boardId")
    private BoardEntity boardId;

    @Column(name = "name")
    private String name;

    @Column(name = "createAt")
    // 게시물 등록 날짜
    private LocalDateTime createAt;

    @Column(name = "modifiedAt")
    // 게시물 수정 날짜
    private LocalDateTime modifiedAt;

    @Builder
    public ChatRoomEntity(Integer id,BoardEntity boardId,String roomId, UserEntity buyUserId, UserEntity sellUserId, String name, LocalDateTime createAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.roomId = roomId;
        this.buyUserId = buyUserId;
        this.sellUserId = sellUserId;
        this.name = name;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
        this.boardId = boardId;
    }

}

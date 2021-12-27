package com.company.usertradersback.entity;

import com.company.usertradersback.dto.chat.ChatMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "ChatroomMessage")
public class ChatRoomMessageEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sender")
    private String sender;

    @Column(name = "roomId")
    private String roomId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private UserEntity userId;

    @Column(name = "message")
    private String message;

    @Column(name = "createAt")
    private LocalDateTime createAt;

    @Column(name = "type")
    private ChatMessage.MessageType type;

    @Column(name = "userCount")
    private long userCount;

    @Builder
    public ChatRoomMessageEntity(Integer id, String sender, String roomId, UserEntity userId, String message, LocalDateTime createAt, ChatMessage.MessageType type, long userCount) {
        this.id = id;
        this.sender = sender;
        this.roomId = roomId;
        this.userId = userId;
        this.message = message;
        this.createAt = createAt;
        this.type = type;
        this.userCount = userCount;
    }

}
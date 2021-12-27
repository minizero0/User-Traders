package com.company.usertradersback.dto.chat;


import com.company.usertradersback.entity.ChatRoomMessageEntity;
import com.company.usertradersback.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessage {

    @Builder
    public ChatMessage(MessageType type, String roomId, String sender, String message, long userCount,UserEntity userId, LocalDateTime createAt) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.userId = userId;
        this.message = message;
        this.userCount = userCount;
        this.createAt = createAt;
    }

    // 메시지 타입 : 입장, 퇴장, 채팅
    public enum MessageType {
        ENTER, QUIT, TALK
    }

    private MessageType type; // 메시지 타입
    private String roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private UserEntity userId;
    private String message; // 메시지
    private long userCount; // 채팅방 인원수, 채팅방 내에서 메시지가 전달될때 인원수 갱신시 사용
    private LocalDateTime createAt;

    public ChatRoomMessageEntity toEntity() { //저장을 위한 엔티티

        ChatRoomMessageEntity chatRoomMessageEntity = ChatRoomMessageEntity.builder()
                .sender(sender)
                .roomId(roomId)
                .userId(userId)
                .message(message)
                .createAt(LocalDateTime.now())
                .type(type)
                .userCount(userCount)
                .build();

        return chatRoomMessageEntity;
    }
}
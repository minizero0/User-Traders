//package com.company.usertradersback.entity;
//
//import lombok.Getter;
//import lombok.Setter;
//
//import java.io.Serializable;
//import java.util.UUID;
//
//@Getter
//@Setter
//public class ChatRoom implements Serializable {
//
//    private static final long serialVersionUID = 6494678977089006639L;
//
//    private String roomId;
//    private String name;
//    private long userCount; // 채팅방 인원수
//
//    public static ChatRoom create(String name) {
//        ChatRoom chatRoom = new ChatRoom();
//        chatRoom.roomId = UUID.randomUUID().toString();
//        chatRoom.name = name;
//        return chatRoom;
//    }
//}
package com.company.usertradersback.dto.chat;

import com.company.usertradersback.entity.BoardEntity;
import com.company.usertradersback.entity.ChatRoomEntity;
import com.company.usertradersback.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    private String roomId;
    private String name;
    private long userCount; // 채팅방 인원수
    private Integer id;
    private UserEntity sellUserId;
    private UserEntity buyUserId;
    private BoardEntity boardId;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private String newMessage; //최신 메시지

    public static ChatRoom create(String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.name = name;
        return chatRoom;
    }

    public ChatRoom() {

    }

    @Builder
    public ChatRoom(String roomId, String name,
                    BoardEntity boardId,
                    long userCount, Integer id, UserEntity sellUserId, UserEntity buyUserId
    ,LocalDateTime createAt,LocalDateTime modifiedAt,String newMessage) {
        this.roomId = roomId;
        this.name = name;
        this.userCount = userCount;
        this.id = id;
        this.sellUserId = sellUserId;
        this.buyUserId = buyUserId;
        this.boardId = boardId;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
        this.newMessage = newMessage;
    }

    public ChatRoom convertEntityToDto(ChatRoomEntity chatRoomEntity,String newMessage) {
        return ChatRoom.builder()
                .id(chatRoomEntity.getId())
                .roomId(chatRoomEntity.getRoomId())
                .sellUserId(chatRoomEntity.getSellUserId())
                .buyUserId(chatRoomEntity.getBuyUserId())
                .name(chatRoomEntity.getName())
                .boardId(chatRoomEntity.getBoardId())
                .createAt(chatRoomEntity.getCreateAt())
                .modifiedAt(chatRoomEntity.getModifiedAt())
                .newMessage(newMessage)
                .build();
    }

}
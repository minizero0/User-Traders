package com.company.usertradersback.service;


import com.company.usertradersback.dto.chat.ChatMessage;
import com.company.usertradersback.entity.ChatRoomMessageEntity;
import com.company.usertradersback.repository.ChatMessageJpaRepository;
import com.company.usertradersback.repository.ChatRoomJpaRepository;
import com.company.usertradersback.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final ChatMessageJpaRepository chatMessageJpaRepository;

    /**
     * destination정보에서 roomId 추출
     */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessage chatMessage) {
        chatMessage.setUserCount(chatRoomRepository.getUserCount(chatMessage.getRoomId()));
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
            chatMessage.setSender("[알림]");
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
            chatMessage.setSender("[알림]");
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }

    @Transactional //해당 채팅방에 메시지들을 저장해주는 save
    public String saveChatMessage(ChatMessage chatMessage) {

        return chatMessageJpaRepository.save(chatMessage.toEntity()).getMessage();
    }

    //해당 채팅방 이름을 가진 메시지 전부를 출력.
    @Transactional
    public List<ChatMessage> getChatMessageList(String roomId){
//        hashOpsChatRoom.values(CHAT_ROOMS);

        List<ChatRoomMessageEntity> chatRoomMessageEntityList = chatMessageJpaRepository.findByRoomId(roomId);
        List<ChatMessage> results = chatRoomMessageEntityList.stream().map(chatRoomMessageEntity -> {
            ChatMessage chatMessage = ChatMessage.builder()
                    .sender(chatRoomMessageEntity.getSender())
                    .roomId(chatRoomMessageEntity.getRoomId())
                    .userId(chatRoomMessageEntity.getUserId())
                    .message(chatRoomMessageEntity.getMessage())
                    .type(chatRoomMessageEntity.getType())
                    .userCount(chatRoomMessageEntity.getUserCount())
                    .createAt(chatRoomMessageEntity.getCreateAt())
                    .build();
            return chatMessage;
        }).collect(Collectors.toList());
        return results;

    }

    private ChatMessage convertEntityToDto(ChatRoomMessageEntity chatRoomMessageEntity) { //엔티티 객체 변수를 디티오 객체 변수로 변환
        return ChatMessage.builder()
                .sender(chatRoomMessageEntity.getSender())
                .roomId(chatRoomMessageEntity.getRoomId())
                .userId(chatRoomMessageEntity.getUserId())
                .message(chatRoomMessageEntity.getMessage())
                .type(chatRoomMessageEntity.getType())
                .userCount(chatRoomMessageEntity.getUserCount())
                .build();
    }

}
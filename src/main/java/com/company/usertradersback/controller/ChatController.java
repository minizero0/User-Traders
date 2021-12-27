package com.company.usertradersback.controller;

import com.company.usertradersback.config.jwt.JwtTokenProvider;
import com.company.usertradersback.dto.chat.ChatMessage;
import com.company.usertradersback.entity.ChatRoomMessageEntity;
import com.company.usertradersback.entity.UserEntity;
import com.company.usertradersback.env.Url;
import com.company.usertradersback.repository.ChatMessageJpaRepository;
import com.company.usertradersback.repository.ChatRoomRepository;
import com.company.usertradersback.repository.UserRepository;
import com.company.usertradersback.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = Url.url)
@RestController
public class ChatController {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final ChatMessageJpaRepository chatMessageJpaRepository;
    private final UserRepository userRepository;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public String message(ChatMessage message, @Header("token") String token) {
        log.info("메시지용", message);
        System.out.println("@@@@@@@@메시지 들어오나요???");
        System.out.println(message);

        String email = jwtTokenProvider.getUserPk(token);
        Optional<UserEntity> userEntityWrapper = userRepository.findByEmail(email);
        UserEntity userEntity = userEntityWrapper.get();

        String nickname = userEntity.getNickname();
        // 로그인 회원 정보로 대화명 설정
        System.out.println(nickname);

        message.setSender(nickname);
        // 채팅방 인원수 세팅
        message.setUserCount(chatRoomRepository.getUserCount(message.getRoomId()));
//        LocalDateTime dateTime =LocalDateTime.now();
//                message.setCreateAt(dateTime);
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        chatService.sendChatMessage(message);


//        String email = jwtTokenProvider.getUserPk(token);


        ChatRoomMessageEntity chatRoomMessageEntity = ChatRoomMessageEntity.builder()
                .roomId(message.getRoomId())
                .sender(message.getSender())
                .userId(userEntity)
                .message(message.getMessage())
                .type(message.getType())
                .userCount(message.getUserCount())
                .createAt(LocalDateTime.now())
                .build();
        System.out.println("저장중");
        ChatRoomMessageEntity saveMassage = chatMessageJpaRepository.save(chatRoomMessageEntity);
        System.out.println("저장완료");
        System.out.println(saveMassage.getMessage());
        return saveMassage.getMessage();

    }
}

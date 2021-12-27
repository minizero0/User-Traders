package com.company.usertradersback.controller;

import com.company.usertradersback.config.jwt.JwtTokenProvider;
import com.company.usertradersback.dto.chat.ChatMessage;
import com.company.usertradersback.dto.chat.ChatRoom;
import com.company.usertradersback.dto.user.UserTokenDto;
import com.company.usertradersback.entity.UserEntity;
import com.company.usertradersback.env.Url;
import com.company.usertradersback.repository.ChatRoomRepository;
import com.company.usertradersback.repository.UserRepository;
import com.company.usertradersback.service.ChatService;
import com.company.usertradersback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

// import 생략...

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = Url.url)
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final UserService userService;

    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms(Model model) {
        return "chat/room";
    }

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room(@RequestHeader("token") String token
            , @AuthenticationPrincipal UserEntity userEntity) {

        if (userService.validToken(token)) {
            //        return chatRoomRepository.findAllRoom();

            return chatRoomRepository.findAllRoomUser(userEntity, userEntity);
        } else {
            return null;
        }

    }

    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public ChatRoom createRoom(
            @RequestHeader("token") String token,
            @RequestBody ChatRoom reqChatRoom,@AuthenticationPrincipal UserEntity buyUserId ) {
        //예비로 받았다고 가정
        if (userService.validToken(token)) {
            ChatRoom chatRoom = chatRoomRepository.createChatRoom(reqChatRoom.getName());

            chatRoomRepository.registerChatRoom(chatRoom, reqChatRoom.getName(), reqChatRoom.getSellUserId(), buyUserId
            ,reqChatRoom.getBoardId()
            );
            return chatRoom;
        } else {
            return null;
        }
    }

//    // 채팅방 입장 화면
//    @GetMapping("/room/enter/{roomId}")
//    public String roomDetail(Model model, @PathVariable String roomId) {
//        model.addAttribute("roomId", roomId);
//        return "chat/roomdetail";
//    }
    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public List<ChatMessage> roomDetail(@PathVariable("roomId") String roomId) {

        List<ChatMessage> chatMessage = chatService.getChatMessageList(roomId);
        return chatMessage;
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/user")
    @ResponseBody
    public UserTokenDto getUserInfo( @RequestHeader("token") String token,@AuthenticationPrincipal UserEntity user) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        String name = auth.getName();

        if (!userService.validToken(token)) {
          return null;
        }
        String name = user.getUsername();
        List<String> roles = Collections.singletonList("일반회원");
        return UserTokenDto.builder().name(name).token(jwtTokenProvider.createToken(name, roles)).build();
    }
}
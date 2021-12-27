//package com.company.usertradersback.repository;
//
//
//import com.company.usertradersback.entity.ChatRoom;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.Optional;
//
//@RequiredArgsConstructor
//@Service
//public class ChatRoomRepository {
//    // Redis CacheKeys
//    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
//    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
//    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
//
//    @Resource(name = "redisTemplate")
//    private HashOperations<String, String, ChatRoom> hashOpsChatRoom;
//    @Resource(name = "redisTemplate")
//    private HashOperations<String, String, String> hashOpsEnterInfo;
//    @Resource(name = "redisTemplate")
//    private ValueOperations<String, String> valueOps;
//
//    // 모든 채팅방 조회
//    public List<ChatRoom> findAllRoom() {
//        return hashOpsChatRoom.values(CHAT_ROOMS);
//    }
//
//    // 특정 채팅방 조회
//    public ChatRoom findRoomById(String id) {
//        return hashOpsChatRoom.get(CHAT_ROOMS, id);
//    }
//
//    // 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
//    public ChatRoom createChatRoom(String name) {
//        ChatRoom chatRoom = ChatRoom.create(name);
//        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
//        return chatRoom;
//    }
//
//    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
//    public void setUserEnterInfo(String sessionId, String roomId) {
//        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
//    }
//
//    // 유저 세션으로 입장해 있는 채팅방 ID 조회
//    public String getUserEnterRoomId(String sessionId) {
//        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
//    }
//
//    // 유저 세션정보와 맵핑된 채팅방ID 삭제
//    public void removeUserEnterInfo(String sessionId) {
//        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
//    }
//
//    // 채팅방 유저수 조회
//    public long getUserCount(String roomId) {
//        return Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
//    }
//
//    // 채팅방에 입장한 유저수 +1
//    public long plusUserCount(String roomId) {
//        return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + roomId)).orElse(0L);
//    }
//
//    // 채팅방에 입장한 유저수 -1
//    public long minusUserCount(String roomId) {
//        return Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomId)).filter(count -> count > 0).orElse(0L);
//    }
//}

package com.company.usertradersback.repository;

import com.company.usertradersback.dto.chat.ChatRoom;
import com.company.usertradersback.entity.BoardEntity;
import com.company.usertradersback.entity.ChatRoomEntity;
import com.company.usertradersback.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ChatRoomRepository {
    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChatRoom> hashOpsChatRoom;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final ChatMessageJpaRepository chatMessageJpaRepository;

    // 모든 채팅방 조회
    public List<ChatRoom> findAllRoom() {
        return hashOpsChatRoom.values(CHAT_ROOMS);
    }

    // 모든 채팅방 조회
    public List<ChatRoom> findAllRoomUser(UserEntity sellUserId,UserEntity buyUserId){
        List<ChatRoomEntity> chatRoomEntityList = chatRoomJpaRepository.selectUserId(sellUserId.getId(),buyUserId.getId());
        System.out.println(chatRoomEntityList.size());

        if(chatRoomEntityList.size()>1){

            List<ChatRoom> results = chatRoomEntityList.stream().map(
                    chatRoomEntity -> {
                        System.out.println("@@@@@여기");
                        System.out.println(chatRoomEntity.getRoomId());
                        String message = "";
                        if(chatMessageJpaRepository.findTop1ByRoomIdOrderByCreateAtDescIdAsc(chatRoomEntity.getRoomId()) == null){
                            message = "";
                        }else{
                            message = chatMessageJpaRepository
                                    .findTop1ByRoomIdOrderByCreateAtDescIdAsc(chatRoomEntity.getRoomId()).getMessage();
                        }

                        ChatRoom chatRoom = ChatRoom.builder().build()
                                .convertEntityToDto(chatRoomEntity,message);
                        return chatRoom;
                    }
            ).collect(Collectors.toList());

            return results;
        }else {
            List<ChatRoom> resultsTwo = chatRoomEntityList.stream().map(
                    chatRoomEntity -> {
                     ChatRoom chatRoom = new ChatRoom();
                        return chatRoom;
                    }
            ).collect(Collectors.toList());

            return resultsTwo;
        }

    }

    // 특정 채팅방 조회
    public ChatRoom findRoomById(String id) {
        return hashOpsChatRoom.get(CHAT_ROOMS, id);
    }

    //방 생성시 방 목록을 저장
    @Transactional
    public String registerChatRoom(ChatRoom chatRoom, String name , UserEntity sell, UserEntity buy, BoardEntity board) {
        return chatRoomJpaRepository.save(
                ChatRoomEntity.builder()
                        .roomId(chatRoom.getRoomId())
                        .buyUserId(buy)
                        .sellUserId(sell)
                        .name(name)
                        .boardId(board)
                        .createAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .build()
        ).getRoomId();
    }

    // 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    // 유저 세션정보와 맵핑된 채팅방ID 삭제
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

    // 채팅방 유저수 조회
    public long getUserCount(String roomId) {
        return Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    // 채팅방에 입장한 유저수 +1
    public long plusUserCount(String roomId) {
        return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + roomId)).orElse(0L);
    }

    // 채팅방에 입장한 유저수 -1
    public long minusUserCount(String roomId) {
        return Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomId)).filter(count -> count > 0).orElse(0L);
    }
}
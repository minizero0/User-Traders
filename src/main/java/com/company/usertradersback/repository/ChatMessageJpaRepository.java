package com.company.usertradersback.repository;


import com.company.usertradersback.entity.ChatRoomMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ChatMessageJpaRepository extends JpaRepository<ChatRoomMessageEntity, Integer> {

    @Transactional
    @Query("select a from ChatRoomMessageEntity a where a.roomId = :roomId order by a.createAt desc,a.id asc ")
    List<ChatRoomMessageEntity> findByRoomId(String roomId);

    @Transactional
    @Query("select a.message from ChatRoomMessageEntity a where a.roomId = :roomId order by a.createAt desc ,a.id asc")
    String selectNewMessage(String roomId);

    @Transactional
    ChatRoomMessageEntity findTop1ByRoomIdOrderByCreateAtDescIdAsc(String roomId);
}


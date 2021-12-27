package com.company.usertradersback.repository;

import com.company.usertradersback.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoomEntity, Integer> {

   @Transactional
   @Query("select c from ChatRoomEntity c where c.sellUserId.id = :sellUserId or c.buyUserId.id = :buyUserId order by c.createAt desc, c.id asc ")
      List<ChatRoomEntity> selectUserId(Integer sellUserId , Integer buyUserId);



}

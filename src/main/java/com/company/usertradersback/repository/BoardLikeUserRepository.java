package com.company.usertradersback.repository;

import com.company.usertradersback.entity.BoardLikeUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface BoardLikeUserRepository extends JpaRepository<BoardLikeUserEntity,Integer> {

    @Transactional
    @Modifying
    @Query("delete from BoardLikeUserEntity blu where blu.userId.id = :userId and blu.boardId.id = :boardId ")
    Integer deleteById(Integer userId, Integer boardId);

    @Transactional
    @Query("select count(blu.id) from BoardLikeUserEntity blu where blu.userId.id = :userId and blu.boardId.id = :boardId")
    Integer exist(Integer userId, Integer boardId);

    @Transactional
    List<BoardLikeUserEntity> findAllByUserId_Id(Integer userId);

    @Transactional
    @Query("select count(bl) from BoardLikeUserEntity bl where bl.boardId.id = :boardId")
    Integer selectCountBoardId(Integer boardId);

    @Transactional
    @Query("select count(bl) from BoardLikeUserEntity bl where bl.userId.id =:userId")
    Integer selectCountUserId(Integer userId);

//    해당 유저와 게시물이 이미 좋아요인지 아닌지 존재여부를 체크하기 위한 jpql 인데,
//    exists 함수 사용불가로 querydsl에게 support를 받는다.
//    @Query("select exists(select blu.id from BoardLikeUserEntity blu where blu.userId  = :userId and blu.boardId = :boardId)as exist")
//    Integer existsById(Integer userId, Integer boardId);


}

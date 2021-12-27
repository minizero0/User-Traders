package com.company.usertradersback.repository;

import com.company.usertradersback.entity.BoardImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface BoardImageRepository extends JpaRepository<BoardImageEntity,Integer> {

    @Transactional
    @Query("select bi.path from BoardImageEntity bi " +
            "where bi.boardId.id = :boardId")
    List<String> findByPath(Integer boardId);

    @Transactional
    @Modifying
    @Query("delete from BoardImageEntity bi where bi.boardId.id = :boardId")
    void deleteAllByBoardId(Integer boardId);

    @Transactional
    List<BoardImageEntity> findAllByBoardId_Id(Integer boardId);

    //jpa는 limit 함수를 사용할 수 있지만 많은 조건을 쓰지 못하고
    //jpql은 limit 함수를 사용할 수 없다.
    //querydsl을 이용하여 극복 limit 함수 + 여러 조건식 가능
//    @Transactional
//    @Query("select bi.path from BoardImageEntity bi where bi.boardId.id = :boardId")
//    List<String> selectThumbnailPath(Integer boardId);
//
//    @Transactional
//    Optional<String> findFirstByPath(Integer boardId);


}

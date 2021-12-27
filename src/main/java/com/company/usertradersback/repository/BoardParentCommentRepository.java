package com.company.usertradersback.repository;

import com.company.usertradersback.entity.BoardParentCommentEntity;
import com.company.usertradersback.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface BoardParentCommentRepository extends JpaRepository<BoardParentCommentEntity,Integer> {

    @Transactional
    @Modifying
    @Query("delete from BoardParentCommentEntity bpc where bpc.userId = :user and bpc.id = :id")
    void deleteById(UserEntity user, Integer id);

    @Transactional
    @Query("select count(bpc.id) from BoardParentCommentEntity bpc where bpc.userId.id = :userId and bpc.id = :id")
    Integer exist(Integer userId, Integer id);

    @Transactional
    @Query("select count(bcc.pcommentId) from BoardParentCommentEntity bpc left join BoardChildCommentEntity bcc on bpc.id = bcc.pcommentId.id " +
            "where bpc.boardId.id = :boardId")
    Integer selectCountBoardIdBCC(Integer boardId);

    @Transactional
    @Query("select count(bpc.id) from BoardParentCommentEntity bpc where bpc.boardId.id = :boardId")
    Integer selectCountBoardIdBPC(Integer boardId);

    @Transactional
    @Query("select bpc from BoardParentCommentEntity bpc where bpc.boardId.id = :boardId ORDER BY bpc.createAt desc")
    List<BoardParentCommentEntity> findAllByBoardId_Id(Integer boardId);
}

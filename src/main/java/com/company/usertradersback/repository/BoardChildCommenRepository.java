package com.company.usertradersback.repository;

import com.company.usertradersback.entity.BoardChildCommentEntity;
import com.company.usertradersback.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface BoardChildCommenRepository extends JpaRepository<BoardChildCommentEntity,Integer> {
    @Transactional
    @Modifying
    @Query("delete from BoardChildCommentEntity bcc where bcc.userId = :user and bcc.id = :id")
    void deleteById(UserEntity user, Integer id);

    @Transactional
    @Query("select count(bcc.id) from BoardChildCommentEntity bcc where bcc.userId.id = :userId and bcc.id = :id")
    Integer exist(Integer userId, Integer id);

    @Transactional
    @Query("select bcc from BoardChildCommentEntity bcc where bcc.pcommentId.id = :pcommentId ORDER BY bcc.createAt desc")
    List<BoardChildCommentEntity> findAllByPcommentId_Id(Integer pcommentId);

}

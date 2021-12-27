package com.company.usertradersback.repository;

import com.company.usertradersback.entity.BoardDeclarationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface BoardDeclaraionRepository extends JpaRepository<BoardDeclarationEntity,Integer> {

    @Transactional
    @Query("select count(bd.id) from BoardDeclarationEntity bd where bd.userId.id = :userId and bd.boardId.id = :boardId")
    Integer exist(Integer userId, Integer boardId);
}

package com.company.usertradersback.repository;

import com.company.usertradersback.entity.BoardCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface BoardCategoryRepository extends JpaRepository<BoardCategoryEntity, Integer> {

    @Transactional
    List<BoardCategoryEntity> findAllBySubCategoryId_Id(Integer subCategoryId);
}

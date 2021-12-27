package com.company.usertradersback.repository;

import com.company.usertradersback.entity.BoardSubCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardSubCategoryRepository extends JpaRepository<BoardSubCategoryEntity,Integer> {
}

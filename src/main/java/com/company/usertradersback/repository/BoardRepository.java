package com.company.usertradersback.repository;

import com.company.usertradersback.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity,Integer>  {

    @Transactional
    List<BoardEntity> findByTitleContaining(String title);

    @Transactional
    @Query("select b from BoardEntity b " +
            "left join b.categoryId ci on b.categoryId.id = ci.id" +
            " left join ci.subCategoryId sci on ci.subCategoryId.id = sci.id where ci.id = :categoryId and sci.id= :subCategoryId")
    List<BoardEntity> selectAll(Integer categoryId,Integer subCategoryId);

    @Transactional
    @Query("select b from BoardEntity b left join b.userId u on b.userId.id = u.id where u.id= :userId ORDER BY b.createAt desc ")
    List<BoardEntity> findAllByUserId_Id(Integer userId);

    @Transactional
    @Modifying
    @Query("update BoardEntity b set b.views = b.views + 1 where b.id = :boardId")
    void saveViews(Integer boardId);
}

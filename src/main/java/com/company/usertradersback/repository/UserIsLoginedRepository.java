package com.company.usertradersback.repository;

import com.company.usertradersback.entity.UserIsLoginedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface UserIsLoginedRepository extends JpaRepository<UserIsLoginedEntity, Integer> {

    @Transactional
    @Query("select count(u.id) from UserIsLoginedEntity u where u.id = :id")
    Integer checkId(Integer id);

    @Transactional
    @Modifying
    @Query("update UserIsLoginedEntity u " +
            "set u.logoutAt = :logoutAt ,u.loginAt = :now, u.status=1 " +
            "where u.id = :id")
    void updateLoginAt(LocalDateTime logoutAt, LocalDateTime now,Integer id);

    @Transactional
    @Modifying
    @Query("update UserIsLoginedEntity u " +
            "set u.logoutAt = :now ,u.loginAt = :loginAt, u.status=0 " +
            "where u.id = :id")
    void updateLogoutAt(LocalDateTime now,LocalDateTime loginAt,Integer id);


    @Transactional
    @Query("select u.logoutAt from UserIsLoginedEntity u where u.id = :id")
    LocalDateTime findByLogoutAt(Integer id);

    @Transactional
    @Query("select u.loginAt from UserIsLoginedEntity u where u.id = :id")
    LocalDateTime findByLoginAt(Integer id);
}
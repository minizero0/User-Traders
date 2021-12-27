package com.company.usertradersback.repository;

import com.company.usertradersback.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    @Transactional
    Optional<UserEntity> findByEmail(String email);

    @Transactional
    @Query("select u.id from UserEntity u where u.email = :email")
    Integer findIdByEmail(String email);

    @Transactional
    @Query("select count(u.email) " +
            "from UserEntity u where u.email = :email" )
    Integer selectEmailCount(String email);

    @Transactional
    @Query("select count(u.nickname) " +
            "from UserEntity u where u.nickname = :nickname" )
    Integer selectNicknameCount(String nickname);
}
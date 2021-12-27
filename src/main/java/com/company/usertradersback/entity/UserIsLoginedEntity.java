package com.company.usertradersback.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "UserIsLogined")
public class UserIsLoginedEntity {
    @Id
    @Column(name = "id")
    private Integer id;

    //0: 로그아웃, 1:로그인
    @Column(name = "status")
    private Integer status;

    @Column(name = "loginAt")
    private LocalDateTime loginAt;

    @Column(name = "logoutAt")
    private LocalDateTime logoutAt;

    @Builder UserIsLoginedEntity(Integer id, Integer status
            ,LocalDateTime loginAt , LocalDateTime logoutAt){
        this.id = id;
        this.status = status;
        this.loginAt = loginAt;
        this.logoutAt = logoutAt;
    }
}

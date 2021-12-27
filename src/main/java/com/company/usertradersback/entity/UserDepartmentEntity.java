package com.company.usertradersback.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "UserDepartment")
public class UserDepartmentEntity {

    @Id
    @Column(name = "id")
    // 학과 고유 번호
    private Integer id;

    @Column(name = "name")
    // 학과명
    private String name;

    @Builder
    public UserDepartmentEntity(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

}
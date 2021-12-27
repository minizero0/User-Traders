package com.company.usertradersback.repository;

import com.company.usertradersback.entity.UserDepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDepartmentRepository extends JpaRepository<UserDepartmentEntity,Integer> {
}

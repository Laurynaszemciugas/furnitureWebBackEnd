package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByGmail(String username);
    boolean existsByGmail(String username);


}
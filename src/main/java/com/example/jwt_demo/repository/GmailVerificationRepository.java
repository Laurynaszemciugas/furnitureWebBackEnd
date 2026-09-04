package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.Authenfication.GmailAuth;
import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GmailVerificationRepository extends JpaRepository<GmailAuth,Long> {


    boolean existsByOneTimeCode(String code);
    GmailAuth findByOneTimeCode(String code);

}

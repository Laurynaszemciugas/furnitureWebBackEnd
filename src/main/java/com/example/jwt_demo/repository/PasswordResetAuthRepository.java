package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.Authenfication.PasswordResetAuth;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PasswordResetAuthRepository extends JpaRepository<PasswordResetAuth, Long> {

    @Modifying
    @Transactional
    @Query("""
        
        DELETE FROM PasswordResetAuth pra
        WHERE pra.user.id = :userId
        
""")
    void deleteAllPreviousCodes(Long userId);


    @Query("""
        
        Select pra from PasswordResetAuth pra
        where pra.user.id = :userId
        
""")
    PasswordResetAuth getUserCode(Long userId);




}

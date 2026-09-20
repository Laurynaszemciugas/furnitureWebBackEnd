package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.WorkDay;
import com.example.jwt_demo.Entity.WorkDone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface WorkDoneRepository extends JpaRepository<WorkDone,Long> {









}

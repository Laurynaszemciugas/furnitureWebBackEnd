package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.ActionLogs.ActionLogFeed;
import com.example.jwt_demo.Entity.ActionTracker;
import com.example.jwt_demo.Entity.ProductJoin.ProductFinishSteps;
import com.example.jwt_demo.Enums.ActionDesciptionEnum;
import com.example.jwt_demo.Enums.ActionTrackerEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductFinishStepsRepository extends JpaRepository<ProductFinishSteps,Long> {





}

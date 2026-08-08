package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.CustomReport.CustomReportFeed;
import com.example.jwt_demo.Entity.ActionTracker;
import com.example.jwt_demo.Entity.CreateReport.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActionTrackerRepository extends JpaRepository<ActionTracker,Long> {





}

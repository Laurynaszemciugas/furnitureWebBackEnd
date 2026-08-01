package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.CreateReport.Report;
import com.example.jwt_demo.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomReportRepository extends JpaRepository<Report,Long> {





}

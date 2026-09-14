package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.CreateReport.Report;
import com.example.jwt_demo.Entity.EmployeeJoin.EmployeeActiveOrders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeActiveOrdersRepository extends JpaRepository<EmployeeActiveOrders,Long> {
}

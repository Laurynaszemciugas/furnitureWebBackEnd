package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.Order.OrdersFeedData;
import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Entity.WorkDay;
import com.example.jwt_demo.Enums.ActiveInactive;
import com.example.jwt_demo.Enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkDayRepository extends JpaRepository<WorkDay,Long> {


    @Query(value = """
    SELECT COALESCE(MAX(IF(wd.work_day_end IS NULL, 1, 0)), 0)
    FROM bpfurniture.work_day wd
    WHERE wd.employee_id = :id
    """, nativeQuery = true)
    Long doesEmployeeAlreadyStartedWork(
            @Param("id") Long id
    );

    @Query("""
      SELECT wd
      FROM WorkDay wd
      where wd.employee.id = :id and wd.workDayEnd IS NULL
    """)
    WorkDay getWorkDayInfo(
            @Param("id") Long id
    );

    @Query(value = """
    SELECT COALESCE(MAX(IF(wd.work_day_end , 1, 0)), 0)
    FROM bpfurniture.work_day wd
    WHERE wd.employee_id = :id and wd.work_day_created >= :dateFrom and wd.work_day_created <= :dateTo
    """, nativeQuery = true)
    Long doesEmployeeHadWorkDayToday(
            @Param("id") Long id,
            LocalDateTime dateFrom,
            LocalDateTime dateTo
    );







}

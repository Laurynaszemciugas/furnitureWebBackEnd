package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.WorkDay.WorkDayMiniStats;
import com.example.jwt_demo.Entity.WorkDay;
import com.example.jwt_demo.Entity.WorkDone;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface WorkDoneRepository extends JpaRepository<WorkDone,Long> {



    @Query(value = """

    SELECT * FROM bpfurniture.work_done wd where wd.work_day_id = :workDayId ;


""", nativeQuery = true)
    List<WorkDone> allInfoAboutSpecificWorkDay(Long workDayId);


    // =====================================================================================
    // Employee page quick actions
    //=======================================================================================
    @Query("""
        SELECT new com.example.jwt_demo.DTOS.WorkDay.WorkDayMiniStats(
            (SELECT COALESCE(SUM(workedForMinutes), 0)
             FROM WorkDay),
        
            (SELECT COALESCE(COUNT(*),0)
             FROM WorkDay),
        
            (SELECT COALESCE(COUNT(*), 0)
             FROM WorkDone),
        
             (SELECT COALESCE(avg(wd.workedForMinutes),0)
             FROM WorkDay wd))
        
             
""")
    WorkDayMiniStats workDayMini(Long employeeId);



    @Query("""
    SELECT wd
    FROM WorkDay wd
    WHERE wd.employee.id = :employeeId
      AND wd.workDayCreated >= :from
      AND wd.workDayCreated <= :to
    """)
    List<WorkDay> allInfoAccordingToEmployee(
            @Param("employeeId") Long employeeId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );


    @Query("""
     Select CASE
        WHEN COUNT(DISTINCT wd.id) = 0
        THEN 1
        ELSE CEIL(COUNT(DISTINCT wd.id) / 10.0)
    END
    FROM WorkDone wd
    WHERE wd.employee.id = :employeeId
      AND wd.started >= :from
      AND wd.started <= :to
    """)
    Long getTotalPagesInQuickActions(
            @Param("employeeId") Long employeeId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );





}

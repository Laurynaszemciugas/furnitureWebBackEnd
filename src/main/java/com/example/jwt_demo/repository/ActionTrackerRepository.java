package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.ActionLogs.ActionLogFeed;
import com.example.jwt_demo.DTOS.CustomReport.CustomReportFeed;
import com.example.jwt_demo.DTOS.Employees.EmployeeBriefDto;
import com.example.jwt_demo.Entity.ActionTracker;
import com.example.jwt_demo.Entity.CreateReport.Report;
import com.example.jwt_demo.Enums.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ActionTrackerRepository extends JpaRepository<ActionTracker,Long> {


    @Query("""
SELECT new com.example.jwt_demo.DTOS.ActionLogs.ActionLogFeed(

    e.actionName,
    e.whoMadeIt,
    e.typeOfActionRecorded,
    e.action,
    e.created
  
)
FROM ActionTracker e
WHERE   e.user.id = :id 
 and (:prompt IS NULL OR e.actionName = :prompt)
  AND (:actionTrackerEnum IS NULL OR e.typeOfActionRecorded = :actionTrackerEnum)
  AND (:actionDesciptionEnum IS NULL OR e.action = :actionDesciptionEnum)
  AND (:dateFrom IS NULL OR e.created >= :dateFrom)
  AND (:dateTo IS NULL OR e.created <= :dateTo)
 
""")
    List<ActionLogFeed> getActionLogFeed(
            String prompt,
            ActionTrackerEnum actionTrackerEnum,
            ActionDesciptionEnum actionDesciptionEnum,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            Pageable pageable,
            Long id
    );


    @Query("""
SELECT
    CASE
        WHEN COUNT(e.id) = 0 THEN 1
        ELSE CEIL(COUNT(e.id) / :pageCount)
    END
FROM ActionTracker e
WHERE e.user.id = :id
 and (:prompt IS NULL OR e.actionName = :prompt)
AND (:actionTrackerEnum IS NULL OR e.typeOfActionRecorded = :actionTrackerEnum)
  AND (:actionDesciptionEnum IS NULL OR e.action = :actionDesciptionEnum)
  AND (:dateFrom IS NULL OR e.created >= :dateFrom)
  AND (:dateTo IS NULL OR e.created <= :dateTo)
  
  
""")
    Long getNumberOfOrderPages(
            String prompt,
            ActionTrackerEnum actionTrackerEnum,
            ActionDesciptionEnum actionDesciptionEnum,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            double pageCount,
            Long id
    );



}

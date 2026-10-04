package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.Common.ReportMiniStatHolder;
import com.example.jwt_demo.DTOS.DashBoard.ActivityFeedModel;
import com.example.jwt_demo.DTOS.DashBoard.DashBoardEmployeeMiniInfo;
import com.example.jwt_demo.DTOS.DashBoard.TopEmployeesModel;
import com.example.jwt_demo.DTOS.Employees.EmployeeBriefDto;
import com.example.jwt_demo.DTOS.Material.MaterialBriefDto;
import com.example.jwt_demo.DTOS.Order.ComboBoxEmployees;
import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {




    @Query("""
    SELECT e.id
    FROM Employee e
    WHERE e.empId.id = :userId
""")
    Long employeeId(@Param("userId") Long userId);






    // add user id check
    @Query("""

    SELECT new com.example.jwt_demo.DTOS.Order.ComboBoxEmployees(e.id, e.fullName, e.employeeCategory, e.profileImage) FROM Employee e
    
    where e.user.id = :id

""")
    List<ComboBoxEmployees> getUserEmployees(Long id);

    @Query("""

            SELECT new com.example.jwt_demo.DTOS.Common.MiniStatHolder(
            count(e.id),
            SUM(CASE WHEN e.employeeAcIn = 'ACTIVE' THEN 1 ELSE 0 END),
            SUM(CASE WHEN e.employeeAcIn = 'INACTIVE' THEN 1 ELSE 0 END),
            SUM(CASE WHEN e.created >= :fromDate AND e.created <= :toDate THEN 1 ELSE 0 END))
         
            FROM Employee e
            
            where e.user.id = :id


""")
    MiniStatHolder getEmployeeMiniStats(@Param("fromDate") LocalDateTime fromDate, @Param("toDate")LocalDateTime toDate, Long id);


    @Query("""
SELECT new com.example.jwt_demo.DTOS.Employees.EmployeeBriefDto(

    e.id,
    e.profileImage,
    e.fullName,
    e.gmail,
    e.employeeAcIn,
    e.employeeCategory,
    e.employeeDepartment,
    e.hourlyRate,
    e.created
)
FROM Employee e
WHERE   e.user.id = :id 
 and (:employeeAcInChoice IS NULL OR e.employeeAcIn = :employeeAcInChoice)
  AND (:employeeCategoryChoice IS NULL OR e.employeeCategory = :employeeCategoryChoice)
  AND (:employeeDepartmentChoice IS NULL OR e.employeeDepartment = :employeeDepartmentChoice)
  AND (:hourlyRateChoice IS NULL OR e.hourlyRate = :hourlyRateChoice)
  AND (:fromJoinedChoice IS NULL OR e.created >= :fromJoinedChoice)
  AND (:toJoinedChoice IS NULL OR e.created <= :toJoinedChoice)
  AND (
        :promtChoice IS NULL OR
        LOWER(e.name) LIKE LOWER(CONCAT('%', :promtChoice, '%')) OR
        LOWER(e.lastName) LIKE LOWER(CONCAT('%', :promtChoice, '%')) OR
        LOWER(e.fullName) LIKE LOWER(CONCAT('%', :promtChoice, '%')) OR
        LOWER(e.gmail) LIKE LOWER(CONCAT('%', :promtChoice, '%')) OR
        LOWER(CONCAT(e.name, ' ', e.lastName, ' ', e.gmail))
            LIKE LOWER(CONCAT('%', :promtChoice, '%'))
      )
""")
    List<EmployeeBriefDto> getExistingEmployeeDataForFeed(
            @Param("employeeAcInChoice") EmployeeAcIn employeeAcInChoice,
            @Param("employeeCategoryChoice") EmployeeRole employeeCategoryChoice,
            @Param("employeeDepartmentChoice") EmployeeDepartment employeeDepartmentChoice,
            @Param("hourlyRateChoice") Double hourlyRateChoice,
            @Param("fromJoinedChoice") LocalDateTime fromJoinedChoice,
            @Param("toJoinedChoice") LocalDateTime toJoinedChoice,
            @Param("promtChoice") String promtChoice,
            Pageable pageable,
            Long id
    );


    @Query("""
SELECT    CASE
        WHEN COUNT(DISTINCT e.id) = 0
        THEN 1
        ELSE CEIL(COUNT(DISTINCT e.id) / 5.0)
    END
FROM Employee e
WHERE  e.user.id = :id
  AND (:employeeAcInChoice IS NULL OR e.employeeAcIn = :employeeAcInChoice)
  AND (:employeeCategoryChoice IS NULL OR e.employeeCategory = :employeeCategoryChoice)
  AND (:employeeDepartmentChoice IS NULL OR e.employeeDepartment = :employeeDepartmentChoice)
  AND (:hourlyRateChoice IS NULL OR e.hourlyRate = :hourlyRateChoice)
  AND (:fromJoinedChoice IS NULL OR e.created >= :fromJoinedChoice)
  AND (:toJoinedChoice IS NULL OR e.created <= :toJoinedChoice)
  AND (
        :promtChoice IS NULL OR
        LOWER(e.name) LIKE LOWER(CONCAT('%', :promtChoice, '%')) OR
        LOWER(e.lastName) LIKE LOWER(CONCAT('%', :promtChoice, '%')) OR
        LOWER(e.fullName) LIKE LOWER(CONCAT('%', :promtChoice, '%')) OR
        LOWER(e.gmail) LIKE LOWER(CONCAT('%', :promtChoice, '%')) OR
        LOWER(CONCAT(e.name, ' ', e.lastName, ' ', e.gmail))
            LIKE LOWER(CONCAT('%', :promtChoice, '%'))
      )
""")
    Long getTotalPages(
            @Param("employeeAcInChoice") EmployeeAcIn employeeAcInChoice,
            @Param("employeeCategoryChoice") EmployeeRole employeeCategoryChoice,
            @Param("employeeDepartmentChoice") EmployeeDepartment employeeDepartmentChoice,
            @Param("hourlyRateChoice") Double hourlyRateChoice,
            @Param("fromJoinedChoice") LocalDateTime fromJoinedChoice,
            @Param("toJoinedChoice") LocalDateTime toJoinedChoice,
            @Param("promtChoice") String promtChoice,
            Long id
    );

    // dashboard



    @Query("""
    SELECT new com.example.jwt_demo.DTOS.DashBoard.DashBoardEmployeeMiniInfo(
        e.fullName,
        COUNT(oe.employee.id)
    )
    FROM Employee e
    JOIN OrderEmployees oe
        ON oe.employee.id = e.id
    JOIN Orders o
        ON o.id = oe.order.id
           
   WHERE e.user.id = :id
      and o.orderStatus = 'Finished'
     and o.created >= :fromDate AND o.created <= :toDate

    GROUP BY e.id, e.fullName
    ORDER BY COUNT(oe.employee.id) DESC

    LIMIT 1
         
            
    """)
    Optional<DashBoardEmployeeMiniInfo> getEmployeeMiniStat(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("id") Long id
    );

    @Query("""
    SELECT new com.example.jwt_demo.DTOS.DashBoard.TopEmployeesModel(
        s.profileImage,
        s.fullName,
        s.productsFinished,
        s.hourlyRate
    )
    FROM Employee s
    WHERE s.user.id = :userId
    ORDER BY s.productsFinished DESC
""")
    List<TopEmployeesModel> getTopEmployeesModel(
            @Param("userId") Long userId,
            Pageable pageable
    );


    // updating stuff



    // graphs

    @Query("""
    SELECT new com.example.jwt_demo.DTOS.Common.ReportMiniStatHolder(

        /* =====================================================
           1. TOTAL EMPLOYEES - CURRENT
           ===================================================== */

        COUNT(DISTINCT e.id),

        /* =====================================================
           2. TOTAL EMPLOYEES - PREVIOUS
           ===================================================== */

        COUNT(DISTINCT e.id),

        /* =====================================================
           3. AVERAGE HOURS WORKED - CURRENT
           ===================================================== */

        COALESCE(
            (
                SELECT AVG(wd2.workedForMinutes) / 60.0
                FROM WorkDay wd2
                WHERE wd2.employee.user.id = :id
                  AND wd2.workDayCreated >= :currentFrom
                  AND wd2.workDayCreated <= :currentTo
            ),
            0.0
        ),

        /* =====================================================
           4. AVERAGE HOURS WORKED - PREVIOUS
           ===================================================== */

        COALESCE(
            (
                SELECT AVG(wd3.workedForMinutes) / 60.0
                FROM WorkDay wd3
                WHERE wd3.employee.user.id = :id
                  AND wd3.workDayCreated >= :previousFrom
                  AND wd3.workDayCreated <= :previousTo
            ),
            0.0
        ),

        /* =====================================================
           5. LABOR COST - CURRENT
           ===================================================== */

        COALESCE(
            (
                SELECT SUM(
                    (wd4.workedForMinutes / 60.0)
                    * wd4.employee.hourlyRate
                )
                FROM WorkDay wd4
                WHERE wd4.employee.user.id = :id
                  AND wd4.workDayCreated >= :currentFrom
                  AND wd4.workDayCreated <= :currentTo
            ),
            0.0
        ),

        /* =====================================================
           6. LABOR COST - PREVIOUS
           ===================================================== */

        COALESCE(
            (
                SELECT SUM(
                    (wd5.workedForMinutes / 60.0)
                    * wd5.employee.hourlyRate
                )
                FROM WorkDay wd5
                WHERE wd5.employee.user.id = :id
                  AND wd5.workDayCreated >= :previousFrom
                  AND wd5.workDayCreated <= :previousTo
            ),
            0.0
        ),

        /* =====================================================
           7. TOP EMPLOYEE - CURRENT
           ===================================================== */

        (
            SELECT e2.fullName
            FROM WorkDone wd6
            JOIN wd6.employee e2
            JOIN wd6.order o2
            WHERE e2.user.id = :id
              AND wd6.started >= :currentFrom
              AND wd6.started <= :currentTo
              AND o2.orderStatus = 'Finished'
            GROUP BY e2.id, e2.fullName
            ORDER BY COUNT(DISTINCT o2.id) DESC
            LIMIT 1
        ),

        /* =====================================================
           8. TOP EMPLOYEE - PREVIOUS
           ===================================================== */

        (
            SELECT e3.fullName
            FROM WorkDone wd7
            JOIN wd7.employee e3
            JOIN wd7.order o3
            WHERE e3.user.id = :id
              AND wd7.started >= :previousFrom
              AND wd7.started <= :previousTo
              AND o3.orderStatus = 'Finished'
            GROUP BY e3.id, e3.fullName
            ORDER BY COUNT(DISTINCT o3.id) DESC
            LIMIT 1
        )

    )
    FROM Employee e
    WHERE e.user.id = :id
""")
    ReportMiniStatHolder getEmployeeReportMiniStats(
            @Param("currentFrom") LocalDateTime currentFrom,
            @Param("currentTo") LocalDateTime currentTo,
            @Param("previousFrom") LocalDateTime previousFrom,
            @Param("previousTo") LocalDateTime previousTo,
            @Param("id") Long id
    );


}

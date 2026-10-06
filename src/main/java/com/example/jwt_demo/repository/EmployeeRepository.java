package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.Common.GraphDataLongValue;
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

    @Query(value = """
    SELECT

        /* =====================================================
           1. TOTAL EMPLOYEES - THIS MONTH
           ===================================================== */
        (
            SELECT COUNT(DISTINCT e1.id)
            FROM employee e1
            WHERE e1.user_id = :id
              AND e1.created <= :currentTo
        ),

        /* =====================================================
           2. TOTAL EMPLOYEES - LAST MONTH
           ===================================================== */
        (
            SELECT COUNT(DISTINCT e2.id)
            FROM employee e2
            WHERE e2.user_id = :id
              AND e2.created <= :previousTo
        ),

        /* =====================================================
           3. TOP EMPLOYEE - THIS MONTH
           Employee with most FINISHED orders
           ===================================================== */
        (
        SELECT e.full_name
        
        FROM employee e
        
        order by e.products_finished desc
        Limit 1
        ),

        /* =====================================================
           4. TOP EMPLOYEE - LAST MONTH
           ===================================================== */
        (
            SELECT e.products_finished
                   
                   FROM bpfurniture.employee e
                   
                   order by e.products_finished desc
                   Limit 1
        ),

        /* =====================================================
           5. AVERAGE HOURS - THIS MONTH
           ===================================================== */
        COALESCE(
            (
                SELECT ROUND(avg(wd.worked_for_minutes) / 60,2) FROM work_day wd
                WHERE wd.user_id = :id
                 AND wd.work_day_created >= :currentFrom
                  AND wd.work_day_created <= :currentTo
            ),
            0.0
        ),

        /* =====================================================
           6. AVERAGE HOURS - LAST MONTH
           ===================================================== */
               
                   
           COALESCE(
            (
                SELECT ROUND(avg(wd.worked_for_minutes) / 60,2) FROM work_day wd
                WHERE wd.user_id = :id
                 AND wd.work_day_created >= :previousFrom
                  AND wd.work_day_created <= :previousTo
            ),
            0.0
        ),             
                           

        /* =====================================================
           7. LABOR COST - THIS MONTH
           ===================================================== */
        COALESCE(
            (
                SELECT ROUND(SUM(
                    (wd3.worked_for_minutes / 60.0)
                    * e7.hourly_rate
                ),2)
                FROM work_day wd3
                JOIN employee e7
                    ON e7.id = wd3.employee_id
                WHERE e7.user_id = :id
                  AND wd3.work_day_created >= :currentFrom
                  AND wd3.work_day_created <= :currentTo
            ),
            0.0
        ),

        /* =====================================================
           8. LABOR COST - LAST MONTH
           ===================================================== */
        COALESCE(
            (
                SELECT SUM(
                    (wd4.worked_for_minutes / 60.0)
                    * e8.hourly_rate
                )
                FROM work_day wd4
                JOIN employee e8
                    ON e8.id = wd4.employee_id
                WHERE e8.user_id = :id
                  AND wd4.work_day_created >= :previousFrom
                  AND wd4.work_day_created <= :previousTo
            ),
            0.0
        )

    """,
            nativeQuery = true)
    List<Object[]> getEmployeeReportMiniStats(
            @Param("currentFrom") LocalDateTime currentFrom,
            @Param("currentTo") LocalDateTime currentTo,
            @Param("previousFrom") LocalDateTime previousFrom,
            @Param("previousTo") LocalDateTime previousTo,
            @Param("id") Long id
    );




    @Query("""
    SELECT
        SUM(e.productsFinished),
        e.employeeCategory
    FROM Employee e
    WHERE e.user.id = :id
      AND e.created >= :currentFrom
      AND e.created <= :currentTo
    GROUP BY e.employeeCategory
    ORDER BY SUM(e.productsFinished) DESC
""")
    List<Object[]> employeeEachCategoryValues(
            @Param("currentFrom") LocalDateTime currentFrom,
            @Param("currentTo") LocalDateTime currentTo,
            @Param("id") Long id
    );



}

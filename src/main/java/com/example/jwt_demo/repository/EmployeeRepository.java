package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.Employees.EmployeeBriefDto;
import com.example.jwt_demo.DTOS.Material.MaterialBriefDto;
import com.example.jwt_demo.DTOS.Order.ComboBoxEmployees;
import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Enums.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {


    // add user id check
    @Query("""

    SELECT new com.example.jwt_demo.DTOS.Order.ComboBoxEmployees(e.id, e.fullName, e.employeeCategory, e.profileImage) FROM Employee e

""")
    List<ComboBoxEmployees> getUserEmployees();

    @Query("""

            SELECT new com.example.jwt_demo.DTOS.Common.MiniStatHolder(
            count(e.id),
            SUM(CASE WHEN e.employeeAcIn = 'ACTIVE' THEN 1 ELSE 0 END),
            SUM(CASE WHEN e.employeeAcIn = 'INACTIVE' THEN 1 ELSE 0 END),
            SUM(CASE WHEN e.created >= :fromDate AND e.created <= :toDate THEN 1 ELSE 0 END))
         
            FROM Employee e


""")
    MiniStatHolder getEmployeeMiniStats(@Param("fromDate") LocalDateTime fromDate, @Param("toDate")LocalDateTime toDate);


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
WHERE (:employeeAcInChoice IS NULL OR e.employeeAcIn = :employeeAcInChoice)
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
            Pageable pageable
    );


    @Query("""
SELECT    CASE
        WHEN COUNT(DISTINCT e.id) = 0
        THEN 1
        ELSE CEIL(COUNT(DISTINCT e.id) / 5.0)
    END
FROM Employee e
WHERE (:employeeAcInChoice IS NULL OR e.employeeAcIn = :employeeAcInChoice)
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
            @Param("promtChoice") String promtChoice
    );


}

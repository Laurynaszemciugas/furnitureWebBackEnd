package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.Order.ComboBoxEmployees;
import com.example.jwt_demo.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {


    // add user id check
    @Query("""

    SELECT new com.example.jwt_demo.DTOS.Order.ComboBoxEmployees(e.id, e.fullName, e.employeeCategory) FROM Employee e

""")
    List<ComboBoxEmployees> getUserEmployees();


}

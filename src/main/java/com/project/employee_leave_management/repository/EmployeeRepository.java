package com.project.employee_leave_management.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.project.employee_leave_management.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}

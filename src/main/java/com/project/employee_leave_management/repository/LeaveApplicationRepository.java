package com.project.employee_leave_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.employee_leave_management.entity.LeaveApplication;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Integer> {

	List<LeaveApplication> findByEmployee_EmployeeId(int employeeId);
	
}

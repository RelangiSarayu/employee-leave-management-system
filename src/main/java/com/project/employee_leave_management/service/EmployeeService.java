package com.project.employee_leave_management.service;
import org.springframework.stereotype.Service;
import com.project.employee_leave_management.exception.*; 
import com.project.employee_leave_management.entity.Employee;
import com.project.employee_leave_management.entity.LeaveApplication;
import com.project.employee_leave_management.repository.EmployeeRepository;
import com.project.employee_leave_management.repository.LeaveApplicationRepository;
import java.util.List;
@Service
public class EmployeeService {
	
	private final EmployeeRepository employeeRepository;
	private final LeaveApplicationRepository leaveApplicationRepository;
	
	public EmployeeService(EmployeeRepository employeeRepository,LeaveApplicationRepository leaveApplicationRepository) {
		this.employeeRepository = employeeRepository;
	    this.leaveApplicationRepository = leaveApplicationRepository;
	}
	
	public Employee addEmployee(Employee employee) {
	    if (employeeRepository.existsById(employee.getEmployeeId())) {
	    	throw new BusinessException("Employee ID already exists");
	    }
	    return employeeRepository.save(employee);
	}
	
	public List<Employee> getAllEmployees(){
		return employeeRepository.findAll();
	}
	
	public Employee findEmployeeById(int employeeId){
		return employeeRepository.findById(employeeId).orElseThrow(()-> new ResourceNotFoundException("Employee not Found"));
	}
	
	public String deleteEmployeeById(int employeeId) {
	    Employee emp = findEmployeeById(employeeId);
	    List<LeaveApplication> list = leaveApplicationRepository.findByEmployee_EmployeeId(employeeId);
	    if (list.isEmpty()) {
	        employeeRepository.delete(emp);
	        return "Employee deleted successfully";
	    } else {
	        throw new BusinessException("Cannot delete employee because leave applications exist");
	    }
	}
	
}

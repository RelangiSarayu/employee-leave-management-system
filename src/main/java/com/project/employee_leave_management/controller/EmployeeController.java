package com.project.employee_leave_management.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.project.employee_leave_management.service.EmployeeService;
import com.project.employee_leave_management.entity.Employee;

@RestController
public class EmployeeController {
	private final EmployeeService employeeService;
	
	public EmployeeController(EmployeeService employeeService) {
	    this.employeeService = employeeService;
	}
	
	@PostMapping("/employees")
	public Employee addEmployee(@Valid @RequestBody Employee employee) {
		return employeeService.addEmployee(employee);
	}
	
	@GetMapping("/employees")
	public List<Employee> getAllEmployees(){
		return employeeService.getAllEmployees();
	}
	
	@GetMapping("/employees/{employeeId}")
	public Employee findEmployee(@PathVariable int employeeId){
		return employeeService.findEmployeeById(employeeId);
	}
	
	@DeleteMapping("/employees/{employeeId}")
	public String deleteEmployeeById(@PathVariable int employeeId) {
	    return employeeService.deleteEmployeeById(employeeId);
	}
}

package com.project.employee_leave_management;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.project.employee_leave_management.entity.Employee;
import com.project.employee_leave_management.entity.LeaveApplication;
import com.project.employee_leave_management.exception.BusinessException;
import com.project.employee_leave_management.exception.ResourceNotFoundException;
import com.project.employee_leave_management.repository.EmployeeRepository;
import com.project.employee_leave_management.repository.LeaveApplicationRepository;
import com.project.employee_leave_management.service.EmployeeService;

public class EmployeeServiceTest {
	
	@Mock
	private EmployeeRepository employeeRepository;
	
	@Mock
	private LeaveApplicationRepository leaveRepository;
	
	@InjectMocks
	private EmployeeService employeeService;
	
	@BeforeEach
	void SetUp(){
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void shouldAddEmployeeSuccessfully() {
		Employee employee = new Employee();
		employee.setEmployeeId(101);
		
		when(employeeRepository.existsById(101)).thenReturn(false);
		when(employeeRepository.save(employee)).thenReturn(employee);
		
		Employee result = employeeService.addEmployee(employee);
		
		assertNotNull(result);
		assertEquals(101, result.getEmployeeId());
		verify(employeeRepository).save(employee);
		
	}
	
	@Test
	void shouldRejectDuplicateEmployeeId() {
		Employee employee = new Employee();
		employee.setEmployeeId(101);
		
		when(employeeRepository.existsById(101))
			.thenReturn(true);
		
		assertThrows(BusinessException.class, 
				() -> employeeService.addEmployee(employee));
		
		verify(employeeRepository, never()).save(any(Employee.class));
	}
	
	@Test
	void shouldReturnAllEmployee() {
		Employee employee1 = new Employee();
		employee1.setEmployeeId(101);
		
		Employee employee2 = new Employee();
		employee2.setEmployeeId(102);
		
		when(employeeRepository.findAll()).thenReturn(List.of(employee1, employee2));
		
		List<Employee> result = employeeService.getAllEmployees();
		
		assertEquals(2, result.size());
		verify(employeeRepository).findAll();
	}
	
	@Test
	void shouldFindEmployeeById() {
		Employee employee = new Employee();
		employee.setEmployeeId(101);
		
		when(employeeRepository.findById(101))
		.thenReturn(Optional.of(employee));
		
		Employee result = employeeService.findEmployeeById(101);
		
		assertNotNull(result);
		assertEquals(101, result.getEmployeeId());
		
	}
	
	@Test
	void shouldThrowExceptionWhenEmployeeDoesNotExist() {
		when(employeeRepository.findById(999))
			.thenReturn(Optional.empty());
		
		assertThrows(
				ResourceNotFoundException.class,
				() -> employeeService.findEmployeeById(999));
	}
	
    @Test
    void shouldDeleteEmployeeWithoutLeaveHistory() {
        Employee employee = new Employee();
        employee.setEmployeeId(101);

        when(employeeRepository.findById(101))
            .thenReturn(Optional.of(employee));

        when(leaveRepository.findByEmployee_EmployeeId(101))
            .thenReturn(List.of());

        String result = employeeService.deleteEmployeeById(101);

        assertEquals("Employee deleted successfully", result);
        verify(employeeRepository).delete(employee);
    }
    
    @Test
    void shouldPreventDeletionWhenLeaveHistoryExists() {
        Employee employee = new Employee();
        employee.setEmployeeId(101);

        LeaveApplication leave = new LeaveApplication();

        when(employeeRepository.findById(101))
            .thenReturn(Optional.of(employee));

        when(leaveRepository.findByEmployee_EmployeeId(101))
            .thenReturn(List.of(leave));

        assertThrows(
            BusinessException.class,
            () -> employeeService.deleteEmployeeById(101));

        verify(employeeRepository, never()).delete(any(Employee.class));
    }
}

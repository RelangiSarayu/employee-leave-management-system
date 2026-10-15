package com.project.employee_leave_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.project.employee_leave_management.dto.LeaveApplicationRequestdto;
import com.project.employee_leave_management.dto.LeaveApplicationResponsedto;
import com.project.employee_leave_management.entity.Employee;
import com.project.employee_leave_management.entity.LeaveApplication;
import com.project.employee_leave_management.repository.EmployeeRepository;
import com.project.employee_leave_management.repository.LeaveApplicationRepository;
import com.project.employee_leave_management.exception.BusinessException;
import com.project.employee_leave_management.exception.ResourceNotFoundException;
import com.project.employee_leave_management.service.LeaveService;
import com.project.employee_leave_management.entity.LeaveStatus;
import com.project.employee_leave_management.entity.LeaveType;

public class LeaveServiceTest {
	
	@Mock
	private LeaveApplicationRepository leaveRepository;
	
	@Mock
	private EmployeeRepository employeeRepository;
	
	@InjectMocks
	private LeaveService leaveService;
	
	@BeforeEach
	void setUp(){
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void shoulCalculateLeaveDaysCorrectly() {
		LeaveApplication leave = new LeaveApplication();
		leave.setStartDate(LocalDate.of(2026, 11, 17));
		leave.setEndDate(LocalDate.of(2026, 11, 19));
		
		LeaveService leaveService = new LeaveService(null, null);
		long actualDays = leaveService.calculatedLeaveDays(leave);
		 
		assertEquals(3,actualDays);
	}
	@Test
	void shouldCalculateOneDayLeave() {
		LeaveApplication leave = new LeaveApplication();
		leave.setStartDate(LocalDate.of(2026, 11, 19));
		leave.setEndDate(LocalDate.of(2026, 11, 19));
		
		LeaveService leaveService = new LeaveService(null, null);
		long actualDays = leaveService.calculatedLeaveDays(leave);
		 
		assertEquals(1,actualDays);
	}
	@Test
	void shouldCalculateLongerDayLeave() {
		LeaveApplication leave = new LeaveApplication();
		leave.setStartDate(LocalDate.of(2026, 11, 19));
		leave.setEndDate(LocalDate.of(2026, 12, 1));
		
		LeaveService leaveService = new LeaveService(null, null);
		long actualDays = leaveService.calculatedLeaveDays(leave);
		 
		assertEquals(13,actualDays);
	}
	@Test
	void shouldCalculateUsedLeavesCorrectly() {
		LeaveApplication approvedLeave = new LeaveApplication();
		approvedLeave.setStartDate(LocalDate.of(2026, 11, 17));
		approvedLeave.setEndDate(LocalDate.of(2026, 11, 19));
		approvedLeave.setLeaveStatus(LeaveStatus.Approved);
		
		LeaveApplication pendingLeave = new LeaveApplication();
		pendingLeave.setStartDate(LocalDate.of(2026, 11, 23));
		pendingLeave.setEndDate(LocalDate.of(2026, 11, 24));
		pendingLeave.setLeaveStatus(LeaveStatus.Pending);
		
		when(leaveRepository.findByEmployee_EmployeeId(101))
			.thenReturn(List.of(approvedLeave, pendingLeave));
		long actualUsedLeaves = leaveService.calculateUsedLeaves(101);
		assertEquals(3L, actualUsedLeaves);
		
	}
	
	@Test
	void shouldCalculatePendingLeavesCorrectly() {
		LeaveApplication pendingLeave = new LeaveApplication();
		pendingLeave.setStartDate(LocalDate.of(2026, 11, 17));
		pendingLeave.setEndDate(LocalDate.of(2026, 11, 19));
		pendingLeave.setLeaveStatus(LeaveStatus.Pending);
		
		LeaveApplication approvedLeave = new LeaveApplication();
		approvedLeave.setStartDate(LocalDate.of(2026, 11, 23));
		approvedLeave.setEndDate(LocalDate.of(2026, 11, 24));
		approvedLeave.setLeaveStatus(LeaveStatus.Approved);
		
		when(leaveRepository.findByEmployee_EmployeeId(101))
			.thenReturn(List.of(pendingLeave, approvedLeave));
		
	    long actualPendingLeaves = leaveService.calculatePendingLeaves(101);
		assertEquals(3L, actualPendingLeaves);
	}
	
	@Test
	void shouldCalculateAvailableLeaveCorrectly() {
		LeaveApplication approveLeave = new LeaveApplication();
		approveLeave.setStartDate(LocalDate.of(2026, 11, 10));
		approveLeave.setEndDate(LocalDate.of(2026, 11, 14));
		approveLeave.setLeaveStatus(LeaveStatus.Approved);
		
		LeaveApplication pendingLeave = new LeaveApplication();
		pendingLeave.setStartDate(LocalDate.of(2026, 11, 17));
		pendingLeave.setEndDate(LocalDate.of(2026, 11, 19));
		pendingLeave.setLeaveStatus(LeaveStatus.Pending);
		
		when(leaveRepository.findByEmployee_EmployeeId(101))
			.thenReturn(List.of(approveLeave, pendingLeave));
		
		long actualAvailableLeaves = leaveService.calculateAvailableLeaves(101);
		assertEquals(12L, actualAvailableLeaves);
	}
	
	@Test
	void rejectedAndCancelledLeavesShouldNotReduceAvailableBalance() {
		LeaveApplication rejectedLeave = new LeaveApplication();
		rejectedLeave.setStartDate(LocalDate.of(2026, 11, 10));
		rejectedLeave.setEndDate(LocalDate.of(2026, 11, 10));
		rejectedLeave.setLeaveStatus(LeaveStatus.Rejected);
		
		LeaveApplication cancelledLeave = new LeaveApplication();
		rejectedLeave.setStartDate(LocalDate.of(2026, 11, 10));
		rejectedLeave.setEndDate(LocalDate.of(2026, 11, 10));
		rejectedLeave.setLeaveStatus(LeaveStatus.Cancelled);
		
		when(leaveRepository.findByEmployee_EmployeeId(101))
		.thenReturn(List.of(rejectedLeave, cancelledLeave));
		
		long actualAvailableLeaves = leaveService.calculateAvailableLeaves(101);
		assertEquals(20L, actualAvailableLeaves);
		
	}
	
	@Test
	void shouldReturnFullBalanceWhenEmployeeHasNoLeaves() {
		when(leaveRepository.findByEmployee_EmployeeId(101))
		.thenReturn(List.of());
		
		long actualAvailableLeaves = leaveService.calculateAvailableLeaves(101);
		assertEquals(20L, actualAvailableLeaves);
	}
	
	@Test
	void shouldApplyLeaveSuccessfully() {
		Employee employee = new Employee();
		employee.setEmployeeId(101);
		
		when(employeeRepository.findById(101))
				.thenReturn(java.util.Optional.of(employee));
		
		when(leaveRepository.findByEmployee_EmployeeId(101))
			.thenReturn(List.of());
		
		LeaveApplicationRequestdto request = new LeaveApplicationRequestdto();
		
		request.setEmployeeId(101);
		request.setLeaveType(LeaveType.Sick);
		request.setStartDate(LocalDate.now().plusDays(5));
		request.setEndDate(LocalDate.now().plusDays(7));
		request.setLeaveReason("Fever");
		
		when(leaveRepository.save(any(LeaveApplication.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));
		
		LeaveApplicationResponsedto response = leaveService.applyLeave(request);
		
		assertNotNull(response);
		
		ArgumentCaptor<LeaveApplication> captor = ArgumentCaptor.forClass(LeaveApplication.class);
		
		verify(leaveRepository).save(captor.capture());
		
		LeaveApplication savedLeave = captor.getValue();
		
		assertEquals(LeaveStatus.Pending, savedLeave.getLeaveStatus());
		assertEquals(101, savedLeave.getEmployee().getEmployeeId());
		assertEquals(request.getStartDate(), savedLeave.getStartDate());
		assertEquals(request.getEndDate(), savedLeave.getEndDate());
		
	}
	
	@Test
	void shouldRejectLeaveWhenEmployeeDoesNotExist() {
		
		when(employeeRepository.findById(999))
			.thenReturn(java.util.Optional.empty());
		
		LeaveApplicationRequestdto request = createRequest(
				999, LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));
		assertThrows(ResourceNotFoundException.class, 
				() -> leaveService.applyLeave(request));
		
		verify(leaveRepository, never())
			.save(any(LeaveApplication.class));
	}
	
	 @Test
	    void shouldRejectLeaveWhenStartDateIsInPast() {
	        Employee employee = new Employee();
	        employee.setEmployeeId(101);

	        when(employeeRepository.findById(101))
	                .thenReturn(Optional.of(employee));

	        LeaveApplicationRequestdto request = createRequest(
	                101, LocalDate.now().minusDays(2), LocalDate.now().plusDays(2));

	        assertThrows(BusinessException.class,
	                () -> leaveService.applyLeave(request));

	        verify(leaveRepository, never())
	                .save(any(LeaveApplication.class));
	    }
	
	 @Test
	    void shouldRejectLeaveWhenEndDateIsBeforeStartDate() {
	        Employee employee = new Employee();
	        employee.setEmployeeId(101);

	        when(employeeRepository.findById(101))
	                .thenReturn(Optional.of(employee));

	        LeaveApplicationRequestdto request = createRequest(
	                101, LocalDate.now().plusDays(8), LocalDate.now().plusDays(5));

	        assertThrows(
	                BusinessException.class,
	                () -> leaveService.applyLeave(request));

	        verify(leaveRepository, never())
	                .save(any(LeaveApplication.class));
	    }
	 
	 @Test
	    void shouldRejectLeaveWhenBalanceIsInsufficient() {
	        Employee employee = new Employee();
	        employee.setEmployeeId(101);

	        when(employeeRepository.findById(101))
	                .thenReturn(Optional.of(employee));

	        LeaveApplication existingLeave = createLeave(
	                LocalDate.now().plusDays(30), LocalDate.now().plusDays(49), LeaveStatus.Approved);

	        when(leaveRepository.findByEmployee_EmployeeId(101))
	                .thenReturn(List.of(existingLeave));

	        LeaveApplicationRequestdto request = createRequest(
	                101, LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));

	        assertThrows(
	                BusinessException.class,
	                () -> leaveService.applyLeave(request));

	        verify(leaveRepository, never())
	                .save(any(LeaveApplication.class));
	    }

	    @Test
	    void shouldRejectOverlappingLeave() {
	        Employee employee = new Employee();
	        employee.setEmployeeId(101);

	        when(employeeRepository.findById(101))
	                .thenReturn(Optional.of(employee));

	        LeaveApplication existingLeave = createLeave(
	                LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), LeaveStatus.Pending);

	        when(leaveRepository.findByEmployee_EmployeeId(101))
	                .thenReturn(List.of(existingLeave));

	        LeaveApplicationRequestdto request = createRequest(
	                101, LocalDate.now().plusDays(6), LocalDate.now().plusDays(8));

	        assertThrows(
	                BusinessException.class,
	                () -> leaveService.applyLeave(request));

	        verify(leaveRepository, never())
	                .save(any(LeaveApplication.class));
	    }
	    
	    @Test
	    void shouldApprovePendingLeaves() {
	    	LeaveApplication leave = createLeave(
	    			LocalDate.now().plusDays(5),
	    			LocalDate.now().plusDays(7),
	    			LeaveStatus.Pending);
	    	
	    	mockLeaveById(1, leave);
	    	LeaveApplicationResponsedto response  = leaveService.approveLeave(1);
	    	assertNotNull(response);
	    	assertEquals(LeaveStatus.Approved, leave.getLeaveStatus());
	    	verify(leaveRepository).save(leave);
	    }

	    
	    @Test
	    void shouldRejectPendingLeaves() {
	    	LeaveApplication leave = createLeave(
	    			LocalDate.now().plusDays(5),
	    			LocalDate.now().plusDays(7),
	    			LeaveStatus.Pending);
	    	
	    	mockLeaveById(2, leave);
	    	LeaveApplicationResponsedto response = leaveService.rejectLeave(2);
	    	assertNotNull(response);
	    	assertEquals(LeaveStatus.Rejected, leave.getLeaveStatus());
	    	verify(leaveRepository).save(leave);
	    }
	    
	    @Test 
	    void shouldRejectApprovalWhenLeaveIsNotPending() {
	    	LeaveApplication leave = createLeave(
	    			LocalDate.now().plusDays(5),
	    			LocalDate.now().plusDays(7),
	    			LeaveStatus.Approved);
	    	mockLeaveById(1, leave);
	    	assertThrows(
	    			BusinessException.class,
	    			() -> leaveService.approveLeave(1));
	    	verify(leaveRepository, never())
	    		.save(any(LeaveApplication.class));
	    	
	    	
	    }
	    
	    @Test
	    void shouldCancelApprovedLeave() {
	    	LeaveApplication leave = createLeave(
	    			LocalDate.now().plusDays(5),
	    			LocalDate.now().plusDays(7),
	    			LeaveStatus.Approved);
	    	mockLeaveById(3, leave);
	    	LeaveApplicationResponsedto response = leaveService.cancelLeave((3));
	    	assertNotNull(response);
	    	assertEquals(LeaveStatus.Cancelled, leave.getLeaveStatus());
	    	verify(leaveRepository).save(leave);
	    }
	    
	    @Test
	    void shouldRejectRejectionWhenLeaveIsNotPending() {
	    	LeaveApplication leave = createLeave(
	    			LocalDate.now().plusDays(5),
	    			LocalDate.now().plusDays(7),
	    			LeaveStatus.Approved);
	    	mockLeaveById(2, leave);
	    	assertThrows(BusinessException.class,
	    			() -> leaveService.rejectLeave(2));
	    	verify(leaveRepository, never())
	    		.save(any(LeaveApplication.class));
	    }
	    


		@Test
		void shouldRejectCancellationWhenLeaveIsNotApproved() {
		    LeaveApplication leave = createLeave(
		            LocalDate.now().plusDays(5),
		            LocalDate.now().plusDays(7),
		            LeaveStatus.Pending);
		
		    mockLeaveById(3, leave);
		
		    assertThrows(
		            BusinessException.class,
		            () -> leaveService.cancelLeave(3));
		
		    verify(leaveRepository, never())
		            .save(any(LeaveApplication.class));
		}

		
		
		@Test
		void shouldThrowExceptionWhenLeaveDoesNotExist() {
		    when(leaveRepository.findById(99))
		            .thenReturn(Optional.empty());
		
		    assertThrows(
		            ResourceNotFoundException.class,
		            () -> leaveService.approveLeave(99));
		
		    verify(leaveRepository, never())
		            .save(any(LeaveApplication.class));
		}

	    @Test
	    void shouldReturnAllLeaves() {
	    	LeaveApplication leave = createLeave(
	    			LocalDate.now().plusDays(2),
	    			LocalDate.now().plusDays(3),
	    			LeaveStatus.Pending);
	    	
	    	LeaveApplication leave2 = createLeave(
	    			LocalDate.now().plusDays(2),
	    			LocalDate.now().plusDays(3),
	    			LeaveStatus.Approved);
	    	
	    	when(leaveRepository.findAll())
	    		.thenReturn(List.of(leave, leave2));
	    	
	    	List<LeaveApplicationResponsedto> result = leaveService.getAllLeaves();
	    	assertEquals(2, result.size());
	    	verify(leaveRepository).findAll();	
	    }
	    
	    @Test
	    void shouldFindLeaveById() {
	    	LeaveApplication leave = createLeave(
	    			LocalDate.now().plusDays(2),
	    			LocalDate.now().plusDays(3),
	    		    LeaveStatus.Pending);
	    	
	    	mockLeaveById(1, leave);
	    	LeaveApplicationResponsedto result = leaveService.findLeaveById(1);
	    	assertNotNull(result);
	    	verify(leaveRepository).findById(1);
	    }
	    
	    @Test
	    void shouldThrowExceptionWhenLeaveIdDoesNotExist() {
	        when(leaveRepository.findById(999))
	            .thenReturn(Optional.empty());

	        assertThrows(
	            ResourceNotFoundException.class,
	            () -> leaveService.findLeaveById(999));
	    }
	    
	    @Test
	    void shouldReturnLeavesForEmployee() {
	        int employeeId = 101;

	        LeaveApplication leave = createLeave(
	            LocalDate.now().plusDays(2),
	            LocalDate.now().plusDays(3),
	            LeaveStatus.Pending);

	        when(employeeRepository.existsById(employeeId))
	            .thenReturn(true);

	        when(leaveRepository.findByEmployee_EmployeeId(employeeId))
	            .thenReturn(List.of(leave));

	        List<LeaveApplicationResponsedto> result =
	            leaveService.getEmployeeLeaves(employeeId);

	        assertEquals(1, result.size());
	        verify(employeeRepository).existsById(employeeId);
	        verify(leaveRepository).findByEmployee_EmployeeId(employeeId);
	    }
	    
	    @Test
	    void shouldThrowExceptionWhenEmployeeDoesNotExist() {
	        int employeeId = 999;

	        when(employeeRepository.existsById(employeeId))
	            .thenReturn(false);

	        assertThrows(
	            ResourceNotFoundException.class,
	            () -> leaveService.getEmployeeLeaves(employeeId));

	        verify(leaveRepository, never())
	            .findByEmployee_EmployeeId(employeeId);
	    }
	    
	    @Test
	    void shouldDeletePendingLeave() {
	        LeaveApplication leave = createLeave(
	            LocalDate.now().plusDays(2),
	            LocalDate.now().plusDays(3),
	            LeaveStatus.Pending
	        );

	        mockLeaveById(1, leave);

	        String result = leaveService.deleteLeaveById(1);

	        assertEquals("Leave Deleted Successfully", result);
	        verify(leaveRepository).delete(leave);
	    }
	    
	    @Test
	    void shouldNotDeleteApprovedLeave() {
	        LeaveApplication leave = createLeave(
	            LocalDate.now().plusDays(2),
	            LocalDate.now().plusDays(3),
	            LeaveStatus.Approved
	        );

	        mockLeaveById(1, leave);

	        assertThrows(
	            BusinessException.class,
	            () -> leaveService.deleteLeaveById(1)
	        );

	        verify(leaveRepository, never()).delete(any(LeaveApplication.class));
	    }
	    
	    @Test
	    void shouldNotDeleteLeaveWhenIdDoesNotExist() {
	        when(leaveRepository.findById(999))
	            .thenReturn(Optional.empty());

	        assertThrows(
	            ResourceNotFoundException.class,
	            () -> leaveService.deleteLeaveById(999)
	        );

	        verify(leaveRepository, never()).delete(any(LeaveApplication.class));
	    }
	    
	    private LeaveApplicationRequestdto createRequest(int employeeId, LocalDate startDate, LocalDate endDate) {
	        LeaveApplicationRequestdto request = new LeaveApplicationRequestdto();
	        request.setEmployeeId(employeeId);
	        request.setLeaveType(LeaveType.Sick);
	        request.setStartDate(startDate);
	        request.setEndDate(endDate);
	        request.setLeaveReason("Test Reason");
	        return request;
	    }

	    private LeaveApplication createLeave(LocalDate startDate, LocalDate endDate, LeaveStatus status) {
	        LeaveApplication leave = new LeaveApplication();
	        leave.setStartDate(startDate);
	        leave.setEndDate(endDate);
	        leave.setLeaveStatus(status);
	        
	        // Associating a mock employee to prevent NullPointerException if the service reads the ID
	        Employee employee = new Employee();
	        employee.setEmployeeId(101);
	        leave.setEmployee(employee);
	        
	        return leave;
	    }
	    

	private void mockLeaveById(int leaveId, LeaveApplication leave) {
	    when(leaveRepository.findById(leaveId))
	            .thenReturn(Optional.of(leave));
	
	    when(leaveRepository.save(any(LeaveApplication.class)))
	            .thenAnswer(invocation -> invocation.getArgument(0));
	}


}

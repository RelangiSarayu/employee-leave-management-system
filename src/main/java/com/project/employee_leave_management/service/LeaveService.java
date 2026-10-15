package com.project.employee_leave_management.service;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.List;

import com.project.employee_leave_management.repository.LeaveApplicationRepository;
import com.project.employee_leave_management.repository.EmployeeRepository;
import com.project.employee_leave_management.entity.LeaveApplication;
import com.project.employee_leave_management.entity.Employee;
import com.project.employee_leave_management.entity.LeaveStatus;
import com.project.employee_leave_management.exception.ResourceNotFoundException;
import com.project.employee_leave_management.exception.BusinessException;
import com.project.employee_leave_management.dto.LeaveApplicationRequestdto;
import com.project.employee_leave_management.dto.LeaveApplicationResponsedto;


@Service
public class LeaveService {
	private final LeaveApplicationRepository leaveApplicationRepository;
	private final EmployeeRepository employeeRepository;

	public LeaveService(LeaveApplicationRepository leaveApplicationRepository, EmployeeRepository employeeRepository){
		this.leaveApplicationRepository = leaveApplicationRepository;
		this.employeeRepository = employeeRepository;
	}
	
	private LeaveApplicationResponsedto convertToResponsedto(LeaveApplication leave){
		
		LeaveApplicationResponsedto response = new LeaveApplicationResponsedto();
		
		response.setLeaveId(leave.getLeaveId());
		response.setEmployeeId(leave.getEmployee().getEmployeeId());
		response.setLeaveType(leave.getLeaveType());
		response.setStartDate(leave.getStartDate());
		response.setEndDate(leave.getEndDate());
		response.setLeaveReason(leave.getLeaveReason());
		response.setLeaveStatus(leave.getLeaveStatus());
		
		return response;
	}
	
	private LeaveApplication findLeaveEntityById(int leaveId) {
	    return leaveApplicationRepository.findById(leaveId)
	            .orElseThrow(() ->
	                new ResourceNotFoundException("Leave Application not Found"));
	}
	
	private List<LeaveApplication> getEmployeeLeaveEntities(int employeeId) {

	    return leaveApplicationRepository
	            .findByEmployee_EmployeeId(employeeId);
	}
	
	private boolean leaveDatesOverlap(LocalDate newStartDate, LocalDate newEndDate, int employeeId) {

	    List<LeaveApplication> existingLeaves = getEmployeeLeaveEntities(employeeId);
	    for (LeaveApplication leave : existingLeaves) {
	        if (leave.getLeaveStatus() == LeaveStatus.Pending || leave.getLeaveStatus() == LeaveStatus.Approved) {
	            boolean overlaps = !newStartDate.isAfter(leave.getEndDate()) && !newEndDate.isBefore(leave.getStartDate());
	            if (overlaps) {
	                return true;
	            }
	        }
	    }
	    return false;
	}
	
	public LeaveApplicationResponsedto applyLeave(LeaveApplicationRequestdto request){
		
		LeaveApplication leaveApplication = new LeaveApplication();
		
		Employee employee = employeeRepository.findById(request.getEmployeeId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
		leaveApplication.setEmployee(employee);
		
	    leaveApplication.setLeaveType(request.getLeaveType());
	    
	    leaveApplication.setStartDate(request.getStartDate());
	    leaveApplication.setEndDate(request.getEndDate());
	    
	    leaveApplication.setLeaveReason(request.getLeaveReason());
		
		if(leaveApplication.getStartDate() == null || leaveApplication.getEndDate() == null) {
			throw new BusinessException("Leave dates are required");
		}
		
		if(leaveApplication.getStartDate().isBefore(LocalDate.now())){
			throw new BusinessException("The Start Date is Invalid. Leave start date cannot be in the past");
		}
		
		if(leaveApplication.getEndDate().isBefore(leaveApplication.getStartDate())){
			throw new BusinessException("End Date cannot be before the Start Date");
		}
		
		if(leaveDatesOverlap(leaveApplication.getStartDate(), leaveApplication.getEndDate(), request.getEmployeeId()) == true) {
			throw new BusinessException("This span of leaves were already reserved");
		}
		
		long leaveDays = calculatedLeaveDays(leaveApplication);
		long availableLeaves = calculateAvailableLeaves(employee.getEmployeeId());
		
		if(leaveDays > availableLeaves) {
			throw new BusinessException("Insufficient leave balance");
		}
		
		leaveApplication.setLeaveStatus(LeaveStatus.Pending);
		
	    LeaveApplication savedLeave = leaveApplicationRepository.save(leaveApplication);

	    return convertToResponsedto(savedLeave);
	}
	
	public long calculatedLeaveDays(LeaveApplication leaveApplication){
		return ChronoUnit.DAYS.between(leaveApplication.getStartDate(), leaveApplication.getEndDate()) + 1;
	}
	
	public String deleteLeaveById(int leaveId) {
		LeaveApplication leave = findLeaveEntityById(leaveId);
		if(leave.getLeaveStatus() == LeaveStatus.Pending) {
			leaveApplicationRepository.delete(leave);
			return "Leave Deleted Successfully";
		}else{
			throw new BusinessException("Only pending leaves can be deleted");
		}
	}
	
	public List<LeaveApplicationResponsedto> getAllLeaves(){
		List<LeaveApplication> leaves = leaveApplicationRepository.findAll();
		return leaves.stream().map(this::convertToResponsedto).toList();
	}
	
	public LeaveApplicationResponsedto findLeaveById(int leaveId){
		LeaveApplication leave = findLeaveEntityById(leaveId);
		return convertToResponsedto(leave);
	}
	
	public List<LeaveApplicationResponsedto > getEmployeeLeaves(int employeeId){
	    if (!employeeRepository.existsById(employeeId)) {
	        throw new ResourceNotFoundException("Employee not found");
	    }
		List<LeaveApplication> leaves =  getEmployeeLeaveEntities(employeeId);
	    return leaves.stream().map(this::convertToResponsedto).toList();
	}
	
	public long calculateUsedLeaves(int employeeId){
		List<LeaveApplication> leaves = getEmployeeLeaveEntities(employeeId);
		long usedLeaves = 0;
		for(LeaveApplication leave : leaves) {
			if(leave.getLeaveStatus() == LeaveStatus.Approved) {
				usedLeaves = usedLeaves + calculatedLeaveDays(leave);
			}
		}
		return usedLeaves;
	}

	public long calculatePendingLeaves(int employeeId) {
		List<LeaveApplication> pendingLeavesList = getEmployeeLeaveEntities(employeeId);
		long pendingLeaves = 0;
		for(LeaveApplication leave : pendingLeavesList) {
			if(leave.getLeaveStatus() == LeaveStatus.Pending) {
				pendingLeaves = pendingLeaves + calculatedLeaveDays(leave);
			}
		}
		return pendingLeaves;
	}
	
	public long calculateAvailableLeaves(int employeeId){
		long totalLeaves = 20;
		long usedLeaves = calculateUsedLeaves(employeeId);
		long pendingLeaves = calculatePendingLeaves(employeeId);
		return totalLeaves - usedLeaves - pendingLeaves;
	}
	
	public LeaveApplicationResponsedto approveLeave(int leaveId){
		LeaveApplication usedleave = findLeaveEntityById(leaveId);
		if(usedleave.getLeaveStatus() != LeaveStatus.Pending){
			throw new BusinessException("only Pending leaves can be Approved..");
		}
		
		usedleave.setLeaveStatus(LeaveStatus.Approved);
		LeaveApplication approvedLeave = leaveApplicationRepository.save(usedleave);
		return convertToResponsedto(approvedLeave);
	}
	
	public LeaveApplicationResponsedto rejectLeave(int leaveId) {
		LeaveApplication rejectleave = findLeaveEntityById(leaveId);
		if(rejectleave.getLeaveStatus() != LeaveStatus.Pending) {
			throw new BusinessException("Only Pending leaves can be Rejected... Approved Or Canncelled Leaves cannot be Rejected...");
		}
		rejectleave.setLeaveStatus(LeaveStatus.Rejected);
		LeaveApplication rejectedLeave = leaveApplicationRepository.save(rejectleave);
		return convertToResponsedto(rejectedLeave);
	}
	
	public LeaveApplicationResponsedto cancelLeave(int leaveId) {
	    LeaveApplication cancelleave = findLeaveEntityById(leaveId);
	    if (cancelleave.getLeaveStatus() != LeaveStatus.Approved) {
	        throw new BusinessException("Only approved leaves can be cancelled... Pending Or Rejected cannot be cancelled");
	    }
	    cancelleave.setLeaveStatus(LeaveStatus.Cancelled);
	    LeaveApplication cancelledLeave = leaveApplicationRepository.save(cancelleave);
	    return convertToResponsedto(cancelledLeave);
	}
	
}

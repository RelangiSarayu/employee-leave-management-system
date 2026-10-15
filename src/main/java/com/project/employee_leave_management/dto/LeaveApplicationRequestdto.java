package com.project.employee_leave_management.dto;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import com.project.employee_leave_management.entity.LeaveType;

public class LeaveApplicationRequestdto {
	
	@NotNull(message = "Employee ID is required")
	@Positive(message = "Employee ID must be greater than zero")
	private Integer employeeId;
	@NotNull(message = "Leave type is required")
	private LeaveType leaveType;
	@NotNull(message = "Start date is required")
	private LocalDate startDate;
	@NotNull(message = "End Date is required")
	private LocalDate endDate;
	@NotBlank(message = "Leave Reason is required")
	private String leaveReason;
	
	public Integer getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}
	public LeaveType getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(LeaveType leaveType) {
		this.leaveType = leaveType;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public String getLeaveReason() {
		return leaveReason;
	}
	public void setLeaveReason(String leaveReason) {
		this.leaveReason = leaveReason;
	}
	
	
	
}

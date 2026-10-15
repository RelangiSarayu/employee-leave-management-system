package com.project.employee_leave_management.dto;

import java.time.LocalDate;
import com.project.employee_leave_management.entity.LeaveType;
import com.project.employee_leave_management.entity.LeaveStatus;

public class LeaveApplicationResponsedto {
	
	 	private int leaveId;
	    private int employeeId;
	    private LeaveType leaveType;
	    private LocalDate startDate;
	    private LocalDate endDate;
	    private String leaveReason;
	    private LeaveStatus leaveStatus;
	    
	    public int getLeaveId() {
			return leaveId;
		}
		public void setLeaveId(int leaveId) {
			this.leaveId = leaveId;
		}
		public int getEmployeeId() {
			return employeeId;
		}
		public void setEmployeeId(int employeeId) {
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
		public LeaveStatus getLeaveStatus() {
			return leaveStatus;
		}
		public void setLeaveStatus(LeaveStatus leaveStatus) {
			this.leaveStatus = leaveStatus;
		}
		
}

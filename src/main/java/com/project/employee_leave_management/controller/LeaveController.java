package com.project.employee_leave_management.controller;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.project.employee_leave_management.service.LeaveService;
import com.project.employee_leave_management.dto.LeaveApplicationRequestdto;
import com.project.employee_leave_management.dto.LeaveApplicationResponsedto;

@RestController
public class LeaveController {
	private final LeaveService leaveService;
	
	public LeaveController(LeaveService leaveService) {
		this.leaveService = leaveService;
	}
	
	@PostMapping("/leaves")
	public LeaveApplicationResponsedto applyLeave(@Valid @RequestBody LeaveApplicationRequestdto leaveApplicationRequest) {
		return leaveService.applyLeave(leaveApplicationRequest);
	}
	
	@DeleteMapping("/leaves/{leaveId}")
	public String deleteleave(@PathVariable int leaveId) {
		return leaveService.deleteLeaveById(leaveId);
	}
	
	@GetMapping("/leaves")
	public List<LeaveApplicationResponsedto> getAllLeaves(){
		return leaveService.getAllLeaves();
	}
	
	@GetMapping("/leaves/{leaveId}")
	public LeaveApplicationResponsedto findLeaveById(@PathVariable int leaveId){
		return leaveService.findLeaveById(leaveId);
	}
	
	@GetMapping("/employees/{employeeId}/leaves")
	public List<LeaveApplicationResponsedto> getEmployeeLeaves(@PathVariable int employeeId){
		return leaveService.getEmployeeLeaves(employeeId);
	}
	
	@GetMapping("employees/{employeeId}/leaves/available")
	public long calaculateAvailableLeaves(@PathVariable int employeeId) {
		return leaveService.calculateAvailableLeaves(employeeId);
	}
	
	@PostMapping("leaves/{leaveId}/approve")
	public LeaveApplicationResponsedto approveLeave(@PathVariable int leaveId){
		return leaveService.approveLeave(leaveId);
	}
	
	@PostMapping("/leaves/{leaveId}/reject")
	public LeaveApplicationResponsedto rejectLeave(@PathVariable int leaveId) {
		return leaveService.rejectLeave(leaveId);
	}
	
	@PostMapping("/leaves/{leaveId}/cancel")
	public LeaveApplicationResponsedto cancelLeave(@PathVariable int leaveId){
		return leaveService.cancelLeave(leaveId);
	}
}

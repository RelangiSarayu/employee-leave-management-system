package com.project.employee_leave_management;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmployeeLeaveManagementApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Test
	void additionTest() {
        int result = 10 + 5;

        assertEquals(15, result);
    }

}

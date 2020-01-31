package serenity;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.junit.*;
import org.junit.runner.RunWith;
import steps.Employee;

@RunWith(SerenityRunner.class)
public class EmployeeAPI {
	@Steps
    Employee employee;
	private String id;
	
	
	@Test
	public void createEmployee() {
		employee.createEmployee("seren10", "12300", "35");
		employee.createEmployeeIsSuccessful(200);
		id = employee.id;
		System.out.println(id);
	}
	@Test 
	public void searchEmployee() {
		employee.searchEmployee(id);
		employee.searchEmployeeIsSuccessful(200);
	}
	@Test
	public void updateEmployee() {
		employee.updateEmployee(id, "seren15", "12400", "35");
		employee.updateEmployeeIsSuccessful(200);
	}
	@Test 
	public void deleteEmployee() {
		employee.deleteEmployee(id);
		employee.deleteIsSuccessful(200);
	}
}

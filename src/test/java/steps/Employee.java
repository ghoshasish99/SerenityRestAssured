package steps;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Step;

public class Employee {
	
	private String baseURL="http://dummy.restapiexample.com/api/v1/";
    private Response response;
    public String id;
	
    @Step
	public void createEmployee(String name, String salary, String age) {
		response = SerenityRest.given().body(createPayload(name,salary,age)).post(baseURL+"create");
	}
    @Step
	public void createEmployeeIsSuccessful(int code) {
		response.then().statusCode(code);
		this.id = response.then().extract().jsonPath().getString("id");
	}
    
	@Step
	public void searchEmployee(String id) {
		response = SerenityRest.get(baseURL+"employee/"+id);
	}
	@Step
	public void searchEmployeeIsSuccessful(int code) {
		response.then().statusCode(code);
		System.out.println(response.asString());
	}
	@Step
	public void updateEmployee(String id, String name, String salary, String age) {
		response = SerenityRest.given().body(createPayload(name, salary, age)).put(baseURL+"update/"+id);
	}
	@Step
	public void updateEmployeeIsSuccessful(int code)
	{
		response.then().statusCode(code);
		System.out.println(response.asString());
	}
	@Step
	public void deleteEmployee(String id) {
		response = SerenityRest.delete(baseURL+"delete/"+id);
	}
	@Step
	public void deleteIsSuccessful(int code) {
		response.then().statusCode(code);
		System.out.println(response.asString());
	}
	
	private String createPayload(String name, String salary, String age ) {		
		return ("{\"name\": "+"\""+name+"\""+",\"salary\":"+"\""+salary+"\""+",\"age\":"+"\""+age+"\""+"}");
	}

}

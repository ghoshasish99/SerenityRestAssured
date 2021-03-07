package src.test.java.com.serenity.tests.features.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import io.restassured.RestAssured;
import org.junit.runner.RunWith;


@RunWith(SerenityRunner.class)

public class PaymentAPI {

	private String id;

	@Before
	public void connectionSetup() {
		System.out.println("============== In the Before Hook ==============");
		RestAssured.baseURI = "http://json-server-1.azurewebsites.net/";
	}

	@After
	public void reset() {
		System.out.println("============== Resetting all connections ==============");
		RestAssured.reset();
	}

	@Steps
	PaymentSteps steps;

	@Given("^I create a customer with \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\",\"([^\"]*)\",\"([^\"]*)\",\"([^\"]*)\"$")
	public void i_create_a_customer_with(String FirstName, String LastName, String Age, String EmailID, String Nationality, String Date)
			throws Exception {
		steps.createCustomer(FirstName, LastName, Age, EmailID, Nationality, Date);
	}

	@Given("^I create an account of type \"([^\"]*)\"$")
	public void i_create_an_account_of_type(String AccountType) throws Exception {
		steps.createAccount(AccountType);
	}

	@When("^I debit the account with \"([^\"]*)\" and credit internal account$")
	public void i_debit_the_account_with_and_credit_internal_account(String TransactionAmount) throws Exception {
		steps.createPayment(TransactionAmount);
	}

	@Then("^The payment should be successful$")
	public void the_payment_should_be_successful() throws Exception {
		steps.assertResponseSuccess();
	}

}

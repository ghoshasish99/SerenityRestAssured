package com.serenity.tests.features.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import io.restassured.RestAssured;

import java.util.Collection;

import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;


@RunWith(SerenityRunner.class)

public class PaymentAPI {

	String scenarioName;
	Collection<String> tags;
	
	@Steps
	PaymentSteps steps;
	
	
	@Before
	public void connectionSetup(Scenario scenario) throws ParseException {
		System.out.println("========== Checking if services are up ==============");
		scenarioName = scenario.getName();
		RestAssured.baseURI = "http://json-server-1.azurewebsites.net/";
		steps.checkServices();
		tags=scenario.getSourceTagNames();   
	}

	@After
	public void reset() {
		System.out.println("========== Resetting all connections ==============");
		RestAssured.reset();
	}

	@Given("^I create a customer with \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\",\"([^\"]*)\",\"([^\"]*)\",\"([^\"]*)\"$")
	public void i_create_a_customer_with(String FirstName, String LastName, String Age, String EmailID, String Nationality, String Date)
			throws Exception {
		if(steps.checkStatus("customer").equals("OK"))
		  steps.createCustomer(FirstName, LastName, Age, EmailID, Nationality, Date);
		else {
		  steps.retrieveGoldenData(tags);
		}
	}

	@Given("^I create an account of type \"([^\"]*)\"$")
	public void i_create_an_account_of_type(String AccountType) throws Exception {
		if(steps.checkStatus("accounts").equals("OK"))
		   steps.createAccount(AccountType);
		else
		  steps.retrieveGoldenData(tags);
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

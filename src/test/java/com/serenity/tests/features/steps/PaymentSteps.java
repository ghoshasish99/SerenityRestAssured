package com.serenity.tests.features.steps;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Random;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import net.thucydides.core.annotations.Step;

public class PaymentSteps {

	private Response res = null; 
	private JsonPath jp = null; 
	private RequestSpecification requestSpec;
	private String customercode = "";
	private String IBAN = "";

	@Step
	public void createCustomer(String FirstName, String LastName, String Age, String EmailID, String Nationality,
			String Date) throws org.json.simple.parser.ParseException {
		
		String path = "/customers";
		String customerPayload = customerPayload(FirstName,LastName,Age,EmailID,Nationality,Date);
		postRequest(path,customerPayload);		
	}
	
	public void createAccount(String AccountType) throws org.json.simple.parser.ParseException {
		
		String path = "/accounts";
		String accountPayload = accountPayload(AccountType);
		postRequest(path,accountPayload);
		
	}
	
    public void createPayment(String TransactionAmount) throws org.json.simple.parser.ParseException {
		
		String path = "/transactions";
		String paymentPayload = paymentPayload(TransactionAmount);
		postRequest(path,paymentPayload);
		
	}

	private String customerPayload(String FirstName, String LastName, String Age, String EmailID, String Nationality,
			String Date) throws org.json.simple.parser.ParseException {
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader("src/test/resources/payloads/customer.json")) {
			Random rnd = new Random();
			int number = rnd.nextInt(999999);
			customercode = String.format("%06d", number);

			JSONObject customerDetails = (JSONObject) jsonParser.parse(reader);
			customerDetails.put("first_name", FirstName);
			customerDetails.put("last_name", LastName);
			customerDetails.put("age", Age);
			customerDetails.put("email", EmailID);
			customerDetails.put("nationality", Nationality);
			customerDetails.put("date_of_incorporation", Date);
			customerDetails.put("customer_code", customercode);
			if (Integer.parseInt(Age) < 60)
				customerDetails.put("customer_rating", 1);
			else
				customerDetails.put("customer_rating", 2);
			System.out.println(customerDetails.toString());
			return customerDetails.toString();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	private String accountPayload(String AccountType) throws org.json.simple.parser.ParseException {
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader("src/test/resources/payloads/accounts.json")) {
			Random rnd = new Random();
			int number = rnd.nextInt(999999);
			String IBANpostfix = String.format("%06d", number);
			IBAN = "NLBANK20B" + IBANpostfix;
			JSONObject accountDetails = (JSONObject) jsonParser.parse(reader);
			accountDetails.put("account_type", AccountType);
			accountDetails.put("account_id", IBAN);
			accountDetails.put("customer_code", customercode);
			return accountDetails.toString();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String paymentPayload(String TransactionAmount) throws org.json.simple.parser.ParseException {
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader("src/test/resources/payloads/transactions.json")) {

			JSONObject transactionDetails = (JSONObject) jsonParser.parse(reader);
			transactionDetails.put("amount", TransactionAmount);
			transactionDetails.put("debit_account", IBAN);
			return transactionDetails.toString();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	private void postRequest(String path, String payload) throws org.json.simple.parser.ParseException {
		try {
			RequestSpecBuilder builder = new RequestSpecBuilder();
			builder.setBasePath(path);
			builder.setContentType("application/json");
			builder.setBody(payload);
			requestSpec = builder.build();
			requestSpec = RestAssured.given().spec(requestSpec);
			requestSpec.log().all();
			res = requestSpec.when().post();
			assertEquals("Request Successful", 201, res.getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void assertResponseSuccess(){
		try {
			assertEquals("Request Successful", 201, res.getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}

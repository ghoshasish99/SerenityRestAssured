package com.serenity.tests.features.steps;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.runtime.StepDefinition;
import net.thucydides.core.annotations.Step;

public class PaymentSteps {

	private Response res = null;
	private JsonPath jp = null;
	private RequestSpecification requestSpec;
	private static String customercode = "";
	private String IBAN = "";
	protected static final Logger LOG = LoggerFactory.getLogger(StepDefinition.class);

	public void createCustomer(String FirstName, String LastName, String Age, String EmailID, String Nationality,
			String Date) throws org.json.simple.parser.ParseException {

		String path = "/customers";
		String customerPayload = customerPayload(FirstName, LastName, Age, EmailID, Nationality, Date);
		postRequest(path, customerPayload);
	}

	public void createAccount(String AccountType) throws org.json.simple.parser.ParseException {

		String path = "/accounts";
		String accountPayload = accountPayload(AccountType);
		postRequest(path, accountPayload);

	}

	public void createPayment(String TransactionAmount) throws org.json.simple.parser.ParseException {

		String path = "/transactions";
		String paymentPayload = paymentPayload(TransactionAmount);
		postRequest(path, paymentPayload);

	}

	private String customerPayload(String FirstName, String LastName, String Age, String EmailID, String Nationality,
			String Date) throws org.json.simple.parser.ParseException {
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader("src/test/resources/payloads/customer.json")) {
			if (checkStatus("customer").equals("OK")) {
				Random rnd = new Random();
				int number = rnd.nextInt(999999);
				customercode = String.format("%06d", number);
			}

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

	private static void updateStatusFile(String service, String status) throws org.json.simple.parser.ParseException {
		JSONParser jsonParser = new JSONParser();
		String fileloc = "src/test/resources/serviceStatus/serviceStatus.json";
		try (FileReader reader = new FileReader(fileloc)) {
			JSONObject serviceDetails = (JSONObject) jsonParser.parse(reader);
			JSONObject serviceJson = (JSONObject) serviceDetails.get(service);
			serviceJson.put("status", status);

			FileWriter file = new FileWriter(fileloc);
			file.write(serviceDetails.toJSONString());
			file.flush();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String checkStatus(String service) throws org.json.simple.parser.ParseException {
		JSONParser jsonParser = new JSONParser();
		String fileloc = "src/test/resources/serviceStatus/serviceStatus.json";
		try (FileReader reader = new FileReader(fileloc)) {
			JSONObject serviceDetails = (JSONObject) jsonParser.parse(reader);
			JSONObject serviceJson = (JSONObject) serviceDetails.get(service);
			return (String) serviceJson.get("status");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getQuery(String tag) throws org.json.simple.parser.ParseException {

		JSONParser jsonParser = new JSONParser();
		String fileloc = "src/test/resources/database/queries.json";
		try (FileReader reader = new FileReader(fileloc)) {
			JSONObject queryDetails = (JSONObject) jsonParser.parse(reader);
			JSONObject queryJson = (JSONObject) queryDetails.get(tag);
			return (String) queryJson.get("query");

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
			requestSpec.log().body();
			res = requestSpec.when().post();
			assertEquals("Request Successful", 201, res.getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getRequest(String path) throws org.json.simple.parser.ParseException {
		try {
			RequestSpecBuilder builder = new RequestSpecBuilder();
			builder.setBasePath(path);
			requestSpec = builder.build();
			requestSpec = RestAssured.given().spec(requestSpec);
			res = requestSpec.when().get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void assertResponseSuccess() {
		try {
			assertEquals("Request Successful", 201, res.getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkServices() throws org.json.simple.parser.ParseException {

		getRequest("/customers/1");
		if (res.getStatusCode() != 200) {
			System.out.println("========== Customer Service : Currently unavailable =========");
			updateStatusFile("customer", "NOK");
		} else {
			updateStatusFile("customer", "OK");
			System.out.println("========== Customer Service : Up and running =========");
		}
		getRequest("/accounts/1");
		if (res.getStatusCode() != 200) {
			System.out.println("========== Accounts Service : Currently unavailable =========");
			updateStatusFile("accounts", "NOK");
		} else {
			updateStatusFile("accounts", "OK");
			System.out.println("========== Accounts Service : Up and running =========");
		}
		getRequest("/transactions/1");
		if (res.getStatusCode() != 200) {
			System.out.println("========== Payments Service : Currently unavailable =========");
			updateStatusFile("payments", "NOK");
		} else {
			System.out.println("========== Payments Service : Up and running =========");
			updateStatusFile("payments", "OK");
		}
	}

	private static String retrieveGoldenDataDB(String query) throws ClassNotFoundException, IOException {
		System.out.println("============= Database Query =============");
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		InputStream input = new FileInputStream("src/test/resources/database/connection.properties");
		Properties prop = new Properties();
		prop.load(input);
		String url = prop.getProperty("url");
		String port = prop.getProperty("port");
		String user = prop.getProperty("user");
		String password = prop.getProperty("password");
		String instanceName = prop.getProperty("instanceName");
		String databaseName = prop.getProperty("databaseName");

		String connectionUrl = "jdbc:sqlserver://" + url + ":" + port + ";instance=" + instanceName + ";databaseName="
				+ databaseName + ";user=" + user + ";password=" + password;
		try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
			String SQL = query;
			ResultSet rs = stmt.executeQuery(SQL);

			List<String> resultList = new ArrayList<String>();
			while (rs.next()) {
				resultList.add(rs.getString(1));
			}
			return resultList.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void retrieveGoldenData(Collection<String> tags)
			throws org.json.simple.parser.ParseException, ClassNotFoundException {

		JSONParser jsonParser = new JSONParser();
		String fileloc = "src/test/resources/database/queries.json";

		for (String tag : tags) {

			String data = "";
			try (FileReader reader = new FileReader(fileloc)) {
				JSONObject serviceDetails = (JSONObject) jsonParser.parse(reader);
				if (serviceDetails.containsKey(tag)) {
					JSONObject serviceJson = (JSONObject) serviceDetails.get(tag);
					if (((String) serviceJson.get("data")).isEmpty()) {
						data = retrieveGoldenDataDB(getQuery(tag));
						serviceJson.put("data", data);
					} else
						data = (String) serviceJson.get("data");
					System.out.println("Query used     : " + getQuery(tag));
					System.out.println("Retrieved data : " + data);
					customercode = data;
					FileWriter file = new FileWriter(fileloc);
					file.write(serviceDetails.toJSONString());
					file.flush();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

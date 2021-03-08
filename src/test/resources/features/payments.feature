Feature: Execute Payment
  System should be able to process SEPA payments 
  
  @SeniorCitizen @customer1
  Scenario Outline: SEPA payments for a senior citizen
    Given I create a customer with "<FirstName>", "<LastName>", "<Age>","<EmailID>","<Nationality>","<Date>"
    And   I create an account of type "<AccountType>"
    When  I debit the account with "<TransactionAmount>" and credit internal account
    Then  The payment should be successful
    
    Examples:
      | FirstName | LastName | Age | EmailID        |Nationality|Date      |AccountType|TransactionAmount|
      | Steven    | Waugh    | 65  | Steve@test.com |Australia  |23-03-2020|current    |500              |
    
  @NonSeniorCitizen @customer2
  Scenario Outline: SEPA payments for a non-senior citizen
    Given I create a customer with "<FirstName>", "<LastName>", "<Age>","<EmailID>","<Nationality>","<Date>"
    And   I create an account of type "<AccountType>"
    When  I debit the account with "<TransactionAmount>" and credit internal account
    Then  The payment should be successful
    
    Examples:
      | FirstName | LastName | Age | EmailID        |Nationality|Date      |AccountType|TransactionAmount|
      | Mark      | Waugh    | 35  | Mark@test.com  |Australia  |23-03-2020|current    |900              | 
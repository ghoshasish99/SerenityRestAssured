# RestAssured with Cucumber - Serenity

[![RestAssured-Cucumber-Serenity](https://github.com/ghoshasish99/SerenityRestAssured/actions/workflows/maven.yml/badge.svg)](https://github.com/ghoshasish99/SerenityRestAssured/actions/workflows/maven.yml)

### Getting started:
3 main maven dependencies are as follows :

```xml
       <!-- serenity-core -->
        <dependency>
            <groupId>net.serenity-bdd</groupId>
            <artifactId>serenity-core</artifactId>
            <version>${serenity.version}</version>
        </dependency>
        <!-- serenity-cucumber -->
        <dependency>
            <groupId>net.serenity-bdd</groupId>
            <artifactId>serenity-cucumber</artifactId>
            <version>${serenity.cucumber.version}</version>
        </dependency>        
        <!-- serenity-rest-assured -->
        <dependency>
            <groupId>net.serenity-bdd</groupId>
            <artifactId>serenity-rest-assured</artifactId>
            <version>${serenity.version}</version>
        </dependency>
```
Working folder structure :
```
src
 |-- test
       |-- java
             |-- steps
                   |-- steps.java (individual steps are captured)
                   |-- stepdefinitions.java (stepdefinitions are captured)
             |-- TestRunner.java
       |-- resources
             |-- features
                    |-- .feature (feature files)
             |-- payloads
                    |-- .json (request payloads)
  |-- pom.xml                  

```
The TestRunner file is simple :
```java
import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;


import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features= "src/test/resources/features/payments.feature")

public class TestRunner {
	
}
```
You can study this repository for a detailed implementation
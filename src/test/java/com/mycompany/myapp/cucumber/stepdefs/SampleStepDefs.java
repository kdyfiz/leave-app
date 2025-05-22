package com.mycompany.myapp.cucumber.stepdefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SampleStepDefs {

    @Given("the application is running")
    public void theApplicationIsRunning() {
        // Add implementation
    }

    @When("I perform a sample action")
    public void iPerformASampleAction() {
        // Add implementation
    }

    @Then("I should see the expected result")
    public void iShouldSeeTheExpectedResult() {
        // Add implementation
    }
}

package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "stepdefinition",
                "base"             
        },
        plugin = {"pretty"},
        tags = "@confirm"
)
public class TestRunner extends AbstractTestNGCucumberTests {
}

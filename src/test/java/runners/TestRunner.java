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
        tags = "@confirm or @smoke"
)
public class TestRunner extends AbstractTestNGCucumberTests {
}

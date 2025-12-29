package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import utils.ConfigReader;

public class BaseTest {

  private final DriverManager driverManager;

    public BaseTest(DriverManager driverManager) {
        this.driverManager = driverManager;
    }

    @Before
    public void setupApi() {
        RestAssured.baseURI = ConfigReader.get("base.uri");
        System.out.println(">>> API BASE URI SET <<<");
    }

    public void setupUI() {
        System.out.println(">>> UI SETUP STARTED <<<");

        System.setProperty(
            "webdriver.chrome.driver",
            "C:/Users/DiRai/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe"
        );

        WebDriver driver = new ChromeDriver();
        driver.get(ConfigReader.get("url"));

        driverManager.setDriver(driver);
    }

    @After
    public void tearDown() {
        if (driverManager.getDriver() != null) {
            driverManager.getDriver().quit();
        }
    }
}
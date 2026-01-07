package page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import base.DriverManager;
import model.spotTradeModel;

public class SpotTradePage {

    private final DriverManager driverManager;
    private WebDriver driver;
    private boolean initialized = false;

    public SpotTradePage(DriverManager driverManager) {
        this.driverManager = driverManager;
    }

    private void initElements() {
        if (!initialized) {
            driver = driverManager.getDriver();

            if (driver == null) {
                throw new IllegalStateException(
                    "WebDriver is NULL. @Before hook did not run.");
            }

            PageFactory.initElements(driver, this);
            initialized = true;
        }
    }

    @FindBy(xpath = "(//label[text()='Buy'])[1]/following-sibling::input[1]")
    private WebElement buyInputField;

    @FindBy(xpath = "(//label[text()='Sell'])[1]/following-sibling::input[1]")
    private WebElement sellInputField;

    @FindBy(xpath = "(//label[text()='Amount'])[1]/following-sibling::input[1]")
    private WebElement amountInputField;

    @FindBy(xpath = "(//label[text()='Rate'])[1]/following-sibling::input[1]")
    private WebElement rateInputField;

    @FindBy(xpath = "//button[text()='Submit Spot']")
    private WebElement submitSpotButton;

    @FindBy(xpath = "//div[contains(text(),'Created trade id')]")
    private WebElement tradeIdText;

    public void submitSpotTrade(spotTradeModel tradeData) throws InterruptedException {
        initElements(); 
        buyInputField.clear();
        if (tradeData.getBuyCurrency() != null) {
            buyInputField.sendKeys(tradeData.getBuyCurrency());
        }
        sellInputField.clear();
        if (tradeData.getSellCurrency() != null) {
            sellInputField.sendKeys(tradeData.getSellCurrency());
        }
        amountInputField.clear();
        if (tradeData.getBuyAmount() != null) {
            amountInputField.sendKeys(tradeData.getBuyAmount().toString());
        }
        rateInputField.clear();
        if (tradeData.getRate() != null) {
            rateInputField.sendKeys(tradeData.getRate().toString());
        }
        submitSpotButton.click();
        Thread.sleep(10000);
    }

    public int getTradeId() {
        initElements();
        return Integer.parseInt(tradeIdText.getText().replaceAll("\\D+", ""));
    }
}
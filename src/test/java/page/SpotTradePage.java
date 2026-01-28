package page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import base.DriverManager;
import model.spotTradeModel;
import utils.ElementUtil;

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

    @FindBy(xpath = "//label[text()='Counterparty']/parent::div/select")
    private WebElement counterpartySelect;

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

        ElementUtil.selectElementByText(counterpartySelect, tradeData.getCounterparty().toUpperCase());
        submitSpotButton.click();
        Thread.sleep(10000);
    }

    public int getTradeId() {
        initElements();
        return Integer.parseInt(tradeIdText.getText().replaceAll("\\D+", ""));
    }

    /**
     * Attempt to click a Confirm button for the given trade id in the UI.
     * This uses a few flexible selectors to account for different DOM layouts.
     */
    public void confirmTradeById(int tradeId) throws InterruptedException {
        initElements();
        String idStr = String.valueOf(tradeId);
        boolean clicked = false;
        try {
            driver.findElement(org.openqa.selenium.By.xpath("//tr[td[text()='" + idStr + "']]//button[contains(.,'Confirm')]")).click();
            clicked = true;
            System.out.println("Clicked Confirm using row selector for trade " + idStr);
        } catch (Exception e) {
            System.out.println("Row selector click failed for trade " + idStr + ": " + e.getMessage());
        }

        if (!clicked) {
            try {
                driver.findElement(org.openqa.selenium.By.xpath("//button[contains(.,'Confirm') and contains(@data-id,'" + idStr + "')]")).click();
                clicked = true;
                System.out.println("Clicked Confirm using data-id selector for trade " + idStr);
            } catch (Exception e) {
                System.out.println("Data-id selector click failed for trade " + idStr + ": " + e.getMessage());
            }
        }

        if (!clicked) {
            try {
                org.openqa.selenium.WebElement el = driver.findElement(org.openqa.selenium.By.xpath("//button[contains(.,'Confirm')]"));
                try {
                    el.click();
                } catch (Exception clickEx) {
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                }
                clicked = true;
                System.out.println("Clicked Confirm using generic Confirm button for trade " + idStr);
            } catch (Exception e) {
                throw new IllegalStateException("Could not find a Confirm button for trade " + tradeId + ": " + e.getMessage(), e);
            }
        }
        Thread.sleep(500);

        try {
            org.openqa.selenium.Alert alert = driver.switchTo().alert();
            alert.accept();
            Thread.sleep(500);
        } catch (Exception ignored) { }

        //Try to click an OK button in a modal/dialog
        try {
            java.util.List<org.openqa.selenium.WebElement> okButtons = driver.findElements(org.openqa.selenium.By.xpath("//button[normalize-space(.)='OK' or normalize-space(.)='Ok' or normalize-space(.)='ok' or contains(.,'OK') or contains(.,'Ok')]") );
            if (!okButtons.isEmpty()) {
                for (org.openqa.selenium.WebElement b : okButtons) {
                    if (b.isDisplayed() && b.isEnabled()) {
                        b.click();
                        Thread.sleep(500);
                        break;
                    }
                }
            }
        } catch (Exception ignored) { }
        Thread.sleep(2000);
    }

    /**
     * Attempt to click a Cancel button for the given trade id in the UI.
     */
    public void cancelTradeById(int tradeId) throws InterruptedException {
        initElements();
        String idStr = String.valueOf(tradeId);
        boolean clicked = false;
        try {
            driver.findElement(org.openqa.selenium.By.xpath("//tr[td[text()='" + idStr + "']]//button[contains(.,'Cancel') or contains(.,'Cancel Trade')]")).click();
            clicked = true;
            System.out.println("Clicked Cancel using row selector for trade " + idStr);
        } catch (Exception e) {
            System.out.println("Row selector cancel click failed for trade " + idStr + ": " + e.getMessage());
        }

        if (!clicked) {
            try {
                driver.findElement(org.openqa.selenium.By.xpath("//button[contains(.,'Cancel') and contains(@data-id,'" + idStr + "')]")).click();
                clicked = true;
                System.out.println("Clicked Cancel using data-id selector for trade " + idStr);
            } catch (Exception e) {
                System.out.println("Data-id selector cancel click failed for trade " + idStr + ": " + e.getMessage());
            }
        }

        if (!clicked) {
            try {
                org.openqa.selenium.WebElement el = driver.findElement(org.openqa.selenium.By.xpath("//button[contains(.,'Cancel')]") );
                try { el.click(); } catch (Exception clickEx) { ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", el); }
                clicked = true;
                System.out.println("Clicked Cancel using generic Cancel button for trade " + idStr);
            } catch (Exception e) {
                throw new IllegalStateException("Could not find a Cancel button for trade " + tradeId + ": " + e.getMessage(), e);
            }
        }
        Thread.sleep(500);

        // Accept native alert if present
        try {
            org.openqa.selenium.Alert alert = driver.switchTo().alert();
            alert.accept();
            Thread.sleep(500);
        } catch (Exception ignored) { }

        // Click OK on modal if present
        try {
            java.util.List<org.openqa.selenium.WebElement> okButtons = driver.findElements(org.openqa.selenium.By.xpath("//button[normalize-space(.)='OK' or normalize-space(.)='Ok' or normalize-space(.)='ok' or contains(.,'OK') or contains(.,'Ok')]") );
            if (!okButtons.isEmpty()) {
                for (org.openqa.selenium.WebElement b : okButtons) {
                    if (b.isDisplayed() && b.isEnabled()) {
                        b.click();
                        Thread.sleep(500);
                        break;
                    }
                }
            }
        } catch (Exception ignored) { }

        Thread.sleep(2000);
    }

    /**
     * Try to read the trade status from the UI. Returns null if not found.
     */
    public String getTradeStatusById(int tradeId) {
        initElements();
        String idStr = String.valueOf(tradeId);
        String status = null;

        try {
            org.openqa.selenium.WebElement statusEl = driver.findElement(org.openqa.selenium.By.xpath("//tr[td[text()='" + idStr + "']]//td[contains(@class,'status') or contains(.,'Status')]") );
            status = statusEl.getText().trim();
        } catch (Exception ignored) { }

        if (status == null) {
            try {
                org.openqa.selenium.WebElement statusEl = driver.findElement(org.openqa.selenium.By.xpath("//*[contains(text(),'Status') and contains(.,'" + idStr + "')]/following-sibling::*[1]") );
                status = statusEl.getText().trim();
            } catch (Exception ignored) { }
        }

        if (status == null) {
            try {
                org.openqa.selenium.WebElement statusEl = driver.findElement(org.openqa.selenium.By.xpath("//*[contains(text(),'Status')]/ancestor::*[1]") );
                status = statusEl.getText().trim();
            } catch (Exception ignored) { }
        }

        if (status != null) {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("(NEW|CONFIRMED|CANCELLED|SETTLED)", java.util.regex.Pattern.CASE_INSENSITIVE).matcher(status);
            if (m.find()) {
                return m.group(1).toUpperCase();
            }
            return status;
        }

        return null;
    }
}
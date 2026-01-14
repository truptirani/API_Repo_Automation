package stepdefinition;

import static org.junit.Assert.assertEquals;

import api.TradeApi;
import base.BaseTest;
import endpoints.pdfConstants;
import endpoints.pdfFields;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import model.spotTradeModel;
import page.SpotTradePage;
import utils.JsonUtils;
import utils.PdfComparison;
import utils.TestDataUtil;

public class spotTrade {

    private final SpotTradePage spotTradePage;
    private final TradeApi tradeApi;
    private final BaseTest baseTest;
    private int tradeID;
    private String actualPdfPath;
    private int confirmationTradeId;

    public spotTrade(SpotTradePage spotTradePage, TradeApi tradeApi, BaseTest baseTest) {
        this.spotTradePage = spotTradePage;
        this.tradeApi = tradeApi;
        this.baseTest = baseTest;
    }

    @Given ("User is in the FX practice app page")
    public void user_is_in_the_fx_practice_app_page() {
        baseTest.setupUI();

    }

    @And("user creates a spot trade using {string}")
    public void user_creates_a_spot_trade_using(String fileName) throws InterruptedException {
        spotTradeModel tradeData
                = TestDataUtil.readJson("testdata/" + fileName, spotTradeModel.class);

        spotTradePage.submitSpotTrade(tradeData);
    }

    @When("I fetch the spot trade by id")
    public void i_fetch_the_trade_by_id() {
        tradeID = spotTradePage.getTradeId();
        System.out.println("Trade ID: " + tradeID);
    }

    @Then("the GET response status should be {int}")
    public void the_get_response_status_should_be(int statusCode) {
        Response response = TradeApi.getTradeResponse(String.valueOf(spotTradePage.getTradeId()));
        System.out.println(response.getStatusCode());
    }

    @Then("the returned trade should match the payload {string}")
    public void the_returned_trade_should_match_the_payload(String fileName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Response response = TradeApi.getTradeResponse(String.valueOf(spotTradePage.getTradeId()));
       spotTradeModel actualTrade = response.as(spotTradeModel.class);
        spotTradeModel expectedTrade
                = mapper.readValue(
                        JsonUtils.getJson(fileName), spotTradeModel.class);
        assertEquals(expectedTrade.getBuyCurrency(),actualTrade.getBuyCurrency());
        assertEquals(expectedTrade.getSellCurrency(), actualTrade.getSellCurrency());
        assertEquals(expectedTrade.getStatus(), actualTrade.getStatus());
        assertEquals(expectedTrade.getRate(),actualTrade.getRate());
        assertEquals(expectedTrade.getBuyAmount(), actualTrade.getBuyAmount());

    }

    @Then("generate the confirmation document using confirmation generation api")
    public void generate_confirmation_document_using_api() {
         confirmationTradeId =TradeApi.confirmationGeneationResponse(tradeID).jsonPath().getInt("id");
    }
    
     @And("fetch the generated pdf document using pdf fetch api")
    public void fetch_generated_pdf_document() {
         actualPdfPath = TradeApi.downloadPdf(confirmationTradeId, "Actual_SpotTradeConfirmation.pdf");
    }

     @And("Verify the content of generated pdf with the expected pdf")
    public void verify_the_content_of_generated_pdf_with_the_expected_pdf() {
        String expectedpdfPath = pdfConstants.EXPECTED_PDF_BASE_PATH + "/Expected_SpotTradeConfirmation.pdf";
       PdfComparison.validatePdfFields(actualPdfPath, expectedpdfPath, pdfFields.SPOT_TRADE_CONFIRMATION);

    }
}

package stepdefinition;
import org.junit.Assert;

import api.TradeApi;
import endpoints.pdfConstants;
import endpoints.pdfFields;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import model.forwardTradeModel;
import utils.PdfComparison;
import utils.TestDataUtil;

public class forwardTrade {

    private final TradeApi tradeApi;
    private Response postResponse;
    private int tradeId;
    private int confirmationTradeId;
    private String actualPdfPath;

    public forwardTrade(TradeApi tradeApi) {
        this.tradeApi = tradeApi;
    }

    @Given("user creates a forward trade post request")
    public void user_creates_a_forward_trade_post_request(){
         forwardTradeModel payload = TestDataUtil.readJson("testdata/forwardTrade.json", forwardTradeModel.class); 
            postResponse = TradeApi.createforwardTrade(payload);
            tradeId = postResponse.jsonPath().getInt("id");

    }
    @When("I fetch the forward trade by id")
    public void i_fetch_the_forward_trade_by_id() {
            System.out.println("Forward Trade ID: " + tradeId);
    }

    @Then("the GET response of forward trade status should be {int}")
    public void the_get_response_of__status_should_be(int statusCode) {
      Response getResponse = tradeApi.getTradeResponse(tradeId);
         Assert.assertEquals(200, getResponse.getStatusCode());
    }

    @Then("the returned forward trade should match the payload")
    public void the_returned_forward_trade_should_match_the_payload() {
        forwardTradeModel expectedResponse = postResponse.as(forwardTradeModel.class);
        Assert.assertEquals(expectedResponse.getBuyCurrency(),postResponse.jsonPath().getString("buyCurrency"));
        Assert.assertEquals(expectedResponse.getBuyAmount(),postResponse.jsonPath().getString("buyAmount"));
        Assert.assertEquals(expectedResponse.getRate(),postResponse.jsonPath().getString("rate"));
        Assert.assertEquals(expectedResponse.getSellCurrency(),postResponse.jsonPath().getString("sellCurrency"));
        Assert.assertEquals(expectedResponse.getStatus(),postResponse.jsonPath().getString("status"));
        Assert.assertEquals(expectedResponse.getTradeType(),postResponse.jsonPath().getString("tradeType"));

    }

    @Then("generate the confirmation document for forward trade using confirmation generation api")
    public void generate_confirmation_document_for_forward_trade_using_api() {
         confirmationTradeId =TradeApi.confirmationGeneationResponse(tradeId).jsonPath().getInt("id");
    }
    
     @And("fetch the generated pdf document for forward trade using pdf fetch api")
    public void fetch_generated_pdf_document_for_forward_trade() {
         actualPdfPath = TradeApi.downloadPdf(confirmationTradeId, "Actual_ForwardTradeConfirmation.pdf");
    }

    @Then("Verify the content of generated pdf with the expected pdf for forward trade")
     public void verify_the_content_of_generated_pdf_with_the_expected_pdf_for_forward_trade() {
     String expectedpdfPath = pdfConstants.EXPECTED_PDF_BASE_PATH + "/Expected_ForwardTradeConfirmation.pdf";
     PdfComparison.validatePdfFields(actualPdfPath, expectedpdfPath, pdfFields.FORWARD_TRADE_CONFIRMATION);
}

  }
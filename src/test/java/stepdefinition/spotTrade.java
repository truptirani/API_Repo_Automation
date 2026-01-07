package stepdefinition;

import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


import api.TradeApi;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import io.restassured.response.Response;
import model.spotTradeModel;
import page.SpotTradePage;
import utils.JsonUtils;
import utils.TestDataUtil;
import base.BaseTest;

public class spotTrade {

    private final SpotTradePage spotTradePage;
    private final TradeApi tradeApi;
    private final BaseTest baseTest;
    private static Response lastResponse;


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

        // For invalid payloads (validation tests) we post directly to the API
        if ("invalidTrade.json".equals(fileName)) {
            String payload = JsonUtils.getJson(fileName);
            lastResponse = TradeApi.createSpotTrade(payload);
        } else {
            spotTradePage.submitSpotTrade(tradeData);
        }
    }

    @When("I fetch the spot trade by id")
    public void i_fetch_the_trade_by_id() {
        int tradeID = spotTradePage.getTradeId();
        System.out.println("Trade ID: " + tradeID);
        lastResponse = TradeApi.getTradeResponse(String.valueOf(tradeID));
    }

    @When("I fetch the spot trade by id {string}")
    public void i_fetch_the_spot_trade_by_id(String tradeId) {
        lastResponse = TradeApi.getTradeResponse(tradeId);
        
    }

    @Then("the GET response status should be {int}")
    public void the_get_response_status_should_be(int statusCode) {
        Response response;
        if (lastResponse != null) {
            response = lastResponse;
        } else {
            response = TradeApi.getTradeResponse(String.valueOf(spotTradePage.getTradeId()));
        }
        System.out.println(response.getStatusCode());
        assertEquals(statusCode, response.getStatusCode());
    }

     @Then("the POST response status should be {int}")
    public void the_post_response_status_should_be(int statusCode) {
        assertThat("No POST response recorded", lastResponse, notNullValue());
        System.out.println("POST response status: " + lastResponse.getStatusCode());
        System.out.println("POST response body: " + lastResponse.getBody().asString());
        assertEquals(statusCode, lastResponse.getStatusCode());
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

    @Then("the error message should contain {string}")
    public void the_error_message_should_contain(String expectedSubstring) {
        assertThat("No response recorded", lastResponse, notNullValue());
        
        String message = null;
        try {
            message = lastResponse.jsonPath().getString("message");
        } catch (Exception ignored) { }
        if (message != null) {
            assertThat(message, containsString(expectedSubstring));
        } else {
            assertThat(lastResponse.getBody().asString(), containsString(expectedSubstring));
        }
    }

}

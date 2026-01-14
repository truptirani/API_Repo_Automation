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

    private static java.util.Map<String, String> previousStatus = new java.util.HashMap<>();

    @When("I confirm the trade with ID {string}")
    public void i_confirm_the_trade_with_id_frontend(String tradeId) throws InterruptedException {
        int id = Integer.parseInt(tradeId);

        String prev = spotTradePage.getTradeStatusById(id);
        if (prev == null) {
            try {
                Response respBefore = TradeApi.getTradeResponse(tradeId);
                if (respBefore != null && respBefore.getStatusCode() < 500) {
                    try {
                        spotTradeModel t = respBefore.as(spotTradeModel.class);
                        prev = t.getStatus();
                    } catch (Exception ignored) { }
                } else if (respBefore != null) {
                    System.out.println("GET before confirm returned status " + respBefore.getStatusCode() + ": " + respBefore.getBody().asString());
                }
            } catch (Exception ignored) { }
        }
        if (prev != null) {
            previousStatus.put(tradeId, prev);
        }

        spotTradePage.confirmTradeById(id);

        String observed = waitForStatus(tradeId, null, 15);
        System.out.println("Observed status after confirm: " + observed);

        lastResponse = TradeApi.getTradeResponse(tradeId);
    }

    @When("I cancel the trade with ID {string}")
    public void i_cancel_the_trade_with_id_frontend(String tradeId) throws InterruptedException {
        int id = Integer.parseInt(tradeId);

        String prev = spotTradePage.getTradeStatusById(id);
        if (prev == null) {
            try {
                Response respBefore = TradeApi.getTradeResponse(tradeId);
                if (respBefore != null && respBefore.getStatusCode() < 500) {
                    try { spotTradeModel t = respBefore.as(spotTradeModel.class); prev = t.getStatus(); } catch (Exception ignored) {}
                }
            } catch (Exception ignored) { }
        }
        if (prev != null) previousStatus.put(tradeId, prev);

        spotTradePage.cancelTradeById(id);

        String observed = waitForStatus(tradeId, "CANCELLED", 15);
        System.out.println("Observed status after cancel: " + observed);

        lastResponse = TradeApi.getTradeResponse(tradeId);
    }

    /**
     * Poll the trade GET endpoint until the status equals expectedStatus (if provided),
     * or until timeout. Returns the latest observed status (or null if never observed).
     */
    private String waitForStatus(String tradeId, String expectedStatus, int timeoutSeconds) throws InterruptedException {
        int waited = 0;
        String lastStatus = null;
        while (waited < timeoutSeconds * 1000) {
            Response resp = TradeApi.getTradeResponse(tradeId);
            if (resp == null) {
                System.out.println("GET returned null response for trade " + tradeId);
            } else {
                int code = resp.getStatusCode();
                System.out.println("GET /trades/" + tradeId + " status=" + code);
                if (code >= 500) {
                    System.out.println("Server error on GET: " + resp.getBody().asString());
                } else {
                    try {
                        spotTradeModel m = resp.as(spotTradeModel.class);
                        if (m != null) {
                            lastStatus = m.getStatus();
                            if (lastStatus != null) {
                                if (expectedStatus == null || expectedStatus.equalsIgnoreCase(lastStatus)) {
                                    return lastStatus;
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to parse GET response: " + e.getMessage());
                    }
                }
            }
            Thread.sleep(1000);
            waited += 1000;
        }
        return lastStatus;
    }


    @Then("the trade status for ID {string} should be {string} from {string}")
    public void the_trade_status_for_ID_should_be_from(String tradeId, String expectedStatus, String fromStatus) throws InterruptedException {
        String prev = previousStatus.get(tradeId);
        if (prev != null) {
            assertEquals("Previous status mismatch", fromStatus, prev);
        }

        int id = Integer.parseInt(tradeId);
        String uiStatus = spotTradePage.getTradeStatusById(id);
        System.out.println("UI status immediately after confirm: " + uiStatus);
        String actual = waitForStatus(tradeId, expectedStatus, 30);

        if (actual == null) {
            uiStatus = spotTradePage.getTradeStatusById(id);
            System.out.println("UI status on fallback read: " + uiStatus);
            actual = uiStatus;
        }

        System.out.println("Final observed status for " + tradeId + " = " + actual);
        assertEquals("Trade status did not update as expected", expectedStatus, actual);
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

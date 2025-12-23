package stepdefinition;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import utils.JsonUtils;
import api.TradeApi;

public class tradeLifecycle {
   

    private String tradeId;

    @Given("user creates a trade using {string}")
    public void create_trade(String file) {
        tradeId = TradeApi.createTrade(JsonUtils.getJson(file));
    }

    @When("user amends the trade using {string}")
    public void amend_trade(String file) {
        TradeApi.amendTrade(tradeId, JsonUtils.getJson(file));
    }

    @When("user confirms the trade")
    public void confirm_trade() {
        TradeApi.confirmTrade(tradeId);
    }

    @Then("user settles the trade using {string}")
    public void settle_trade(String file) {
        TradeApi.settleTrade(tradeId, JsonUtils.getJson(file));
    }
}



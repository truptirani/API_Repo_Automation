package stepdefinition;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import api.TradeApi;
import model.forwardTradeModel;
import utils.TestDataUtil;
import io.restassured.response.Response;
import org.junit.Assert;

public class forwardTrade {

    private final TradeApi tradeApi;
    private Response postResponse;
    private String tradeId;

    public forwardTrade(TradeApi tradeApi) {
        this.tradeApi = tradeApi;
    }

    @Given("user creates a forward trade post request")
    public void user_creates_a_forward_trade_post_request(){
         forwardTradeModel payload = TestDataUtil.readJson("testdata/forwardTrade.json", forwardTradeModel.class); 
            postResponse = TradeApi.createforwardTrade(payload);
            tradeId = postResponse.jsonPath().getString("id");

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
}
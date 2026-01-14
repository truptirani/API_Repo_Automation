Feature: FX Trade Confirmation and Cancellation

 
  @confirm
  Scenario: Confirm a Spot Trade
    Given User is in the FX practice app page
    And user creates a spot trade using "spotTrade.json"
    When I fetch the spot trade by id
    When I confirm the trade with ID "1"
    Then the trade status for ID "1" should be "CONFIRMED" from "NEW"

   @confirm
    Scenario: Cancel a Spot Trade
    Given User is in the FX practice app page
    And user creates a spot trade using "spotTrade.json"
    When I fetch the spot trade by id
    When I cancel the trade with ID "8"
    Then the trade status for ID "8" should be "CANCELLED" from "NEW"
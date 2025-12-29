Feature: Spot trade API — create and verify

@smoke
  Scenario: Create a spot trade and verify it is stored
    Given User is in the FX practice app page
    And user creates a spot trade using "spotTrade.json"
    When I fetch the spot trade by id
    Then the GET response status should be 200
    And the returned trade should match the payload "spotTrade.json"


@smoke
  Scenario: Create a forward trade and verify it is stored
    Given user creates a forward trade post request
    When I fetch the forward trade by id
    Then the GET response of forward trade status should be 200
    And the returned forward trade should match the payload

    
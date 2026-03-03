Feature: LucyEvent API testing

  @LucyEvent
  Scenario: To verify that after POST creating new LucyEvent
    Given I have a LucyEvent
    When Create New LucyEvent
    Then The LucyEvent response should contain "id"
    Then Validate API ES and DB for LucyEventId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The LucyEvent response status code should be 201

  @LucyEvent
  Scenario: To verify that GET request Fetched All data
    When Fetch All LucyEvent Details
    Then The LucyEvent response status code should be 200

  @LucyEvent
  Scenario: To verify that GET with ID Fetched Correct data
    When Fetch LucyEvent Details with ID
    Then The LucyEvent response status code should be 200

  @LucyEvent
  Scenario: TO verify that after PUT request Data should get update in DataBase
    When Update LucyEvent Details
    Then Validate API ES and DB for LucyEventId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The LucyEvent response status code should be 200

  @LucyEvent
  Scenario: To verify that After PAtch request data should get update in DataBase
    When Update Patch LucyEvent
    Then Validate API ES and DB for LucyEventId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The LucyEvent response status code should be 200

  @LucyEvent
  Scenario: To verify that after Delete Request data should get reoved from DataBase
    When Delete LucyEvent with ID
    Then The LucyEvent response status code should be 204

  @Negative_Scenarioes_for_LucyEvent_entity
  Scenario: To verify that GET with ID Fetched Correct data (Which is already deleted)
    When Fetch LucyEvent Details with ID
    Then The LucyEvent response status code should be 200

  @Negative_Scenarioes_for_LucyEvent_entity
  Scenario: TO verify that after PUT request Data should get update in DataBase (Which is already deleted)
    When Update LucyEvent Details
    Then The LucyEvent response status code should be 200

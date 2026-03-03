Feature: Environment API testing

  @Environment
  Scenario: To verify that creating new Environment
    Given I have a Environment
    When Create New Environment
    Then The Environment response should contain "id"
    Then Validate API ES and DB for EnvironmentId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The Environment response status code should be 201

  @Environment
  Scenario: To verify that GET request fetched all data
    When Fetch All Environment Details
    Then The Environment response status code should be 200

  @Environment
  Scenario: To verify that GET with ID request fetched Correct data
    When Fetch Environment Details with ID
    Then The Environment response status code should be 200

  @Environment
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update Environment Details
    Then Validate API ES and DB for EnvironmentId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The Environment response status code should be 200

  @Environment
  Scenario: To verify that after Patch request data sjould get update in DataBase
    When Update Patch Environment
    Then Validate API ES and DB for EnvironmentId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The Environment response status code should be 200

  @Environment
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Environment with ID
    Then The Environment response status code should be 204

  @Negative_Scenarioes_for_Environment_entity
  Scenario: To verify that GET with ID request fetched Correct data (Which is  already deleted)
    When Fetch Environment Details with ID
    Then The Environment response status code should be 200

  @Negative_Scenarioes_for_Environment_entity
  Scenario: To verify that after PUT request data should get update in DataBase (Which is  already deleted)
    When Update Environment Details
    Then Validate API ES and DB for EnvironmentId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The Environment response status code should be 200

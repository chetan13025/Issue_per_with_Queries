Feature: RootCause API testing

  @RootCause
  Scenario: To verify that after POST Creating new RootCause
    Given I have a RootCause
    When Create New RootCause
    Then The RootCause response should contain "id"
    Then Validate API ES and DB for RootCauseId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The RootCause response status code should be 201

  @RootCause
  Scenario: To verify that GET request fetched all data
    When Fetch All RootCause Details
    Then The RootCause response status code should be 200

  @RootCause
  Scenario: To verify that GET with ID Fetched Correct data
    When Fetch RootCause Details with ID
    Then The RootCause response status code should be 200

  @RootCause
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update RootCause Details
    Then Validate API ES and DB for RootCauseId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The RootCause response status code should be 200

  @RootCause
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch RootCause
    Then Validate API ES and DB for RootCauseId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The RootCause response status code should be 200

  @RootCause
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete RootCause with ID
    Then The RootCause response status code should be 204

  @Negative_Scenarioes_for_RootCause_entity
  Scenario: To verify that GET with ID Fetched Correct data (Which is already deleted)
    When Fetch RootCause Details with ID
    Then The RootCause response status code should be 200

  @Negative_Scenarioes_for_RootCause_entity
  Scenario: To verify that after PUT request data should get update in DataBase (Which is already deleted)
    When Update RootCause Details
    Then Validate API ES and DB for RootCauseId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The RootCause response status code should be 200

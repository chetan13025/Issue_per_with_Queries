Feature: Occurrence API testing

  @Occurrence
  Scenario: To verify that after POST creating new Occurrence
    Given I have a Occurrence
    When Create New Occurrence
    Then The Occurrence response should contain "id"
    Then Validate API ES and DB for OccurrenceId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The Occurrence response status code should be 201

  @Occurrence
  Scenario: T verify that GET request fetched all data
    When Fetch All Occurrence Details
    Then The Occurrence response status code should be 200

  @Occurrence
  Scenario: To verify that GET with ID Fetched Correct data
    When Fetch Occurrence Details with ID
    Then The Occurrence response status code should be 200

  @Occurrence
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update Occurrence Details
    Then Validate API ES and DB for OccurrenceId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The Occurrence response status code should be 200

  @Occurrence
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch Occurrence
    Then Validate API ES and DB for OccurrenceId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The Occurrence response status code should be 200

  @Occurrence
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Occurrence with ID
    Then The Occurrence response status code should be 204

  @Negative_Scenarioes_for_Occurrence_entity
  Scenario: To verify that GET with ID Fetched Correct data (Which is already deleted)
    When Fetch Occurrence Details with ID
    Then The Occurrence response status code should be 200

  @Negative_Scenarioes_for_Occurrence_entity
  Scenario: To verify that after PUT request data should get update in DataBase (Which is already deleted)
    When Update Occurrence Details
    Then The Occurrence response status code should be 200

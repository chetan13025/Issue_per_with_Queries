Feature: Worklog API testing

  @Worklog
  Scenario: To verify that after POST creating new Worklog
    Given I have a Worklog
    When Create New Worklog
    Then The Worklog response should contain "id"
    Then Validate API ES and DB for WorklogId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The Worklog response status code should be 201

  @Worklog
  Scenario: To verify that  GET request fetched all data
    When Fetch Worklog Details with ID
    Then The Worklog response status code should be 200

  @Worklog
  Scenario: To verify that GET with ID Fetched Correct Data
    When Fetch Worklog Details with ID
    Then The Worklog response status code should be 200

  @Worklog
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update Worklog Details
    Then Validate API ES and DB for WorklogId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The Worklog response status code should be 200

  @Worklog
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch Worklog
    Then Validate API ES and DB for WorklogId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The Worklog response status code should be 200

  @Worklog
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Worklog with ID
    Then The Worklog response status code should be 204

  @Negative_Scenarioes_for_Worklog_entity
  Scenario: To verify that GET with ID Fetched Correct Data (Which is already deleted)
    When Fetch Worklog Details with ID
    Then The Worklog response status code should be 200

  @Negative_Scenarioes_for_Worklog_entity
  Scenario: To verify that after PUT request data should get update in DataBase (Which is already deleted)
    When Update Worklog Details
    Then The Worklog response status code should be 200

Feature: IssueLog API testing

  @IssueLog
  Scenario: To verify that after POST request  New IssueLog should create
    Given I have a IssueLog
    When Create New IssueLog
    Then The IssueLog response should contain "id"
    Then Validate API ES and DB for IssueLogId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,eventdate" excludeFromEs "deleted"
    And The IssueLog response status code should be 201

  @IssueLog
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueLog Details
    Then The IssueLog response status code should be 200

  @IssueLog
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch IssueLog Details with ID
    Then The IssueLog response status code should be 200

  @IssueLog
  Scenario: To verify that after PUT request data should get update in Database
    When Update IssueLog Details
    Then Validate API ES and DB for IssueLogId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,eventdate" excludeFromEs "deleted"
    Then The IssueLog response status code should be 200

  @IssueLog
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch IssueLog
    Then Validate API ES and DB for IssueLogId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,eventdate" excludeFromEs "deleted"
    Then The IssueLog response status code should be 200

  @IssueLog
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete IssueLog with ID
    Then The IssueLog response status code should be 204

  @Negative_scenarioes_for_IssueLog_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch IssueLog Details with ID
    Then The IssueLog response status code should be 200

  @Negative_scenarioes_for_IssueLog_entity
  Scenario: To verify that after PUT request data should get update in Database (Which is  already deleted)
    When Update IssueLog Details
    Then The IssueLog response status code should be 200

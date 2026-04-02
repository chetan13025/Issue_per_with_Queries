Feature: IssueRootCause API testing

  @IssueRootCause
  Scenario: To verify that after POST request  New Issue should create
    Given I have a IssueRootCause
    When Create New IssueRootCause
    Then The IssueRootCause response should contain "id"
    Then Validate API ES and DB for IssueRootCauseId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The IssueRootCause response status code should be 201

  @IssueRootCause
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueRootCause Details
    Then The IssueRootCause response status code should be 200

  @IssueRootCause
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch IssueRootCause Details with ID
    Then The IssueRootCause response status code should be 200

  @IssueRootCause
  Scenario: To verify that after PUT request data should get update in Database
    When Update IssueRootCause Details
    Then Validate API ES and DB for IssueRootCauseId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueRootCause response status code should be 200

  @IssueRootCause
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch IssueRootCause
    Then Validate API ES and DB for IssueRootCauseId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueRootCause response status code should be 200

  @Custom_Queries_for_IssueRootCause
  Scenario: To verify that after GET request FindByIssueId
    When FindByIssueId For IssueRootCause
    Then The IssueRootCause response status code should be 200

  @IssueRootCause
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete IssueRootCause with ID
    Then The IssueRootCause response status code should be 204

  @Negative_scenarioes_for_IssueRootCause_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch IssueRootCause Details with ID
    Then The IssueRootCause response status code should be 200

  @Negative_scenarioes_for_IssueRootCause_entity
  Scenario: To verify that after PUT request data should get update in Database (Which is  already deleted)
    When Update IssueRootCause Details
    Then The IssueRootCause response status code should be 200

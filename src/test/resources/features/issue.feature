Feature: Issue API testing

  @Issue
  Scenario: To verify that after POST request  New Issue should create
    Given I have a Issue
    When Create New Issue
    Then The Issue response should contain "id"
    Then Validate API ES and DB for IssueId after POST skipping ES true excludingEverywhere "code" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
    And The Issue response status code should be 201

  @Issue
  Scenario: To verify GET request that Fetched all data
    When Fetch All Issues Details
    Then The Issue response status code should be 200

  @Issue
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch Issue Details with ID
    Then The Issue response status code should be 200

  @Issue
  Scenario: To verify that after PUT request data should get update in Database
    When Update Issue Details
    Then Validate API ES and DB for IssueId after PUT skipping ES true excludingEverywhere "code" excludeFromRequest "id,deleted,createdate,updateDate" excludeFromEs "deleted"
    Then The Issue response status code should be 200

  @Issue
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch Issue
    Then Validate API ES and DB for IssueId after PATCH skipping ES true excludingEverywhere "code" excludeFromRequest "id,deleted,createdate,updateDate" excludeFromEs "deleted"
    Then The Issue response status code should be 200

  @Custom_Queries_for_Issue
  Scenario: To verify that after GET request FinfBySprintId
    When FindBySprintId For Issue
    Then The Issue response status code should be 200

  @Custom_Queries_for_Issue
  Scenario: To verify that after GET request FindByTitleOrId
    When FindTitleOrId For Issue
    Then The Issue response status code should be 200

  @Issue
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Issue with ID
    Then The Issue response status code should be 204

  @Negative_scenarioes_for_Issue_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch Issue Details with ID
    Then The Issue response status code should be 200

  @Negative_scenarioes_for_Issue_entity
  Scenario: To verify that after PUT request data should get update in Database (Which is  already deleted)
    When Update Issue Details
    Then The Issue response status code should be 200

Feature: IssueComments API testing

  @IssueComment
  Scenario: To verify that after POST request  New Issue should create
    Given I have a IssueComment
    When Create New IssueComment
    Then The IssueComment response should contain "id"
    Then Validate API ES and DB for IssueCommentsId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate" excludeFromEs "deleted"
    And The IssueComment response status code should be 201

  @IssueComment
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueComments Details
    Then The IssueComment response status code should be 200

  @IssueComment
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch IssueComment Details with ID
    Then The IssueComment response status code should be 200

  @IssueComment
  Scenario: To verify that after PUT request data should get update in Database
    When Update IssueComment Details
    Then Validate API ES and DB for IssueCommentsId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate" excludeFromEs "deleted"
    Then The IssueComment response status code should be 200

  @IssueComment
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch IssueComment
    Then Validate API ES and DB for IssueCommentsId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate" excludeFromEs "deleted"
    Then The IssueComment response status code should be 200

  @IssueComment
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete IssueComment with ID
    Then The IssueComment response status code should be 204

  @Negative_scenarioes_for_IssueComment_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch IssueComment Details with ID
    Then The IssueComment response status code should be 200

  @Negative_scenarioes_for_IssueComment_entity
  Scenario: To verify that after PUT request data should get update in Database (Which is  already deleted)
    When Update IssueComment Details
    Then Validate API ES and DB for IssueCommentsId after PUT skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted,createdate" excludeFromEs "deleted"
    Then The IssueComment response status code should be 200

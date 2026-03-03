Feature: IssueGroup API testing

  @IssueGroup
  Scenario: To verify that after POST request  New IssueGroup should create
    Given I have a IssueGroup
    When Create New IssueGroup
    Then The IssueGroup response should contain "id"
    Then Validate API ES and DB for IssueGroupId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "id,deleted"
    And The IssueGroup response status code should be 201

  @IssueGroup
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueGroups Details
    Then The IssueGroup response status code should be 200

  @IssueGroup
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch IssueGroup Details with ID
    Then The IssueGroup response status code should be 200

  @IssueGroup
  Scenario: To verify that after PUT request data should get update in Database
    When Update IssueGroup Details
    Then Validate API ES and DB for IssueGroupId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "id,deleted"
    Then The IssueGroup response status code should be 200

  @IssueGroup
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch Issuegroup
    Then Validate API ES and DB for IssueGroupId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "id,deleted"
    Then The IssueGroup response status code should be 200

  @IssueGroup
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete IssueGroup with ID
    Then The IssueGroup response status code should be 204

  @Negative_scenarioes_for_IssueGroup_entity
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch IssueGroup Details with ID
    Then The IssueGroup response status code should be 200

  @Negative_scenarioes_for_IssueGroup_entity
  Scenario: To verify that after PUT request data should get update in Database
    When Update IssueGroup Details
    Then The IssueGroup response status code should be 200

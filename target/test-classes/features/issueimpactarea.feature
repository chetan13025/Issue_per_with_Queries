Feature: IssueImpactArea API testing

  @IssueImpactArea
  Scenario: To verify that after POST request  New IssueImpactArea should create
    Given I have a IssueImapactArea
    When Create New IssueImpactArea
    Then The IssueImpactArea response should contain "id"
    Then Validate API ES and DB for IsueImpactAreaId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The IssueImpactArea response status code should be 201

  @IssueImpactArea
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueImpactArea Details
    Then The IssueImpactArea response status code should be 200

  @IssueImpactArea
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch IssueImpactArea Details with ID
    Then The IssueImpactArea response status code should be 200

  @IssueImpactArea
  Scenario: To verify that after PUT request data should get update in Database
    When Update IssueImpactArea Details
    Then Validate API ES and DB for IsueImpactAreaId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueImpactArea response status code should be 200

  @IssueImpactArea
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch IssueImpactArea
    Then Validate API ES and DB for IsueImpactAreaId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueImpactArea response status code should be 200

  @Custom_Queries_for_IssueImpactArea_entity
  Scenario: To verify that after GET request FindByIssueId
    When FindByIssueId for Issueimpactarea
    Then The IssueImpactArea response status code should be 200

  @IssueImpactArea
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete IssueImpactArea with ID
    Then The IssueImpactArea response status code should be 204

  @Negative_scenarioes_for_IssueImpactArea_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch IssueImpactArea Details with ID
    Then The IssueImpactArea response status code should be 200

  @Negative_scenarioes_for_IssueImpactArea_entity
  Scenario: To verify that after PUT request data should get update in Database (Which is  already deleted)
    When Update IssueImpactArea Details
    Then The IssueImpactArea response status code should be 200

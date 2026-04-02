Feature: IssueFeatureStatistics API testing

  @IssueFeatureStatistics
  Scenario: To verify that after POST request  New IssueFeatureStatistics should create
    Given I have a IssueFeatureStatistics
    When Create New IssueFeatureStatistics
    Then The IssueFeatureStatistics response should contain "id"
    Then Validate API ES and DB for IssueFeatureStatisticsID after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
    And The IssueFeatureStatistics response status code should be 201

  @IssueFeatureStatistics
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueFeatureStatistics Details
    Then The IssueFeatureStatistics response status code should be 200

  @IssueFeatureStatistics
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch IssueFeatureStatistics Details with ID
    Then The IssueFeatureStatistics response status code should be 200

  @IssueFeatureStatistics
  Scenario: To verify that after PUT request data should get update in Database
    When Update IssueFeatureStatistics Details
    Then Validate API ES and DB for IssueFeatureStatisticsID after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
    Then The IssueFeatureStatistics response status code should be 200

  @IssueFeatureStatistics
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch IssueFeatureStatistics
    Then Validate API ES and DB for IssueFeatureStatisticsID after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
    Then The IssueFeatureStatistics response status code should be 200

  @Custom_queries_for_IssueFeatureStatistics
  Scenario: To verify that after GET request FindByProjectId
    When FindByIssueId for Issuefeaturestatistics
    Then The IssueFeatureStatistics response status code should be 200

  @IssueFeatureStatistics
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete IssueFeatureStatistics with ID
    Then The IssueFeatureStatistics response status code should be 204

  @Negative_scenarioes_for_IssueFeatureStatistics_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch IssueFeatureStatistics Details with ID
    Then The IssueFeatureStatistics response status code should be 200

  @Negative_scenarioes_for_IssueFeatureStatistics_entity
  Scenario: To verify that after PUT request data should get update in Database (Which is  already deleted)
    When Update IssueFeatureStatistics Details
    Then The IssueFeatureStatistics response status code should be 200

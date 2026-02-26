@IssueFeatureStatistics

Feature: IssueFeatureStatistics API testing

Scenario: Creating New IssueFeatureStatistics
Given I have a IssueFeatureStatistics
When Create New IssueFeatureStatistics
Then The IssueFeatureStatistics response should contain "id"
Then Validate API ES and DB for IssueFeatureStatisticsID after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
And The IssueFeatureStatistics response status code should be 201

Scenario: Fetching All IssueFeatureStatistics Details
When Fetch All IssueFeatureStatistics Details
Then The IssueFeatureStatistics response status code should be 200

Scenario: Fetching IssueFeatureStatistics Details with ID
When Fetch IssueFeatureStatistics Details with ID
Then The IssueFeatureStatistics response status code should be 200

Scenario: Updating PUT IssueFeatureStatistics Details with ID
When Update IssueFeatureStatistics Details
Then Validate API ES and DB for IssueFeatureStatisticsID after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
Then The IssueFeatureStatistics response status code should be 200

Scenario: Updating Patch IssueFeatureStatistics Details with ID
When Update Patch IssueFeatureStatistics
Then Validate API ES and DB for IssueFeatureStatisticsID after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
Then The IssueFeatureStatistics response status code should be 200

Scenario: Deleting IssueFeatureStatistics Details with ID
When Delete IssueFeatureStatistics with ID
Then The IssueFeatureStatistics response status code should be 204





 

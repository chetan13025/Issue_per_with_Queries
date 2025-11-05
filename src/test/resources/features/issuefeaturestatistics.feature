@IssueFeatureStatistics

Feature: IssueFeatureStatistics API testing

Scenario: Creating New IssueFeatureStatistics
Given I have a IssueFeatureStatistics
When Create New IssueFeatureStatistics
Then The IssueFeatureStatistics response should contain "id"
And The IssueFeatureStatistics response status code should be 201

Scenario: Fetching All IssueFeatureStatistics Details
When Fetch All IssueFeatureStatistics Details
Then The IssueFeatureStatistics response status code should be 200

Scenario: Fetching IssueFeatureStatistics Details with ID
When Fetch IssueFeatureStatistics Details with ID
Then The IssueFeatureStatistics response status code should be 200

Scenario: Updating PUT IssueFeatureStatistics Details with ID
When Update IssueFeatureStatistics Details
Then The IssueFeatureStatistics response status code should be 200

Scenario: Updating Patch IssueFeatureStatistics Details with ID
When Update Patch IssueFeatureStatistics
Then The IssueFeatureStatistics response status code should be 200

Scenario: Deleting IssueFeatureStatistics Details with ID
When Delete IssueFeatureStatistics with ID
Then The IssueFeatureStatistics response status code should be 204





 
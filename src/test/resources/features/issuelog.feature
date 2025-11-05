@IssueLog

Feature: issueLog API testing

Scenario: Creating New IssueLog
Given I have a IssueLog
When Create New IssueLog
Then The IssueLog response should contain "id"
And The IssueLog response status code should be 201

Scenario: Fetching All IssueLogs Details
When Fetch All IssueLog Details
Then The IssueLog response status code should be 200

Scenario: Fetching IssueLogs Details with  ID
When Fetch IssueLog Details with ID
Then The IssueLog response status code should be 200

Scenario: Updating PUT IssueLog Details with ID
When Update IssueLog Details
Then The IssueLog response status code should be 200

Scenario: Updating Patch IssueLog Details with ID
When Update Patch IssueLog
Then The IssueLog response status code should be 200

Scenario: Deleting IssueLog Details with ID
When Delete IssueLog with ID
Then The IssueLog response status code should be 204






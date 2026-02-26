@IssueLog

Feature: issueLog API testing

Scenario: Creating New IssueLog
Given I have a IssueLog
When Create New IssueLog
Then The IssueLog response should contain "id"
Then Validate API ES and DB for IssueLogId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,eventdate" excludeFromEs "deleted"
And The IssueLog response status code should be 201

Scenario: Fetching All IssueLogs Details
When Fetch All IssueLog Details
Then The IssueLog response status code should be 200

Scenario: Fetching IssueLogs Details with  ID
When Fetch IssueLog Details with ID
Then The IssueLog response status code should be 200

Scenario: Updating PUT IssueLog Details with ID
When Update IssueLog Details
Then Validate API ES and DB for IssueLogId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,eventdate" excludeFromEs "deleted"
Then The IssueLog response status code should be 200

Scenario: Updating Patch IssueLog Details with ID
When Update Patch IssueLog
Then Validate API ES and DB for IssueLogId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,eventdate" excludeFromEs "deleted"
Then The IssueLog response status code should be 200

Scenario: Deleting IssueLog Details with ID
When Delete IssueLog with ID
Then The IssueLog response status code should be 204






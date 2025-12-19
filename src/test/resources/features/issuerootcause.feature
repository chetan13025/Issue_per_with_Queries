@IssueRootCause

Feature: IssueRootCause API testing

Scenario: Creating New IssueRootCause
Given I have a IssueRootCause
When Create New IssueRootCause
Then The IssueRootCause response should contain "id"
Then Validate API ES and DB for IssueRootCauseId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The IssueRootCause response status code should be 201

Scenario: Fetching All IssueRootCause Details
When Fetch All IssueRootCause Details
Then The IssueRootCause response status code should be 200

Scenario: Fetching IssueRootCause Details with ID
When Fetch IssueRootCause Details with ID
Then The IssueRootCause response status code should be 200

Scenario: Updating PUT IssueRootCause Details with ID
When Update IssueRootCause Details
Then Validate API ES and DB for IssueRootCauseId after PUT skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueRootCause response status code should be 200

Scenario: Updating Patch IssueRootCause Details with ID
When Update Patch IssueRootCause
Then Validate API ES and DB for IssueRootCauseId after PATCH skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueRootCause response status code should be 200

Scenario: Deleting IssueRootCause Details with ID
When Delete IssueRootCause with ID
Then The IssueRootCause response status code should be 204







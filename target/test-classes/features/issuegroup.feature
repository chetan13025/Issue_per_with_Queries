@IssueGroup
Feature: IssueGroup API testing 

Scenario: Creating new IssueGroup
Given I have a IssueGroup
When  Create New IssueGroup
Then The IssueGroup response should contain "id"
Then Validate API ES and DB for IssueGroupId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "id,deleted"
And The IssueGroup response status code should be 201

Scenario: Fetching All IssueGroup
When Fetch All IssueGroups Details
Then The IssueGroup response status code should be 200

Scenario: Fetching IssueGroup Details with ID
When Fetch IssueGroup Details with ID
Then The IssueGroup response status code should be 200

Scenario: Updating PUT IssueGroup Details with ID
When Update IssueGroup Details
Then Validate API ES and DB for IssueGroupId after PUT skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "id,deleted"
Then The IssueGroup response status code should be 200

Scenario: Updating Patch IssueGroup Details with ID
When Update Patch Issuegroup
Then Validate API ES and DB for IssueGroupId after PATCH skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "id,deleted"
Then The IssueGroup response status code should be 200

Scenario: Deleting IssueGroup Details with ID
When Delete IssueGroup with ID
Then The IssueGroup response status code should be 204







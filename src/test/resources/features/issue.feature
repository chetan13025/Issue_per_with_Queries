@Issue

Feature: Issue API testing

Scenario: Creating New Issue
Given I have a Issue
When Create New Issue
Then The Issue response should contain "id"
Then Validate API ES and DB for IssueId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
And The Issue response status code should be 201

Scenario: Fetch All Issues
When Fetch All Issues Details
Then The Issue response status code should be 200

Scenario: Fetching Issue Details with ID
When Fetch Issue Details with ID
Then The Issue response status code should be 200

Scenario: Updating PUT Issue Details with ID
When Update Issue Details
Then Validate API ES and DB for IssueId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The Issue response status code should be 200

Scenario: Updating Patch Issue Details with ID
When Update Patch Issue
Then Validate API ES and DB for IssueId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The Issue response status code should be 200

Scenario: Deleting Issue Details with ID
When Delete Issue with ID
Then The Issue response status code should be 204







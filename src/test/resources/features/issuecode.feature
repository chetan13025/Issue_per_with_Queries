@IssuCode
Feature: IssueCode API testing

Scenario: Creating New IssueCode
Given I have a IssueCode
When Create New IssueCode
Then The IssueCode response should contain "id"
Then Validate API ES and DB for IssueCodeId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The IssueCode response status code should be 201

Scenario: Fetching All Issuecode Details
When Fetch All IssueCode Details
Then The IssueCode response status code should be 200

Scenario: Fetching IssueCode with ID
When Fetch IssueCode Details with ID
Then The IssueCode response status code should be 200

Scenario: Updating PUT IssueCode Details with ID 
When Update IssueCode
Then Validate API ES and DB for IssueCodeId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueCode response status code should be 200

Scenario: Updating Patch IssueCode Details with ID
When Update Patch IssueCode
Then Validate API ES and DB for IssueCodeId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueCode response status code should be 200

Scenario: Deleting IssueCode Details with ID
When Delete IssueCode with ID
Then The IssueCode response status code should be 204








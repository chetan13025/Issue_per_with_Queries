@IssuCode
Feature: IssueCode API testing

Scenario: Creating New IssueCode
Given I have a IssueCode
When Create New IssueCode
Then The IssueCode response should contain "id"
And The IssueCode response status code should be 201

Scenario: Fetching All Issuecode Details
When Fetch All IssueCode Details
Then The IssueCode response status code should be 200

Scenario: Fetching IssueCode with ID
When Fetch IssueCode Details with ID
Then The IssueCode response status code should be 200

Scenario: Updating PUT IssueCode Details with ID 
When Update IssueCode
Then The IssueCode response status code should be 200

Scenario: Updating Patch IssueCode Details with ID
When Update Patch IssueCode
Then The IssueCode response status code should be 200

Scenario: Deleting IssueCode Details with ID
When Delete IssueCode with ID
Then The IssueCode response status code should be 204








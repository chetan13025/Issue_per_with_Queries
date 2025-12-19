@IssueComponent

Feature: IssueComponent API testing

Scenario: To verify that creating new IssueComponent
Given I have a IssueComponent
When Create New IssueComponent
Then The IssueComponent response should contain "id"
Then Validate API ES and DB for IssueComponentId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The IssueComponent response status code should be 201

Scenario: To verify that GET request Fetched all data
When Fetch All IssueComponent Details
Then The IssueComponent response status code should be 200

Scenario: To verify that GET with ID fetched Corect data
When Fetch IssueComponent Details with ID
Then The IssueComponent response status code should be 200

Scenario: To verify that after PUT request data should get update in DataBase
When Update IssueComponent Details
Then Validate API ES and DB for IssueComponentId after PUT skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueComponent response status code should be 200

Scenario: To verify that after Patch request data should get update in DataBase
When Update Patch IssueComponent
Then Validate API ES and DB for IssueComponentId after PATCH skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueComponent response status code should be 200

Scenario: To verify that after Delete request data should get delete from DataBase
When Delete IssueComponent with ID
Then The IssueComponent response status code should be 204







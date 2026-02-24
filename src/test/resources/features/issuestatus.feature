@IssueStatus

Feature: IssueStatus API testing

Scenario: To verify that New IssueStatus is creating
Given I have a IssueStatus
When Create New IssueStatus
Then The IssueStatus response should contain "id"
Then Validate API ES and DB for IssueStatusId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The IssueStatus response status code should be 201


Scenario: To verify that fetching all IssueStatus
When Fetch All IssueStatus Details
Then The IssueStatus response status code should be 200

Scenario: To verify that GET with ID fetched correct Data
When Fetch IssueStatus Details with ID
Then The IssueStatus response status code should be 200

Scenario: To verify that in PUT request data should get update in DataBase
When Update IssueStatus Details
Then Validate API ES and DB for IssueStatusId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueStatus response status code should be 200

Scenario: To verify that after Patch request data should get update in DataBase
When Update Patch IssueStatus
Then Validate API ES and DB for IssueStatusId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"


Scenario: To verify that after delete request data should get removed from DataBase
When Delete IssueStatus with ID
Then The IssueStatus response status code should be 204





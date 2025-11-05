@IssueStatus

Feature: IssueStatus API testing

Scenario: To verify that New IssueStatus is creating
Given I have a IssueStatus
When Create New IssueStatus
Then The IssueStatus response status code should be 201
And The IssueStatus response should contain "id"

Scenario: To verify that fetching all IssueStatus
When Fetch All IssueStatus Details
Then The IssueStatus response status code should be 200

Scenario: To verify that GET with ID fetched correct Data
When Fetch IssueStatus Details with ID
Then The IssueStatus response status code should be 200

Scenario: To verify that in PUT request data should get update in DataBase
When Update IssueStatus Details
Then The IssueStatus response status code should be 200

Scenario: To verify that after delete request data should get removed from DataBase
When Delete IssueStatus with ID
Then The IssueStatus response status code should be 204





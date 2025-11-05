@IssueStep

Feature: IssueStep API testing

Scenario: To verify that New IssueStep is creating
Given I have a IssueStep
When Create New IssueStep
Then The IssueStep response should contain "id"
And The IssueStep response status code should be 201

Scenario: To verify that fetching All IssueSteps
When Fetch All IssueStep Details
Then The IssueStep response status code should be 200

Scenario: To verify that GET with ID fetched correct data
When Fetch IssueStep Details with ID
Then The IssueStep response status code should be 200

Scenario: To verify that in PUT request Data is get update in DataBase
When Update IssueStep Details
Then The IssueStep response status code should be 200

Scenario: To verify that in Patch request data is get update in DataBase
When Update Patch IssueStep
Then The IssueStep response status code should be 200

Scenario: To verify that after delete request data should get delete from DataBase






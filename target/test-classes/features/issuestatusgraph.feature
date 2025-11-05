@IssueStatusGraph
Feature: IssueStatusGraph API testing

Scenario: To verify that New IssueStatusGraph is creating
Given I have a IssueStatusGraph
When Create New IssueStatusGraph
Then The IssueStatusGraph response should contain "id"
And The IssueStatusGraph response status code should be 201

Scenario: To verify that fetching All IssueStatusGraph
When Fetch All IssueStatusGraph Details
Then The IssueStatusGraph response status code should be 200

Scenario: To verify that in PUT data should get Update in DateBase
When Update IssueStatusGraph Details
Then The IssueStatusGraph response status code should be 200

Scenario: To verify that in Patch data should get update in DataBase
When Update Patch IssueStatusGraph
Then The IssueStatusGraph response status code should be 200

Scenario: To verify that after Delete data should get delete from DataBase
When Delete IssueStatusGraph with ID
Then The IssueStatusGraph response status code should be 204





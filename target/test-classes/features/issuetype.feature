@IssueType

Feature: IssueType API testing

Scenario: To verify that new IssureType is creating
Given I have a IssueType
When Create New IssueType
Then The IssueType response should contain "id"
Then Validate API ES and DB for IssueTypeId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The IssueType response status code should be 201

Scenario: To verify that Fetching all IssueType
When Fetch All IssueType Details
Then The IssueType response status code should be 200

Scenario: To verify that GET with ID fetched correct Data
When Fetch IssueType Details with ID
Then The IssueType response status code should be 200

Scenario: To verify that in PUT request data isgetting update in DataBase
When Update IssueType Details
Then Validate API ES and DB for IssueTypeId after PUT skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
When The IssueType response status code should be 200

Scenario: To verify that in Patch request data is getting update in DataBase
When Update Patch IssueType
Then Validate API ES and DB for IssueTypeId after PATCH skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueType response status code should be 200

Scenario: To verify that After Delete request Data is Deleting from DataBase
When Delete IssueType with ID
Then The IssueType response status code should be 204






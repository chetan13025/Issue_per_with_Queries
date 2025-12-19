@IssueVersion

Feature: IssueVersion API testing

Scenario: To Verify that New issueVersion is Creating
Given I have a IssueVersion
When Create New IssueVersion
Then The IssueVersion response should contain "id"
Then Validate API ES and DB for IssueVersionId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The IssueVersion response status code should be 201

Scenario: To Verify that GET request Fetched All data
When Fetch All IssueVersion Details
Then The IssueVersion response status code should be 200

Scenario: To Verify that GET with ID Fetched Correct Data
When Fetch IssueVersion Details with ID
Then The IssueVersion response status code should be 200

Scenario: To Verify that after PUT request Data should get update in DataBase
When Update IssueVersion Details
Then Validate API ES and DB for IssueVersionId after PUT skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueVersion response status code should be 200

Scenario: To Verify that After PATCH request Data should get update in DataBase
When Update Patch IssueVersion
Then Validate API ES and DB for IssueVersionId after PATCH skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueVersion response status code should be 200

Scenario: To Verify that after Delete Data should get Delete from DataBase
When Delete IssueVersion with ID
Then The IssueVersion response status code should be 204






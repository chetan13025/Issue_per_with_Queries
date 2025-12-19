@IssueTestCase

Feature: IssueTestCase API testing

Scenario: To verify that new IssueTestCase creating
Given I have a IssueTestCase
When Create New IssueTestCase
Then The IssueTestCase response should contain "id"
Then Validate API ES and DB for IssueTestCaseId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The IssueTestCase response status code should be 201

Scenario: To verify that Fetching All IssueTestCase
When Fetch All IssueTestCase Details
Then The IssueTestCase response status code should be 200

Scenario: To verify that GET with ID fetched correct data
When Fetch IssueTestCase Details with ID
Then The IssueTestCase response status code should be 200

Scenario: To verify that in PUT request Data is geting update in DataBase
When Update IssueTestCase Details
Then Validate API ES and DB for IssueTestCaseId after PUT skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueTestCase response status code should be 200

Scenario: To verify that in Patch request data is geting update in DataBase
When Update Patch IssueTestCase
Then Validate API ES and DB for IssueTestCaseId after PATCH skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueTestCase response status code should be 200

Scenario: To verify that After Delete request data is delete from DataBase
When Delete IssueTestCase with ID
Then The IssueTestCase response status code should be 204










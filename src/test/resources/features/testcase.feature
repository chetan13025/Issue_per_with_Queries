@TestCase

Feature: TestCase API testing

Scenario: To verify that creating New TestCase
Given I have a TestCase
When Create New TestCase
Then The TestCase response should contain "id"
Then Validate API ES and DB for TestCaseId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The TestCase response status code should be 201

Scenario: To verify that GET request fetched all data
When Fetch All TestCase Details
Then The TestCase response status code should be 200

Scenario: To verify GET with ID fetched Correct Data
When Fetch TestCase Details with ID
Then The TestCase response status code should be 200

Scenario: To verify that after PUT request data should get update in Database
When Update TestCase Details
Then Validate API ES and DB for TestCaseId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The TestCase response status code should be 200

Scenario: To verify that after Patch request data should get update in DataBase
When Update Patch TestCase
Then Validate API ES and DB for TestCaseId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The TestCase response status code should be 200

Scenario: To verify that after delete request data should get removed form DataBase
When Delete TestCase with ID
Then The TestCase response status code should be 204








@UnitTestResult

Feature: UnitTestResult API testing

Scenario: To verify that creating new UnitTestResult
Given I have a UnitTestResult
When Create New UnitTestResult
Then The UnitTestResult response should contain "id"
Then Validate API ES and DB for UnitTestResultId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The UnitTestResult response status code should be 201

Scenario: To verify that GET request fetched all data 
When Fetch All UnitTestResult Details
Then The UnitTestResult response status code should be 200

Scenario: To verify that GET with ID fetch Correct data
When Fetch UnitTestResult Details with ID
Then The UnitTestResult response status code should be 200

Scenario: To verify that after PUT request data should get update in DataBase
When Update UnitTestResult Details
Then Validate API ES and DB for UnitTestResultId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The UnitTestResult response status code should be 200

Scenario: To verify that after Patch request data should get update in DataBase
When Update Patch UnitTestResult
Then Validate API ES and DB for UnitTestResultId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The UnitTestResult response status code should be 200


Scenario: To verify that after Delete request data should get removed from DataBase
When Delete UnitTestResult with ID
Then The UnitTestResult response status code should be 204








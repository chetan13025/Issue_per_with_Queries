@RootCause

Feature: RootCause API testing

Scenario: To verify that Creating new RootCause
Given I have a RootCause
When Create New RootCause
Then The RootCause response should contain "id"
Then Validate API ES and DB for RootCauseId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The RootCause response status code should be 201


Scenario: To verify that GET request fetched all data 
When Fetch All RootCause Details
Then The RootCause response status code should be 200

Scenario: To verify that GET with ID Fetched Correct data
When Fetch RootCause Details with ID
Then The RootCause response status code should be 200

Scenario: To verify that after PUT request data should get update in DataBase
When Update RootCause Details
Then Validate API ES and DB for RootCauseId after PUT skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The RootCause response status code should be 200

Scenario: To verify that after Patch request data should get update in DataBase
When Update Patch RootCause
Then Validate API ES and DB for RootCauseId after PATCH skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The RootCause response status code should be 200

Scenario: To verify that after Delete request data should get removed from DataBase
When Delete RootCause with ID
Then The RootCause response status code should be 204







@Nature

Feature: Nature API testing

Scenario: To verify that creating New Nature
Given I have a Nature
When Create New Nature
Then The Nature response should contain "id"
Then Validate API ES and DB for NatureId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The Nature response status code should be 201

Scenario: To verify that  GET request fetched all data
When Fetch All Nature Details
Then The Nature response status code should be 200

Scenario: To verify that GET with ID Fetched Correct data
When Fetch Nature Details with ID
Then The Nature response status code should be 200

Scenario: To verify that after PUT request data should get update in DataBase
When Update Nature Details
Then Validate API ES and DB for NatureId after PUT skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The Nature response status code should be 200

Scenario: To verify that after Patch request data should get update in DataBase
When Update Patch Nature
Then Validate API ES and DB for NatureId after PATCH skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The Nature response status code should be 200

Scenario: To verify that after Delete request data should get removed form DataBase
When Delete Nature with ID
Then The Nature response status code should be 204








 

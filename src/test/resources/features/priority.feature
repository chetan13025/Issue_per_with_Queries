@Priority

Feature: Priority API testing

Scenario: To verify that creating new Priority
Given I have a Priority
When Create New Priority
Then The Priority response should contain "id"
Then Validate API ES and DB for PriorityId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The Priority response status code should be 201

Scenario: To verify that GET request fetched all data
When Fetch All Priority Details
Then The Priority response status code should be 200

Scenario: To verify that GET with ID fetched Correct data
When Fetch Priority Details with ID
Then The Priority response status code should be 200

Scenario: To verify that after PUT data should get update in DataBase
When Update Priority Details
Then Validate API ES and DB for PriorityId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The Priority response status code should be 200

Scenario: To verify that after Patch data should get update in DataBase
When Update Patch Priority
Then Validate API ES and DB for PriorityId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The Priority response status code should be 200

Scenario: To verify that after Delete request data should get removed from DataBase
When Delete Priority with ID
Then The Priority response status code should be 204






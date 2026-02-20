@Behavior

Feature: Behavior API testing

Scenario: To verify that Creating new Behavior
Given I have a Behavior
When Create New Behavior
Then The Behavior response should contain "id"
Then Validate API ES and DB for BehaviorId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The Behavior response status code should be 201

Scenario: To verify that GET request Fetched All data
When Fetch All Behavior Details
Then The Behavior response status code should be 200

Scenario: To verify that GET with ID Fetched Correct Data
When Fetch Behavior Details with ID
Then The Behavior response status code should be 200

Scenario: To verify that after PUT request data should get update in Database
When Update Behavior Details
Then Validate API ES and DB for BehaviorId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The Behavior response status code should be 200

Scenario: To verify that after Patch request data shouldget update in DataBase
When Update Patch Behavior
Then Validate API ES and DB for BehaviorId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The Behavior response status code should be 200

Scenario: To verify that after Delete request data should get removed from Datebase
When Delete Behavior with ID
Then The Behavior response status code should be 204











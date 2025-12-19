@Occurrence

Feature: Occurrence API testing

Scenario: To verify that creating new Occurrence
Given I have a Occurrence
When Create New Occurrence
Then The Occurrence response should contain "id"
Then Validate API ES and DB for OccurrenceId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The Occurrence response status code should be 201

Scenario: T verify that GET request fetched all data
When Fetch All Occurrence Details
Then The Occurrence response status code should be 200

Scenario: To verify that GET with ID Fetched Correct data
When Fetch Occurrence Details with ID
Then The Occurrence response status code should be 200

Scenario: To verify that after PUT request data should get update in DataBase
When Update Occurrence Details
Then Validate API ES and DB for OccurrenceId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The Occurrence response status code should be 200

Scenario: To verify that after Patch request data should get update in DataBase
When Update Patch Occurrence
Then Validate API ES and DB for OccurrenceId after PATCH skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The Occurrence response status code should be 200

Scenario: To verify that after Delete request data should get removed from DataBase
When Delete Occurrence with ID
Then The Occurrence response status code should be 204 







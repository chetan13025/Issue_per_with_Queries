@Resolution

Feature: Resolution API testing

Scenario: To verify that creating new Resolution
Given I have a Resolution
When Create New Resolution
Then The Resolution response should contain "id"
Then Validate API ES and DB for ResolutionId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The Resolution response status code should be 201

Scenario: To verify that GET request fetched all data
When Fetch All Resolution Details
Then The Resolution response status code should be 200

Scenario: To verify that GET with ID fetched Correct data
When Fetch Resolution Details with ID
Then The Resolution response status code should be 200

Scenario: To verify that after PUT request data should get update in DataBase
When Update Resolution Details
Then Validate API ES and DB for ResolutionId after PUT skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The Resolution response status code should be 200

Scenario: To verify that after Patch request data should get update in DataBase
When Update Patch Resolution
Then Validate API ES and DB for ResolutionId after PATCH skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The Resolution response status code should be 200

Scenario: To verify that after Delete request data should get removed from DataBase
When Delete Resolution with ID
Then The Resolution response status code should be 204



 


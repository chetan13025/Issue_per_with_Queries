@IssueSprint

Feature: IssueSprint API testing

Scenario: To verify that  New IssueSprint is Creating
Given I have a IssueSprint
When Create New IssueSprint
Then The IssueSprint response should contain "id"
Then Validate API ES and DB for IssueSprintId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The IssueSprint response status code should be 201

Scenario: To verify that Fetching All IssueSprints
When Fetch All IssueSprint Details
Then The IssueSprint response status code should be 200

Scenario: To verify that GET with ID Fetched Correct Data
 When Fetch IssueSprint Details with ID
 Then The IssueSprint response status code should be 200
 
 Scenario: To verify that in PUT data is updating in DataBase
When Update IssueSprint Details
Then Validate API ES and DB for IssueSprintId after PUT skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueSprint response status code should be 200

Scenario: To verify that in Patch data is updating in DataBase
When Update Patch IssueSprint
Then Validate API ES and DB for IssueSprintId after PATCH skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueSprint response status code should be 200

Scenario: To verify that After Delete Data should delete from DataBase
When Delete IssueSprint with ID
Then The IssueSprint response status code should be 204


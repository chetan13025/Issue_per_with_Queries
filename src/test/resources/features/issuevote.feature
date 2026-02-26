@IssueVote

Feature: IssueVote API testing

Scenario: To Verify that New IssueVote is creating
Given I have a IssueVote
When Create New IssueVote
Then The IssueVote response should contain "id"
Then Validate API ES and DB for IssueVoteId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
And The IssueVote response status code should be 201

Scenario: To verify that GET request Fetched all data
When Fetch All IssueVote Details
Then The IssueVote response status code should be 200

Scenario: To verify that GET with ID Fetched Correct Data
When Fetch IssueVote Details with ID
Then The IssueVote response status code should be 200

Scenario: To verify that after PUT request Data should get update in Data Base
When Update IssueVote Details
Then Validate API ES and DB for IssueVoteId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
Then The IssueVote response status code should be 200

Scenario: To verify that after PATCH request Data should get udpate in DataBase
When Update Patch IssueVote
Then Validate API ES and DB for IssueVoteId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
Then The IssueVote response status code should be 200

Scenario: TO verify that After Delete request Data should get delete in DataBAse
When Delete IssueVote with ID
Then The IssueVote response status code should be 204










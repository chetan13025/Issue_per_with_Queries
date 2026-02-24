@IssueComment

Feature: IssueComments API testing 
Scenario: Creating New IssueComment
Given I have a IssueComment
When Create New IssueComment
Then The IssueComment response should contain "id"
Then Validate API ES and DB for IssueCommentsId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate" excludeFromEs "deleted"
And The IssueComment response status code should be 201

Scenario: Fetching All IssueComments Details
When Fetch All IssueComments Details
Then The IssueComment response status code should be 200

Scenario: Fetching IssueComments Details with ID
When Fetch IssueComment Details with ID
Then The IssueComment response status code should be 200

Scenario: Updating PUT IssueComments with ID
When Update IssueComment Details
Then Validate API ES and DB for IssueCommentsId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate" excludeFromEs "deleted"
Then The IssueComment response status code should be 200

Scenario: Updating Patch IssueComments with ID
When Update Patch IssueComment
Then Validate API ES and DB for IssueCommentsId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate" excludeFromEs "deleted"
Then The IssueComment response status code should be 200

Scenario: Deleting IssueComments Details with ID
When Delete IssueComment with ID
Then The IssueComment response status code should be 204









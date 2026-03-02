@IssueCollaborator

Feature: IssueCollaborator API testing 

Scenario: Creating New IssueCollaborator
Given I have a IssueCollaborator
When Create New IssueCollaborator
Then The IssueCollaborator response should contain "id"
Then Validate API ES and DB for IssueCollaboratorId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
And The IssueCollaborator response status code should be 201

Scenario: Fetching All IssueCollaborator
When Fetch All IssueCollaborator Details
Then The IssueCollaborator response status code should be 200

Scenario: Fetching IssueCollaborator Details with ID
When Fetch IssueCollaborator Details with ID
Then The IssueCollaborator response status code should be 200

Scenario: Updating PUT IssueCollaborator Details with ID
When Update IssueCollaborator Details
Then Validate API ES and DB for IssueCollaboratorId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted",createdate,updatedate excludeFromEs "deleted"
Then The IssueCollaborator response status code should be 200

Scenario: Updating Patch IssueCollaborator Details with ID
When Update Patch IssueCollaborator
Then Validate API ES and DB for IssueCollaboratorId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
Then The IssueCollaborator response status code should be 200

Scenario: Deleting IssueCollaborator Details with ID
When Delete IssueCollaborator with ID
Then The IssueCollaborator response status code should be 204





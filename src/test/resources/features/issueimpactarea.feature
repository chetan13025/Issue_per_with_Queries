@IssueImpactArea

Feature: IssueImpactArea API testing

Scenario: Creating New IssueImpactArea
Given I have a IssueImapactArea
When Create New IssueImpactArea
Then The IssueImpactArea response should contain "id"
And The IssueImpactArea response status code should be 201

Scenario: Fetching All IssueImpactArea Details 
When Fetch All IssueImpactArea Details
Then The IssueImpactArea response status code should be 200

Scenario: Fetching IssueImpactArea Details with ID
When Fetch IssueImpactArea Details with ID
Then The IssueImpactArea response status code should be 200

Scenario: Updating PUT IssueImpactArea Details with ID
When Update IssueImpactArea Details
Then The IssueImpactArea response status code should be 200

Scenario: Updating Patch IssueImpactArea
When Update Patch IssueImpactArea
Then The IssueImpactArea response status code should be 200

Scenario: Deleting IssueImpactArea Details with ID
When Delete IssueImpactArea with ID
Then The IssueImpactArea response status code should be 204










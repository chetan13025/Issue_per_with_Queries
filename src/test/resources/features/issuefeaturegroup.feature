@IssueFeatureGroup

Feature: IssueFeatureGroup API testing

Scenario: Creating New IssueFeatureGroup
Given I have a IssueFeatureGroup
When Create New IssueFeatureGroup
Then The IssueFeatureGroup response should contain "id"
Then Validate API ES and DB for IsueFeatureGroupId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The IssueFeatureGroup response status code should be 201

Scenario: Fetching All IssueFeatureGroup Details
When Fetch All IssueFeatureGroup Details
Then The IssueFeatureGroup response status code should be 200

Scenario: Fetching IssueFeatureGroup Details with ID
When Fetch IssueFeatureGroup Details with ID
Then The IssueFeatureGroup response status code should be 200

Scenario: Updating PUT IssueFeatureGroup Details with ID
When Update IssueFeatureGroup Details
Then Validate API ES and DB for IsueFeatureGroupId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueFeatureGroup response status code should be 200

Scenario: Updating PATCH IssuefeatureGroup Details with ID
When Update Patch IssueFeatureGroup
Then Validate API ES and DB for IsueFeatureGroupId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueFeatureGroup response status code should be 200

Scenario: Deleting IssueFeatureGroup Details with ID
When Delete IssueFeatureGroup with ID
Then The IssueFeatureGroup response status code should be 204





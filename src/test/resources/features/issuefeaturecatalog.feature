@IssueFeatureCatalog

Feature: IssueFeatureCatalog API testing

Scenario: Creating New IssueFeatureCatalog
Given I have a IssueFeatureCatalog
When Create New IssueFeatureCatalog
Then The IssueFeatureCatalog response should contain "id"
Then Validate API ES and DB for IssueFeatureCatalogId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The IssueFeatureCatalog response status code should be 201

Scenario: Fetching All IssueFeatureCatalog
When Fetch All IssueFeatureCatalog Details
Then The IssueFeatureCatalog response status code should be 200

Scenario: Fetching IssueFeatureCatalog Details with ID
When Fetch IssueFeatureCatalog Details with ID
Then The IssueFeatureCatalog response status code should be 200

Scenario: Updating PUT IssueFeatureCatalog Details with ID
When Update IssueFeatureCatalog Details
  Then Validate API ES and DB for IssueFeatureCatalogId after PUT skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueFeatureCatalog response status code should be 200

Scenario: Updating PATCH IssueFeatureCatalog Details with ID
When Update Patch IssueFeatureCatalog
  Then Validate API ES and DB for IssueFeatureCatalogId after PATCH skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueFeatureCatalog response status code should be 200

Scenario: Deleing IssueFeatureCatalog Details with ID
When Delete IssueFeatureCatalog with ID
Then The IssueFeatureCatalog response status code should be 204








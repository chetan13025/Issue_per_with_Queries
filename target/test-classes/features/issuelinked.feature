@IssueLinked

Feature: IssueLinked API testing

Scenario: Creating New IssueLinked
Given I have a IssueLinked
When Create New IssueLinked
Then The IssueLinked response should contain "id"
Then Validate API ES and DB for IssueLinkedId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted,linkeddate" excludeFromEs "deleted"
And The IssueLinked response status code should be 201


Scenario: Fetching All IssueLinked  Details
When Fetch All IssueLinked Details
Then The IssueLinked response status code should be 200

Scenario: Fetching IssueLinked Details with ID
When Fetch IssueLinked Details with ID
Then The IssueLinked response status code should be 200

Scenario: Updating PUT IssueLinked Details with ID
When Update IssueLinked Details
Then Validate API ES and DB for IssueLinkedId after PUT skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueLinked response status code should be 200

Scenario: Updating Patch IssueLinked Details with ID
When Update Patch IssueLinked
Then Validate API ES and DB for IssueLinkedId after PATCH skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The IssueLinked response status code should be 200

Scenario: Deleting IssueLinked Details
When Delete IssueLinked with ID
Then The IssueLinked response status code should be 204






@UserProject

Feature: UserProject API testing

Scenario: Creating New User Project
Given I have a UserProject
When Create New UserProject
Then The UserProject response should contain "id"
Then Validate API ES and DB for UserProjectId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The UserProject response status code should be 201

Scenario: Fetching All UserProject 
When Fetch All UserProject Details
Then The UserProject response status code should be 200

Scenario: Fetching UserProject Details with ID
When Fetch UserProject Details with ID
Then The UserProject response status code should be 200

Scenario: Updating PUT UserProject Details with ID
When Update UserProject Details
Then Validate API ES and DB for UserProjectId after PUT skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The UserProject response status code should be 200

Scenario: Updating Patch UserProject Details with ID
When Update Patch UserProject
Then Validate API ES and DB for UserProjectId after PATCH skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The UserProject response status code should be 200

Scenario: Deleting UserProject Details with ID
When Delete UserProject with ID
Then The UserProject response status code should be 204






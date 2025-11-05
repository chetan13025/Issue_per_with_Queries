@UserProject

Feature: UserProject API testing

Scenario: Creating New User Project
Given I have a UserProject
When Create New UserProject
Then The UserProject response should contain "id"
And The UserProject response status code should be 201

Scenario: Fetching All UserProject 
When Fetch All UserProject Details
Then The UserProject response status code should be 200

Scenario: Fetching UserProject Details with ID
When Fetch UserProject Details with ID
Then The UserProject response status code should be 200

Scenario: Updating PUT UserProject Details with ID
When Update UserProject Details
Then The UserProject response status code should be 200

Scenario: Updating Patch UserProject Details with ID
When Update Patch UserProject
Then The UserProject response status code should be 200

Scenario: Deleting UserProject Details with ID
When Delete UserProject with ID
Then The UserProject response status code should be 204






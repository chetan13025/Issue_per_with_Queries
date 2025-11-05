@UserState

Feature: UserState API testing 

Scenario: Creating New UserState
Given I have a UserState
When Create New UserState
Then The UserState response should contain "id"
And The UserState response status code should be 201

Scenario: Fetching ALL UserState Details 
When Fetch All UserState Details
Then The UserState response status code should be 200

Scenario: Fetching UserState Details with ID
When Fetch UserState Details with ID
Then The UserState response status code should be 200

Scenario: Updating PUT UserState Details with ID
When Update UserState Details
Then The UserState response status code should be 200

Scenario: Updating Patch UserState Details with ID
When Update Patch UserState
Then The UserState response status code should be 200

Scenario: Deleting UserState Details with ID
When Delete UserState with ID
Then The UserState response status code should be 204






 
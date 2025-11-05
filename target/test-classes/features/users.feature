@User

Feature: User API testing 

Scenario: Creating New User
Given I have a User
When Create New User
Then The User response should contain "id"
And The User response status code should be 201

Scenario: Fetching All Users
When Fetch All Users Details
Then The User response status code should be 200

Scenario: Fetching Users Details with ID
When Fetch User Details with ID
Then The User response status code should be 200

Scenario: Updating PUT User Details with ID
When Update User Details
Then The User response status code should be 200

Scenario: Updating Patch User Details with ID
When Update Patch User
Then The User response status code should be 200

Scenario: Deleting User Details with ID
When Delete User with ID
Then The User response status code should be 204







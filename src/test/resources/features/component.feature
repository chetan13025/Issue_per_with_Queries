@Component

Feature: Component API testing

Scenario: To verify that creating new Component
Given I have a Component 
When Create New Component
Then The Component response should contain "id"
Then Validate API ES and DB for ComponentId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The Component response status code should be 201

Scenario: To verify that GET request  fetched all data
When Fetch All Component Details
Then The Component response status code should be 200

Scenario: To verify that GET with ID fetched Correct data
When Fetch Component Details with ID
Then The Component response status code should be 200


Scenario: To verify that after PUT request  data should get update in DataBase
When Update Component Details
Then Validate API ES and DB for ComponentId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The Component response status code should be 200

Scenario: To verify that after Patch request data should get update in DataBase
When Update Patch Component
Then Validate API ES and DB for ComponentId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The Component response status code should be 200

Scenario: To verify that after Delete request data should get removed from DataBase
When Delete Component with ID
Then The Component response status code should be 204




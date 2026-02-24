@DependencyType

Feature: DependencyType API testing

Scenario:  To verify that creating new DependencyType
Given I have a DependencyType
When Create New DependencyType
Then The DependencyType response should contain "id"
Then Validate API ES and DB for DependencyTypeId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The DependencyType response status code should be 201

Scenario: To verify that GET request fetched all data
When Fetch All DependencyType Details
Then The DependencyType response status code should be 200

Scenario: To verify that GET with ID request fetched Correct data
When Fetch DependencyType Details with ID
Then The DependencyType response status code should be 200

Scenario: To verify that after PUT request data should get update in DataBase
When Update DependencyType Details
Then Validate API ES and DB for DependencyTypeId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The DependencyType response status code should be 200

Scenario: To verify that after patch request data should get update in DataBase
When Update Patch DependencyType
Then Validate API ES and DB for DependencyTypeId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The DependencyType response status code should be 200

Scenario: To verify that after Delete request data should get removed from DataBase
When Delete DependencyType with ID
Then The DependencyType response status code should be 204 






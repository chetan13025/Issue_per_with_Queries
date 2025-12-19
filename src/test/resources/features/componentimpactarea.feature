@ComponentImpactArea
Feature: ComponentimpactArea API testing

  Scenario: To verify that Creating new ComponentImpactArea
    Given I have a ComponentImpactArea
    When Create New ComponentImpactArea
    Then The ComponentImpactArea response should contain "id"
    Then Validate API ES and DB for ComponentImpactAreaId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The ComponentImpactArea response status code should be 201

  Scenario: To verify that GET request Fetched all data
    When Fetch All ComponentImpactArea Details
    Then The ComponentImpactArea response status code should be 200

  Scenario: To verify that Get with ID Fetched Correct data
    When Fetch ComponentImpactArea Details with ID
    Then The ComponentImpactArea response status code should be 200

  Scenario: To verify that after PUT request data should get update in DataBase
    When Update ComponentImpactArea Details
    Then Validate API ES and DB for ComponentImpactAreaId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The ComponentImpactArea response status code should be 200

  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch ComponentImpactArea
    Then Validate API ES and DB for ComponentImpactAreaId after POST skipping ES false excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The ComponentImpactArea response status code should be 200

  Scenario: To verify that after delete request data should get removed from DataBase
    When Delete ComponentImpactArea with ID
    Then The ComponentImpactArea response status code should be 204

Feature: Module API testing

  @Module
  Scenario: To verify that after POST New Module is creating
    Given I have a Module
    When Create New Module
    Then The Module response should contain "id"
    Then Validate API ES and DB for ModuleId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "null"
    And The Module response status code should be 201

  @Module
  Scenario: To verify that GET request Fetched all data
    When Fetch All Module Details
    Then The Module response status code should be 200

  @Module
  Scenario: To verify that GET with ID Fetched Correct data
    When Fetch Module Details with ID
    Then The Module response status code should be 200

  @Module
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update Module Details
    Then Validate API ES and DB for ModuleId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "null"
    Then The Module response status code should be 200

  @Module
  Scenario: To verify that after PATCH request data should get update in DataBase
    When Update Patch Module
    Then Validate API ES and DB for ModuleId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "null"
    Then The Module response status code should be 200

  @Module
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Module with ID
    Then The Module response status code should be 204

  @Negative_Scenarioes_for_Module_entity
  Scenario: To verify that GET with ID Fetched Correct data (Which is already deleted)
    When Fetch Module Details with ID
    Then The Module response status code should be 200

  @Negative_Scenarioes_for_Module_entity
  Scenario: To verify that after PUT request data should get update in DataBase (Which is already deleted)
    When Update Module Details
    Then The Module response status code should be 200

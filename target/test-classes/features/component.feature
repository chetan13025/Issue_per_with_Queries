Feature: Component API testing

  @Component
  Scenario: To verify that after POST new Component should create
    Given I have a Component
    When Create New Component
    Then The Component response should contain "id"
    Then Validate API ES and DB for ComponentId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The Component response status code should be 201

  @Component
  Scenario: To verify that GET request  fetched all data
    When Fetch All Component Details
    Then The Component response status code should be 200

  @Component
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch Component Details with ID
    Then The Component response status code should be 200

  @Component
  Scenario: To verify that after PUT request  data should get update in DataBase
    When Update Component Details
    Then Validate API ES and DB for ComponentId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The Component response status code should be 200

  @Component
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch Component
    Then Validate API ES and DB for ComponentId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The Component response status code should be 200

  @Component
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Component with ID
    Then The Component response status code should be 204

  @Negative_scenarioes_for_Component_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch Component Details with ID
    Then The Component response status code should be 200

  @Negative_scenarioes_for_Component_entity
  Scenario: To verify that after PUT request  data should get update in DataBase (Which is  already deleted)
    When Update Component Details
    Then The Component response status code should be 200

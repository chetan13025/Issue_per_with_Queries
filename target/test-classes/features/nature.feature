Feature: Nature API testing

  @Nature
  Scenario: To verify that after POST creating New Nature
    Given I have a Nature
    When Create New Nature
    Then The Nature response should contain "id"
    Then Validate API ES and DB for NatureId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The Nature response status code should be 201

  @Nature
  Scenario: To verify that  GET request fetched all data
    When Fetch All Nature Details
    Then The Nature response status code should be 200

  @Nature
  Scenario: To verify that GET with ID Fetched Correct data
    When Fetch Nature Details with ID
    Then The Nature response status code should be 200

  @Nature
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update Nature Details
    Then Validate API ES and DB for NatureId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The Nature response status code should be 200

  @Nature
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch Nature
    Then Validate API ES and DB for NatureId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The Nature response status code should be 200

  @Custom_Queries_For_Nature
  Scenario: To verify that after GET request FindByProjectId
    When FindByProjectId For Nature
    Then The Nature response status code should be 200

  @Custom_Queries_For_Nature
  Scenario: TO verify that after GET request FindByProjectIdandIsDefault
    When FindByProjectIdAndIsDefault For Nature
    Then The Nature response status code should be 200

  @Nature
  Scenario: To verify that after Delete request data should get removed form DataBase
    When Delete Nature with ID
    Then The Nature response status code should be 204

  @Negative_Scenarioes_for_Nature_entity
  Scenario: To verify that GET with ID Fetched Correct data (Which is already deleted)
    When Fetch Nature Details with ID
    Then The Nature response status code should be 200

  @Negative_Scenarioes_for_Nature_entity
  Scenario: To verify that after PUT request data should get update in DataBase (Which is already deleted)
    When Update Nature Details
    Then The Nature response status code should be 200

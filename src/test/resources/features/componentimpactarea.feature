Feature: ComponentimpactArea API testing

  @ComponentImpactArea
  Scenario: To verify that after POST new ComponentImpactAreashould create
    Given I have a ComponentImpactArea
    When Create New ComponentImpactArea
    Then The ComponentImpactArea response should contain "id"
    Then Validate API ES and DB for ComponentImpactAreaId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The ComponentImpactArea response status code should be 201

  @ComponentImpactArea
  Scenario: To verify that GET request Fetched all data
    When Fetch All ComponentImpactArea Details
    Then The ComponentImpactArea response status code should be 200

  @ComponentImpactArea
  Scenario: To verify that GET with ID Fetched Correct data
    When Fetch ComponentImpactArea Details with ID
    Then The ComponentImpactArea response status code should be 200

  @ComponentImpactArea
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update ComponentImpactArea Details
    Then Validate API ES and DB for ComponentImpactAreaId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The ComponentImpactArea response status code should be 200

  @ComponentImpactArea
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch ComponentImpactArea
    Then Validate API ES and DB for ComponentImpactAreaId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The ComponentImpactArea response status code should be 200

  @Custom_Queries_for_ComponentImpactArea_entity
  Scenario: To verify that after GET request findByImpactAreaId
    When FindByImpactAreaId
    Then The ComponentImpactArea response status code should be 200

  @Custom_Queries_for_ComponentImpactArea_entity
  Scenario: To verify that after GET request findByComponentIdIn
    When FindByComponentIdIn
    Then The ComponentImpactArea response status code should be 200

  @ComponentImpactArea
  Scenario: To verify that after delete request data should get removed from DataBase
    When Delete ComponentImpactArea with ID
    Then The ComponentImpactArea response status code should be 204

  @Negative_Scenarios_for_ComponentImpactArea_entity
  Scenario: To verify that Get with ID Fetched Correct data (Which is  already deleted)
    When Fetch ComponentImpactArea Details with ID
    Then The ComponentImpactArea response status code should be 200

  @Negative_Scenarios_for_ComponentImpactArea_entity
  Scenario: To verify that after PUT request data should get update in DataBase (Which is  already deleted)
    When Update ComponentImpactArea Details
    Then The ComponentImpactArea response status code should be 200

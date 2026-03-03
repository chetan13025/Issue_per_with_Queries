Feature: ImpactArea API testing

  @ImpactArea
  Scenario: To verify that after POST new ImpactArea should create
    Given I have a ImpactArea
    When Create New ImpactArea
    Then The ImpactArea response should contain "id"
    Then Validate API ES and DB for ImpactAreaId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The ImpactArea response status code should be 201

  @ImpactArea
  Scenario: To verify GET request that Fetched all data
    When Fetch ImpactArea Details with ID
    Then The ImpactArea response status code should be 200

  @ImpactArea
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch ImpactArea Details with ID
    Then The ImpactArea response status code should be 200

  @ImpactArea
  Scenario: To verify that after PUT request data should get update in Database
    When Update ImpactArea Details
    Then Validate API ES and DB for ImpactAreaId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The ImpactArea response status code should be 200

  @ImpactArea
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch ImpactArea
    Then Validate API ES and DB for ImpactAreaId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The ImpactArea response status code should be 200

  @ImpactArea
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete ImpactArea with ID
    Then The ImpactArea response status code should be 204

  @Negative_scenarioes_for_ImpactArea_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch ImpactArea Details with ID
    Then The ImpactArea response status code should be 200

  @Negative_scenarioes_for_ImpactArea_entity
  Scenario: To verify that after PUT request data should get update in Database (Which is  already deleted)
    When Update ImpactArea Details
    Then The ImpactArea response status code should be 200

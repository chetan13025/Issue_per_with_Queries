Feature: Resolution API testing

  @Resolution
  Scenario: To verify that after POST creating new Resolution
    Given I have a Resolution
    When Create New Resolution
    Then The Resolution response should contain "id"
    Then Validate API ES and DB for ResolutionId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The Resolution response status code should be 201

  @Resolution
  Scenario: To verify that GET request fetched all data
    When Fetch All Resolution Details
    Then The Resolution response status code should be 200

  @Resolution
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch Resolution Details with ID
    Then The Resolution response status code should be 200

  @Resolution
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update Resolution Details
    Then Validate API ES and DB for ResolutionId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The Resolution response status code should be 200

  @Resolution
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch Resolution
    Then Validate API ES and DB for ResolutionId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The Resolution response status code should be 200

  @Custom_Queries_For_Resolution
  Scenario: To verify that after GET request FindByProjectId For Resolution
    When FindByProjectId For Resolution
    Then The Resolution response status code should be 200

  @Resolution
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Resolution with ID
    Then The Resolution response status code should be 204

  @Negative_Scenarioes_for_Resolution_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is already deleted)
    When Fetch Resolution Details with ID
    Then The Resolution response status code should be 200

  @Negative_Scenarioes_for_Resolution_entity
  Scenario: To verify that after PUT request data should get update in DataBase (Which is already deleted)
    When Update Resolution Details
    Then Validate API ES and DB for ResolutionId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The Resolution response status code should be 200

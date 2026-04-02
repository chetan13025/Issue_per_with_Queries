Feature: BugType API testing

  @BugType
  Scenario: To verify that after POST new BugType should create
    Given I have a BugType
    When Create New BugType
    Then The BugType response should contain "id"
    Then Validate API ES and DB for BugTypeId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The BugType response status code should be 201

  @BugType
  Scenario: To verify that GET request fetched All data
    When Fetch All BugType Details
    Then The BugType response status code should be 200

  @BugType
  Scenario: To verify that GET with ID fetched correct data
    When Fetch BugType Details with ID
    Then The BugType response status code should be 200

  @BugType
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update BugType Details
    Then Validate API ES and DB for BugTypeId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The BugType response status code should be 200

  @BugType
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch BugType
    Then Validate API ES and DB for BugTypeId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The BugType response status code should be 200

  @Custom_Queries_for_BugType_entity
  Scenario: To verify that after GET request FindByProjectID
    When FindByProjectId
    Then The BugType response status code should be 200

  @Custom_Queries_for_BugType_entity
  Scenario: To verify that GET request FindByProjetcIdandisDefault
    When FindByProjectIdAndIsDefault
    Then The BugType response status code should be 200

  @BugType
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete BugType with ID
    Then The BugType response status code should be 204

  @Negative_Scenarioes_for_BugType_entity
  Scenario: To verify that GET with ID fetched correct data (Which is already deleted)
    When Fetch BugType Details with ID
    Then The BugType response status code should be 200

  @Negative_Scenarioes_for_BugType_entity
  Scenario: To verify that after PUT request data should get update in DataBase (Which is already deleted)
    When Update BugType Details
    Then The BugType response status code should be 200

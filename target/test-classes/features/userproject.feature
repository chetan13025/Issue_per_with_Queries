Feature: UserProject API testing

  @UserProject
  Scenario: To verify that after POST new UserProject should create
    Given I have a UserProject
    When Create New UserProject
    Then The UserProject response should contain "id"
    Then Validate API ES and DB for UserProjectId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The UserProject response status code should be 201

  @UserProject
  Scenario: To verify that GET request fetched All data
    When Fetch All UserProject Details
    Then The UserProject response status code should be 200

  @UserProject
  Scenario: To verify that GET with ID fetched correct data
    When Fetch UserProject Details with ID
    Then The UserProject response status code should be 200

  @UserProject
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update UserProject Details
    Then Validate API ES and DB for UserProjectId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The UserProject response status code should be 200

  @UserProject
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch UserProject
    Then Validate API ES and DB for UserProjectId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The UserProject response status code should be 200

  @Custom_Queries_For_UserProject
  Scenario: To verify that after GET request FinfByuserId For UserProject
    When FindByUserId For UserProject
    Then The UserProject response status code should be 200

  @Custom_Queries_For_UserProject
  Scenario: To verify that after GET request FindByUserName For UserProject
    When FindByUserName For UserProject
    Then The UserProject response status code should be 200

  @UserProject
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete UserProject with ID
    Then The UserProject response status code should be 204

  @Negative_Scenarioes_for_UserProject_entity
  Scenario: To verify that GET with ID fetched correct data (Which is already deleted)
    When Fetch UserProject Details with ID
    Then The UserProject response status code should be 200

  @Negative_Scenarioes_for_UserProject_entity
  Scenario: To verify that after PUT request data should get update in DataBase (Which is already deleted)
    When Update UserProject Details
    Then The UserProject response status code should be 200

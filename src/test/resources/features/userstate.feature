Feature: UserState API testing

  @UserState
  Scenario: To verify that after POST new UserState should create
    Given I have a UserState
    When Create New UserState
    Then The UserState response should contain "id"
    Then Validate API ES and DB for UserStateId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The UserState response status code should be 201

  @UserState
  Scenario: To verify that GET request fetched All data
    When Fetch All UserState Details
    Then The UserState response status code should be 200

  @UserState
  Scenario: To verify that GET with ID fetched correct data
    When Fetch UserState Details with ID
    Then The UserState response status code should be 200

  @UserState
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update UserState Details
    Then Validate API ES and DB for UserStateId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The UserState response status code should be 200

  @UserState
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch UserState
    Then Validate API ES and DB for UserStateId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The UserState response status code should be 200

  @UserState
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete UserState with ID
    Then The UserState response status code should be 204

  @Negative_Scenarioes_for_UserState_entity
  Scenario: To verify that GET with ID fetched correct data (Which is already deleted)
    When Fetch UserState Details with ID
    Then The UserState response status code should be 200

  @Negative_Scenarioes_for_UserState_entity
  Scenario: To verify that after PUT request data should get update in DataBase (Which is already deleted)
    When Update UserState Details
    Then The UserState response status code should be 200

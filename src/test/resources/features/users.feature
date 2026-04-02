Feature: User API testing

  @User
  Scenario: To verify that after POST new User should create
    Given I have a User
    When Create New User
    Then The User response should contain "id"
    Then Validate API ES and DB for UserId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The User response status code should be 201

  @User
  Scenario: To verify that GET request fetched All data
    When Fetch All Users Details
    Then The User response status code should be 200

  @User
  Scenario: To verify that GET with ID fetched correct data
    When Fetch User Details with ID
    Then The User response status code should be 200

  @User
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update User Details
    Then Validate API ES and DB for UserId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The User response status code should be 200

  @User
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch User
    Then Validate API ES and DB for UserId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The User response status code should be 200

  @Custom_Queries_For_User
  Scenario: To verify that after GET request FindByRole For User
    When FindByRole For User
    Then The User response status code should be 200

  @User
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete User with ID
    Then The User response status code should be 204

  @Negative_Scenarioes_for_User_entity
  Scenario: To verify that GET with ID fetched correct data (Which is already deleted)
    When Fetch User Details with ID
    Then The User response status code should be 200

  @Negative_Scenarioes_for_User_entity
  Scenario: To verify that after PUT request data should get update in DataBase (Which is already deleted)
    When Update User Details
    Then The User response status code should be 200

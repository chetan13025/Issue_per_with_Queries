Feature: IssueCode API testing

  @IssuCode
  Scenario: To verify that after POST new IssueCode should create
    Given I have a IssueCode
    When Create New IssueCode
    Then The IssueCode response should contain "id"
    Then Validate API ES and DB for IssueCodeId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The IssueCode response status code should be 201

  @IssuCode
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueCode Details
    Then The IssueCode response status code should be 200

  @IssuCode
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch IssueCode Details with ID
    Then The IssueCode response status code should be 200

  @IssuCode
  Scenario: To verify that after PUT request data should get update in Database
    When Update IssueCode
    Then Validate API ES and DB for IssueCodeId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueCode response status code should be 200

  @IssuCode
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch IssueCode
    Then Validate API ES and DB for IssueCodeId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueCode response status code should be 200

  @IssuCode
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete IssueCode with ID
    Then The IssueCode response status code should be 204

  @Negative_scenarioes_for_IssuCode_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch IssueCode Details with ID
    Then The IssueCode response status code should be 200

  @Negative_scenarioes_for_IssuCode_entity
  Scenario: To verify that after PUT request data should get update in Database (Which is  already deleted)
    When Update IssueCode
    Then The IssueCode response status code should be 200

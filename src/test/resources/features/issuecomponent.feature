Feature: IssueComponent API testing

  @IssueComponent
  Scenario: To verify that after POST new IssueComponent should create
    Given I have a IssueComponent
    When Create New IssueComponent
    Then The IssueComponent response should contain "id"
    Then Validate API ES and DB for IssueComponentId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The IssueComponent response status code should be 201

  @IssueComponent
  Scenario: To verify that GET request Fetched all data
    When Fetch All IssueComponent Details
    Then The IssueComponent response status code should be 200

  @IssueComponent
  Scenario: To verify that GET with ID fetched Corect data
    When Fetch IssueComponent Details with ID
    Then The IssueComponent response status code should be 200

  @IssueComponent
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update IssueComponent Details
    Then Validate API ES and DB for IssueComponentId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueComponent response status code should be 200

  @IssueComponent
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch IssueComponent
    Then Validate API ES and DB for IssueComponentId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueComponent response status code should be 200

  @IssueComponent
  Scenario: To verify that after Delete request data should get delete from DataBase
    When Delete IssueComponent with ID
    Then The IssueComponent response status code should be 204

  @Negative_scenarioes_for_IssueComponent_entity
  Scenario: To verify that GET with ID fetched Corect data (Which is  already deleted)
    When Fetch IssueComponent Details with ID
    Then The IssueComponent response status code should be 200

  @Negative_scenarioes_for_IssueComponent_entity
  Scenario: To verify that after PUT request data should get update in DataBase (Which is  already deleted)
    When Update IssueComponent Details
    Then The IssueComponent response status code should be 200

Feature: IssueStep API testing

  @IssueStep
  Scenario: To verify that New after POST IssueStep is creating
    Given I have a IssueStep
    When Create New IssueStep
    Then The IssueStep response should contain "id"
    Then Validate API ES and DB for IssueStepId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The IssueStep response status code should be 201

  @IssueStep
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueStep Details
    Then The IssueStep response status code should be 200

  @IssueStep
  Scenario: To verify that GET with ID fetched correct data
    When Fetch IssueStep Details with ID
    Then The IssueStep response status code should be 200

  @IssueStep
  Scenario: To verify that in PUT request Data is get update in DataBase
    When Update IssueStep Details
    Then Validate API ES and DB for IssueStepId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueStep response status code should be 200

  @IssueStep
  Scenario: To verify that in Patch request data is get update in DataBase
    When Update Patch IssueStep
    Then Validate API ES and DB for IssueStepId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueStep response status code should be 200

  @IssueStep
  Scenario: To verify that after delete request data should get delete from DataBase
    When Delete IssueStep with ID
    Then The IssueStep response status code should be 204

  @Negative_scenarioes_for_IssueStep_entity
  Scenario: To verify that GET with ID fetched correct data (Which is already deleted)
    When Fetch IssueStep Details with ID
    Then The IssueStep response status code should be 200

  @Negative_scenarioes_for_IssueStep_entity
  Scenario: To verify that in PUT request Data is get update in DataBase (Which is already deleted)
    When Update IssueStep Details
    Then The IssueStep response status code should be 200

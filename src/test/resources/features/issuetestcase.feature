Feature: IssueTestCase API testing

  @IssueTestCase
  Scenario: To verify that after POST new IssueTestCase creating
    Given I have a IssueTestCase
    When Create New IssueTestCase
    Then The IssueTestCase response should contain "id"
    Then Validate API ES and DB for IssueTestCaseId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The IssueTestCase response status code should be 201

  @IssueTestCase
  Scenario: To verify that GET request fetched All data
    When Fetch All IssueTestCase Details
    Then The IssueTestCase response status code should be 200

  @IssueTestCase
  Scenario: To verify that GET with ID fetched correct data
    When Fetch IssueTestCase Details with ID
    Then The IssueTestCase response status code should be 200

  @IssueTestCase
  Scenario: To verify that in PUT request Data is geting update in DataBase
    When Update IssueTestCase Details
    Then Validate API ES and DB for IssueTestCaseId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueTestCase response status code should be 200

  @IssueTestCase
  Scenario: To verify that in Patch request data is geting update in DataBase
    When Update Patch IssueTestCase
    Then Validate API ES and DB for IssueTestCaseId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueTestCase response status code should be 200

  @IssueTestCase
  Scenario: To verify that After Delete request data is delete from DataBase
    When Delete IssueTestCase with ID
    Then The IssueTestCase response status code should be 204

  @Negative_Scenarios_for_Behavior_entity
  Scenario: To verify that GET with ID fetched correct data (Which is  already deleted)
    When Fetch IssueTestCase Details with ID
    Then The IssueTestCase response status code should be 200

  @Negative_Scenarios_for_IssueTestCase_entity
  Scenario: To verify that in PUT request Data is geting update in DataBase (Which is  already deleted)
    When Update IssueTestCase Details
    Then Validate API ES and DB for IssueTestCaseId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueTestCase response status code should be 200

Feature: IssueVersion API testing

  @IssueVersion
  Scenario: To Verify that New issueVersion is Creating
    Given I have a IssueVersion
    When Create New IssueVersion
    Then The IssueVersion response should contain "id"
    Then Validate API ES and DB for IssueVersionId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The IssueVersion response status code should be 201

  @IssueVersion
  Scenario: To Verify that GET request Fetched All data
    When Fetch All IssueVersion Details
    Then The IssueVersion response status code should be 200

  @IssueVersion
  Scenario: To Verify that GET with ID Fetched Correct Data
    When Fetch IssueVersion Details with ID
    Then The IssueVersion response status code should be 200

  @IssueVersion
  Scenario: To Verify that after PUT request Data should get update in DataBase
    When Update IssueVersion Details
    Then Validate API ES and DB for IssueVersionId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueVersion response status code should be 200

  @IssueVersion
  Scenario: To Verify that After PATCH request Data should get update in DataBase
    When Update Patch IssueVersion
    Then Validate API ES and DB for IssueVersionId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueVersion response status code should be 200

  @IssueVersion
  Scenario: To Verify that after Delete Data should get Delete from DataBase
    When Delete IssueVersion with ID
    Then The IssueVersion response status code should be 204

  @Negative_Scenarioes_for_IssueVersion_entity
  Scenario: To Verify that GET with ID Fetched Correct Data (Which is already deleted)
    When Fetch IssueVersion Details with ID
    Then The IssueVersion response status code should be 200

  @Negative_Scenarioes_for_IssueVersion_entity
  Scenario: To Verify that after PUT request Data should get update in DataBase (Which is already deleted)
    When Update IssueVersion Details
    Then The IssueVersion response status code should be 200

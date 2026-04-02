Feature: IssueType API testing

  @IssueType
  Scenario: To verify that after POST new IssureType is creating
    Given I have a IssueType
    When Create New IssueType
    Then The IssueType response should contain "id"
    Then Validate API ES and DB for IssueTypeId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The IssueType response status code should be 201

  @IssueType
  Scenario: To verify that GET request fetched All data
    When Fetch All IssueType Details
    Then The IssueType response status code should be 200

  @IssueType
  Scenario: To verify that GET with ID fetched correct Data
    When Fetch IssueType Details with ID
    Then The IssueType response status code should be 200

  @IssueType
  Scenario: To verify that in PUT request data isgetting update in DataBase
    When Update IssueType Details
    Then Validate API ES and DB for IssueTypeId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    When The IssueType response status code should be 200

  @IssueType
  Scenario: To verify that in Patch request data is getting update in DataBase
    When Update Patch IssueType
    Then Validate API ES and DB for IssueTypeId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueType response status code should be 200

  @Custom_Quereies_for_IssueType
  Scenario: To verify that after GET request FindByProjectId
    When FindByProjectId For IssueType
    Then The IssueType response status code should be 200

  @Custom_Quereies_for_IssueType
  Scenario: To verify that after GET request FindByProjectIdandIsDefault
    When FindByProjectIdAndIsDefault For IssueType
    Then The IssueType response status code should be 200

  @IssueType
  Scenario: To verify that After Delete request Data is Deleting from DataBase
    When Delete IssueType with ID
    Then The IssueType response status code should be 204

  @Negative_Scenarios_for_IssueType_entity
  Scenario: To verify that GET with ID fetched correct Data (Which is  already deleted)
    When Fetch IssueType Details with ID
    Then The IssueType response status code should be 200

  @Negative_Scenarios_for_IssueType_entity
  Scenario: To verify that in PUT request data isgetting update in DataBase (Which is  already deleted)
    When Update IssueType Details
    When The IssueType response status code should be 200

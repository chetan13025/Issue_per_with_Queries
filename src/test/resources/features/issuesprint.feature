Feature: IssueSprint API testing

  @IssueSprint
  Scenario: To verify that after POST New IssueSprint should create
    Given I have a IssueSprint
    When Create New IssueSprint
    Then The IssueSprint response should contain "id"
    Then Validate API ES and DB for IssueSprintId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The IssueSprint response status code should be 201

  @IssueSprint
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueSprint Details
    Then The IssueSprint response status code should be 200

  @IssueSprint
  Scenario: To verify that GET with ID Fetched Correct Data
    When Fetch IssueSprint Details with ID
    Then The IssueSprint response status code should be 200

  @IssueSprint
  Scenario: To verify that in PUT data is updating in DataBase
    When Update IssueSprint Details
    Then Validate API ES and DB for IssueSprintId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueSprint response status code should be 200

  @IssueSprint
  Scenario: To verify that in Patch data is updating in DataBase
    When Update Patch IssueSprint
    Then Validate API ES and DB for IssueSprintId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueSprint response status code should be 200

  @Custom_Queries_IssueSprint
  Scenario: To verify that after GET request FindByIssueIdAndSprintId
    When FindByIssueIdAndSprintId
    Then The IssueSprint response status code should be 200

  @IssueSprint
  Scenario: To verify that After Delete Data should delete from DataBase
    When Delete IssueSprint with ID
    Then The IssueSprint response status code should be 204

  @Negative_scenarioes_for_IssueSprint_entity
  Scenario: To verify that GET with ID Fetched Correct Data (Which is  already deleted)
    When Fetch IssueSprint Details with ID
    Then The IssueSprint response status code should be 200

  @Negative_scenarioes_for_IssueSprint_entity
  Scenario: To verify that in PUT data is updating in DataBase (Which is  already deleted)
    When Update IssueSprint Details
    Then The IssueSprint response status code should be 200

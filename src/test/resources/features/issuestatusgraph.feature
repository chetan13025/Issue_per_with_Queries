Feature: IssueStatusGraph API testing

  @IssueStatusGraph
  Scenario: To verify that after POST New IssueStatusGraph is creating
    Given I have a IssueStatusGraph
    When Create New IssueStatusGraph
    Then The IssueStatusGraph response should contain "id"
    Then Validate API ES and DB for IssueSatusGraphId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The IssueStatusGraph response status code should be 201

  @IssueStatusGraph
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueStatusGraph Details
    Then The IssueStatusGraph response status code should be 200

  @IssueStatusGraph
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch IssueStatusGraph Details with ID
    Then The IssueStatusGraph response status code should be 200

  @IssueStatusGraph
  Scenario: To verify that in PUT data should get Update in DateBase
    When Update IssueStatusGraph Details
    Then Validate API ES and DB for IssueSatusGraphId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueStatusGraph response status code should be 200

  @IssueStatusGraph
  Scenario: To verify that in Patch data should get update in DataBase
    When Update Patch IssueStatusGraph
    Then Validate API ES and DB for IssueSatusGraphId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueStatusGraph response status code should be 200

  @IssueStatusGraph
  Scenario: To verify that after Delete data should get delete from DataBase
    When Delete IssueStatusGraph with ID
    Then The IssueStatusGraph response status code should be 204

  @Negative_scenarioes_for_IssueStatusGraph_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch IssueStatusGraph Details with ID
    Then The IssueStatusGraph response status code should be 200

  @Negative_scenarioes_for_IssueStatusGraph_entity
  Scenario: To verify that in PUT data should get Update in DateBase (Which is  already deleted)
    When Update IssueStatusGraph Details
    Then The IssueStatusGraph response status code should be 200

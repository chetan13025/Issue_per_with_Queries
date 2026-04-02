Feature: IssueFeatureCatalog API testing

  @IssueFeatureCatalog
  Scenario: To verify that after POST request  New IssueFeatureCatalog should create
    Given I have a IssueFeatureCatalog
    When Create New IssueFeatureCatalog
    Then The IssueFeatureCatalog response should contain "id"
    Then Validate API ES and DB for IssueFeatureCatalogId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The IssueFeatureCatalog response status code should be 201

  @IssueFeatureCatalog
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueFeatureCatalog Details
    Then The IssueFeatureCatalog response status code should be 200

  @IssueFeatureCatalog
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch IssueFeatureCatalog Details with ID
    Then The IssueFeatureCatalog response status code should be 200

  @IssueFeatureCatalog
  Scenario: To verify that after PUT request data should get update in Database
    When Update IssueFeatureCatalog Details
    Then Validate API ES and DB for IssueFeatureCatalogId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueFeatureCatalog response status code should be 200

  @IssueFeatureCatalog
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch IssueFeatureCatalog
    Then Validate API ES and DB for IssueFeatureCatalogId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueFeatureCatalog response status code should be 200

  @Custom_Queries_for_IssueFeatureCatalog_entity
  Scenario: To verify that after GET request FindByRuleName
    When FindByRuleName for IssueFeatureCatalog
    Then The IssueFeatureCatalog response status code should be 200

  @Custom_Queries_for_IssueFeatureCatalog_entity
  Scenario: To verify that after GET request FindByProjectID
    When FindByProjectId for IssueFeatureCatalog
    Then The IssueFeatureCatalog response status code should be 200

  @IssueFeatureCatalog
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete IssueFeatureCatalog with ID
    Then The IssueFeatureCatalog response status code should be 204

  @Negative_scenarioes_for_IssueFeatureCatalog_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch IssueFeatureCatalog Details with ID
    Then The IssueFeatureCatalog response status code should be 200

  @Negative_scenarioes_for_IssueFeatureCatalog_entity
  Scenario: To verify that after PUT request data should get update in Database (Which is  already deleted)
    When Update IssueFeatureCatalog Details
    Then The IssueFeatureCatalog response status code should be 200

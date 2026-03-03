Feature: IssueFeatureGroup API testing

  @IssueFeatureGroup
  Scenario: To verify that after POST request  New IssueFeatureGroup should create
    Given I have a IssueFeatureGroup
    When Create New IssueFeatureGroup
    Then The IssueFeatureGroup response should contain "id"
    Then Validate API ES and DB for IsueFeatureGroupId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The IssueFeatureGroup response status code should be 201

  @IssueFeatureGroup
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueFeatureGroup Details
    Then The IssueFeatureGroup response status code should be 200

  @IssueFeatureGroup
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch IssueFeatureGroup Details with ID
    Then The IssueFeatureGroup response status code should be 200

  @IssueFeatureGroup
  Scenario: To verify that after PUT request data should get update in Database
    When Update IssueFeatureGroup Details
    Then Validate API ES and DB for IsueFeatureGroupId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueFeatureGroup response status code should be 200

  @IssueFeatureGroup
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch IssueFeatureGroup
    Then Validate API ES and DB for IsueFeatureGroupId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueFeatureGroup response status code should be 200

  @IssueFeatureGroup
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete IssueFeatureGroup with ID
    Then The IssueFeatureGroup response status code should be 204

  @Negative_scenarioes_for_IssueFeatureGroup_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch IssueFeatureGroup Details with ID
    Then The IssueFeatureGroup response status code should be 200

  @Negative_scenarioes_for_IssueFeatureGroup_entity
  Scenario: To verify that after PUT request data should get update in Database (Which is  already deleted)
    When Update IssueFeatureGroup Details
    Then The IssueFeatureGroup response status code should be 200

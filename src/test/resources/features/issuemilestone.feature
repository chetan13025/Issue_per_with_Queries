Feature: IssueMilestone API testing

  @IssueMilestone
  Scenario: To verify that after POST request  New IssueMilestone should create
    Given I have a IssueMilestone
    When Create New IssueMilestone
    Then The IssueMilestone response should contain "id"
    Then Validate API ES and DB for IssueMilestoneId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    And The IssueMilestone response status code should be 201

  @IssueMilestone
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueMilestone Details
    Then The IssueMilestone response status code should be 200

  @IssueMilestone
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch IssueMilestone Details with ID
    Then The IssueMilestone response status code should be 200

  @IssueMilestone
  Scenario: To verify that after PUT request data should get update in Database
    When Update IssueMilestone Details
    Then Validate API ES and DB for IssueMilestoneId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueMilestone response status code should be 200

  @IssueMilestone
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch IssueMilestone
    Then Validate API ES and DB for IssueMilestoneId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
    Then The IssueMilestone response status code should be 200

  @Custom_Quereies_issueMilestone
  Scenario: To verify that after GET request FindByProjectId
    When FindByProjectId for IssueMilestone
    Then The IssueMilestone response status code should be 200

  @IssueMilestone
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete IssueMilestone with ID
    Then The IssueMilestone response status code should be 204

  @Negative_scenarioes_for_IssueMilestone_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch IssueMilestone Details with ID
    Then The IssueMilestone response status code should be 200

  @Negative_scenarioes_for_IssueMilestone_entity
  Scenario: To verify that after PUT request data should get update in Database (Which is  already deleted)
    When Update IssueMilestone Details
    Then The IssueMilestone response status code should be 200

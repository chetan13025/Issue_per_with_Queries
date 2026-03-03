Feature: IssueLinked API testing

  @IssueLinked
  Scenario: To verify that after POST request  New IssueLinked should create
    Given I have a IssueLinked
    When Create New IssueLinked
    Then The IssueLinked response should contain "id"
    Then Validate API ES and DB for IssueLinkedId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,linkeddate" excludeFromEs "deleted"
    And The IssueLinked response status code should be 201

  @IssueLinked
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueLinked Details
    Then The IssueLinked response status code should be 200

  @IssueLinked
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch IssueLinked Details with ID
    Then The IssueLinked response status code should be 200

  @IssueLinked
  Scenario: To verify that after PUT request data should get update in Database
    When Update IssueLinked Details
    Then Validate API ES and DB for IssueLinkedId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,linkeddate" excludeFromEs "deleted"
    Then The IssueLinked response status code should be 200

  @IssueLinked
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch IssueLinked
    Then Validate API ES and DB for IssueLinkedId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,linkeddate" excludeFromEs "deleted"
    Then The IssueLinked response status code should be 200

  @IssueLinked
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete IssueLinked with ID
    Then The IssueLinked response status code should be 204

  @Negative_scenarioes_for_IssueLinked_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch IssueLinked Details with ID
    Then The IssueLinked response status code should be 200

  @Negative_scenarioes_for_IssueLinked_entity
  Scenario: To verify that after PUT request data should get update in Database (Which is  already deleted)
    When Update IssueLinked Details
    Then The IssueLinked response status code should be 200

Feature: IssueCollaborator API testing

  @IssueCollaborator
  Scenario: To verify that after POST request  New IssueCollaborator should create
    Given I have a IssueCollaborator
    When Create New IssueCollaborator
    Then The IssueCollaborator response should contain "id"
    Then Validate API ES and DB for IssueCollaboratorId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
    And The IssueCollaborator response status code should be 201

  @IssueCollaborator
  Scenario: To verify GET request that Fetched all data
    When Fetch All IssueCollaborator Details
    Then The IssueCollaborator response status code should be 200

  @IssueCollaborator
  Scenario: To verify that GET with ID fetched Correct data
    When Fetch IssueCollaborator Details with ID
    Then The IssueCollaborator response status code should be 200

  @IssueCollaborator
  Scenario: To verify that after PUT request data should get update in Database
    When Update IssueCollaborator Details
    Then Validate API ES and DB for IssueCollaboratorId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
    Then The IssueCollaborator response status code should be 200

  @IssueCollaborator
  Scenario: To verify that after Patch request data should get update in DataBase
    When Update Patch IssueCollaborator
    Then Validate API ES and DB for IssueCollaboratorId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
    Then The IssueCollaborator response status code should be 200

  @IssueCollaborator
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete IssueCollaborator with ID
    Then The IssueCollaborator response status code should be 204

  @Negative_scenarioes_for_IssueCollaborator_entity
  Scenario: To verify that GET with ID fetched Correct data (Which is  already deleted)
    When Fetch IssueCollaborator Details with ID
    Then The IssueCollaborator response status code should be 200

  @Negative_scenarioes_for_IssueCollaborator_entity
  Scenario: To verify that after PUT request data should get update in Database (Which is  already deleted)
    When Update IssueCollaborator Details
    Then The IssueCollaborator response status code should be 200

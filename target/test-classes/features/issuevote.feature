Feature: IssueVote API testing

  @IssueVote
  Scenario: To Verify that POST New IssueVote is creating
    Given I have a IssueVote
    When Create New IssueVote
    Then The IssueVote response should contain "id"
    Then Validate API ES and DB for IssueVoteId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
    And The IssueVote response status code should be 201

  @IssueVote
  Scenario: To verify that GET request Fetched all data
    When Fetch All IssueVote Details
    Then The IssueVote response status code should be 200

  @IssueVote
  Scenario: To verify that GET with ID Fetched Correct Data
    When Fetch IssueVote Details with ID
    Then The IssueVote response status code should be 200

  @IssueVote
  Scenario: To verify that after PUT request Data should get update in Data Base
    When Update IssueVote Details
    Then Validate API ES and DB for IssueVoteId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
    Then The IssueVote response status code should be 200

  @IssueVote
  Scenario: To verify that after PATCH request Data should get udpate in DataBase
    When Update Patch IssueVote
    Then Validate API ES and DB for IssueVoteId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted,createdate,updatedate" excludeFromEs "deleted"
    Then The IssueVote response status code should be 200

  @IssueVote
  Scenario: TO verify that After Delete request Data should get delete in DataBAse
    When Delete IssueVote with ID
    Then The IssueVote response status code should be 204

  @Negative_Scenarioes_for_IssueVote_entity
  Scenario: To verify that GET with ID Fetched Correct Data (Which is already deleted)
    When Fetch IssueVote Details with ID
    Then The IssueVote response status code should be 200

  @Negative_Scenarioes_for_IssueVote_entity
  Scenario: To verify that after PUT request Data should get update in Data Base (Which is already deleted)
    When Update IssueVote Details
    Then The IssueVote response status code should be 200
